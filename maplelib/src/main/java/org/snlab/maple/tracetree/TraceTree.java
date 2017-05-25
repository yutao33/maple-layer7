/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;


import com.google.common.base.Preconditions;
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.packet.OutPutPacket;
import org.snlab.maple.packet.parser.Ethernet;
import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.MapleMatch;
import org.snlab.maple.rule.match.MapleMatchInPort;
import org.snlab.maple.rule.route.Forward;
import org.snlab.maple.rule.route.ForwardAction;
import org.snlab.maple.rule.route.Route;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * TraceTree.
 */
public class TraceTree {

    private TraceTreeNode treeroot;

    public TraceTreeNode getTreeRoot() {
        return treeroot;
    }

    //-------------------------------update-----------------------------

    private Map<MapleMatchField, MapleMatch> matchMap = new EnumMap<>(MapleMatchField.class);

    private Map<MapleMatchField, Set<MapleMatch>> unmatchMap = new EnumMap<>(MapleMatchField.class);//TODO for generate rules

    private List<MapleRule> deleteRules = new ArrayList<>();

    public void update(List<Trace.TraceItem> items, MaplePacket pkt) {
        matchMap.clear();
        unmatchMap.clear();

        List<Forward> route = pkt.getRoute();
        if (route == null || route.isEmpty()) {
            route = Collections.singletonList(new Forward(null, ForwardAction.drop()));
        }
        if (items.isEmpty()) {
            treeroot = testifLNodeexpected_ornew(treeroot, route, pkt);
            return;
        }
        Trace.TraceItem item0 = items.get(0);
        treeroot = testifexpected_ornew(treeroot, item0);

        TraceTreeNode nodep = treeroot;

        Trace.TraceItem preitem = items.get(0);
        for (int i = 0; i < items.size(); i++) {
            if (nodep instanceof TraceTreeTNode) {
                Trace.TestItem ti = (Trace.TestItem) preitem;
                TraceTreeTNode t = (TraceTreeTNode) nodep;
                boolean testresult = ti.getresult();

                MapleMatchField matchfield = t.getField();

                TraceTreeNode oldtestbranch;// = t.getBranch(testresult);

                TraceTreeTNode.TNodeEntry trueentry = null;
                if (testresult) {
                    Map.Entry<MapleMatch, TraceTreeTNode.TNodeEntry> mapEntry = t.findEntry(ti.getPktValue());
                    Preconditions.checkState(mapEntry != null);
                    trueentry = mapEntry.getValue();
                    oldtestbranch = trueentry.child;
                    matchMap.put(matchfield, mapEntry.getKey());
                } else {
                    oldtestbranch = t.getBranchFalse();
                    Set<MapleMatch> unmatchset = unmatchMap.get(matchfield);
                    if (unmatchset == null) {
                        unmatchset = new HashSet<>();
                        unmatchMap.put(matchfield, unmatchset);
                    }
                    unmatchset.addAll(t.getBranchTrueMap().keySet());
                }


                if (i == items.size() - 1) {
                    nodep = testifLNodeexpected_ornew(oldtestbranch, route, pkt);
                } else {
                    TraceTreeNode ttn = testifexpected_ornew(oldtestbranch, items.get(i + 1));
                    if (ttn == null) {
                        continue;
                    } else {
                        nodep = ttn;
                        preitem = items.get(i + 1);
                    }
                }


                if (nodep != oldtestbranch) {
                    if (testresult) {
                        trueentry.child = nodep;
                    } else {
                        t.setBranchFalse(nodep);
                    }
                }


                if (!testresult) {
                    t.genBarrierRule(matchMap);
                }

            } else if (nodep instanceof TraceTreeVNode) {
                Trace.TraceGet ti = (Trace.TraceGet) preitem;
                TraceTreeVNode v = (TraceTreeVNode) nodep;
                TraceTreeVNode.VNodeEntry oldentry = v.getEntryOrConstruct(ti,matchMap);

                matchMap.put(ti.getField(), oldentry.match);

                TraceTreeNode oldchild = oldentry.child;
                if (i == items.size() - 1) {
                    nodep = testifLNodeexpected_ornew(oldchild, route, pkt);
                } else {
                    TraceTreeNode ttn = testifexpected_ornew(oldchild, items.get(i + 1));
                    if (ttn == null) {
                        continue;
                    } else {
                        nodep = ttn;
                        preitem = items.get(i + 1);
                    }
                }
                if (nodep != oldchild) {
                    oldentry.child = nodep;
                }
            } else {
                throw new RuntimeException("impossible");
            }
        }
    }

    @Nullable
    private TraceTreeNode testifexpected_ornew(@Nullable TraceTreeNode node, @Nonnull Trace.TraceItem item) {
        if (node != null && node.isConsistentWith(item)) {
            return node;  //NOTE no need to update
        }

        TraceTreeNode ret = null;

        if (item instanceof Trace.TestItem) {

            Trace.TestItem ti = (Trace.TestItem) item;
            ret = TraceTreeTNode.buildNodeIfNeedOrNull(ti, matchMap); //NOTE will generate match

        } else if (item instanceof Trace.TraceGet) {
            Trace.TraceGet ti = (Trace.TraceGet) item;
            ret = TraceTreeVNode.buildNodeIfNeedOrNull(ti, matchMap);
        } else {
            throw new RuntimeException("impossible");
        }

        if(ret != null){
            recurseMarkDeleted(node);
        }
        return ret;
    }

    private TraceTreeNode testifLNodeexpected_ornew(@Nullable TraceTreeNode node, List<Forward> route, MaplePacket pkt) {
        if (node instanceof TraceTreeLNode) {
            TraceTreeLNode l = (TraceTreeLNode) node;
            if (l.getRoute().equals(route)) {
                // NOTE generate drop rule if route is drop
                handleIfNeedDrop(l, pkt);
                holdLNodeRule(l);
                l.pktTrack(pkt);
                return node;
            }
        }
        recurseMarkDeleted(node);
        TraceTreeLNode lNode = TraceTreeLNode.build(route,pkt, matchMap);
        // NOTE generate drop rule if route is drop
        handleIfNeedDrop(lNode, pkt);
        holdLNodeRule(lNode);
        return lNode;
    }

    private void handleIfNeedDrop(TraceTreeLNode l, MaplePacket pkt) {
        Route route = l.getRule().getRoute();
        route.updateDropIfneed(pkt._getInPortId());
    }


    private void recurseMarkDeleted(@Nullable TraceTreeNode node) {
        if (node == null) {
            return;
        }
        if (node instanceof TraceTreeLNode) {
            TraceTreeLNode l = (TraceTreeLNode) node;
            MapleRule lrule = l.getRule();
            MapleRule.Status status = lrule.getStatus();
            if(status.equals(MapleRule.Status.INSTALLED)) {
                lrule.setStatus(MapleRule.Status.DELETE);
                this.deleteRules.add(lrule);
            } else {
                lrule.setStatus(MapleRule.Status.DELETED);
            }
            l.removePktTrack();
        } else if (node instanceof TraceTreeTNode) {
            TraceTreeTNode t = (TraceTreeTNode) node;
            recurseMarkDeleted(t.getBranchFalse());
            Map<MapleMatch, TraceTreeTNode.TNodeEntry> bm = t.getBranchTrueMap();
            for (TraceTreeTNode.TNodeEntry te : bm.values()) {
                if (te.barrierRule != null) {
                    MapleRule.Status status = te.barrierRule.getStatus();
                    if(status.equals(MapleRule.Status.INSTALLED)) {
                        te.barrierRule.setStatus(MapleRule.Status.DELETE);
                        this.deleteRules.add(te.barrierRule);
                    } else {
                        te.barrierRule.setStatus(MapleRule.Status.DELETED);
                    }
                    recurseMarkDeleted(te.child);
                }
            }

        } else if (node instanceof TraceTreeVNode) {
            TraceTreeVNode v = (TraceTreeVNode) node;
            Iterator<TraceTreeNode> iter = v.iterator();
            while (iter.hasNext()) {
                TraceTreeNode next = iter.next();
                recurseMarkDeleted(next);
            }
        } else {
            throw new RuntimeException("impossible");
        }
    }

    //------------------derive packets at LNode------------------------------

    private MapleRule updatedLNodeRule;

    private void holdLNodeRule(TraceTreeLNode lNode){  //FIXME
        updatedLNodeRule =  lNode.getRule();
    }

    public Object[] derivePackets(MapleTopology topo, MaplePacket pkt){  //FIXME bad code
        List<MaplePacket> genpkts = new ArrayList<>();
        List<OutPutPacket> outputpkts = new ArrayList<>();
        recursegenpkts(outputpkts,topo.findPort(pkt._getInPortId()),pkt._getFrame());
        return new Object[]{outputpkts};
    }

    private void recursegenpkts(List<OutPutPacket> outputpkts, MapleTopology.Port inport, Ethernet ethernet) {
        if(inport==null){
            return;
        }
        MapleTopology.Node mynode = inport.getOwner();
        MapleTopology.NodeId mynodeId = mynode.getId();
        Map<MapleTopology.NodeId, Map<MapleTopology.PortId, Forward>> rulesMap = updatedLNodeRule.getRoute().getRulesMap();
        Map<MapleTopology.PortId, Forward> portForwardMap = rulesMap.get(mynodeId);
        if(portForwardMap==null){
            portForwardMap = rulesMap.get(null);
        }
        if(portForwardMap==null){
            return;
        }
        Forward forward = portForwardMap.get(inport.getId());
        if(forward==null){
            forward = portForwardMap.get(null);
        }
        if(forward==null){
            return;
        }
        List<MapleTopology.PortId> portIdList = new ArrayList<>();
        byte[] holddata = ethernet.serialize();
        List<ForwardAction.Action> actions = forward.getActions();
        for (ForwardAction.Action action : actions) {
            if(action instanceof ForwardAction.OutPut){
                MapleTopology.PortId outportId = ((ForwardAction.OutPut) action).getPortId();
                if(outportId.equals(inport.getId())){
                    continue;
                }
                MapleTopology.Port topoport = null;
                for (MapleTopology.Port p1 : mynode.getPorts()) {
                    if(p1.getId().equals(outportId)){
                        topoport=p1;
                        break;
                    }
                }
                if(topoport!=null){
                    MapleTopology.Link topolink = topoport.getLink();
                    if(topolink==null){
                        portIdList.add(outportId);
                    } else {
                        MapleTopology.Port endport = topolink.getEnd();
                        if(endportMatch(endport)){
                            Ethernet ethernet1=new Ethernet();
                            ethernet1.deserialize(holddata,0,holddata.length);//FIXME
                            recursegenpkts(outputpkts,endport,ethernet1);
                        }
                    }
                }
            } else if(action instanceof ForwardAction.SetField){
                //FIXME
            } else if(action instanceof ForwardAction.Drop){
                //nop
            } else if(action instanceof ForwardAction.Punt){
                //nop
            } else {
                throw new Error("type error");
            }
        }
        if(portIdList.size()>0) {
            outputpkts.add(new OutPutPacket(mynodeId, portIdList, holddata));
        }
    }

    private boolean endportMatch(MapleTopology.Port endport) {
        MapleTopology.PortId portId = endport.getId();
        MapleMatchInPort match = (MapleMatchInPort)matchMap.get(MapleMatchField.INPORT);
        if(match==null){
            Set<MapleMatch> unmatches = unmatchMap.get(MapleMatchField.INPORT);
            if(unmatches!=null) {
                for (MapleMatch unmatch : unmatches) {
                    MapleMatchInPort unmatchport = (MapleMatchInPort) unmatch;
                    if (unmatchport.testMatch(portId)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return match.testMatch(portId);
    }


    //-------------------------------generateRules-----------------------------

    private List<MapleRule> incrementRules;

    private List<MapleRule> allRules;
    private int globalpriority;

    public List<MapleRule> generateRules() {

        incrementRules = new ArrayList<>(deleteRules);

        allRules = new ArrayList<>();

        deleteRules.clear();

        globalpriority = 1;
        recurseUpdatePriority(treeroot);

//        if(incrementRules.size()>0){
//            return allRules;
//        }
        return incrementRules;
    }

    private void recurseUpdatePriority(TraceTreeNode node) {
        if (node == null) {
            return;
        }
        if (node instanceof TraceTreeLNode) {
            node.priority = globalpriority;
            MapleRule rule = ((TraceTreeLNode) node).getRule();
            MapleRule.Status ruleStatus = rule.getStatus();
            if(ruleStatus.equals(MapleRule.Status.NONE)){
                rule.setStatus(MapleRule.Status.INSTALL);
                rule.setPriority(globalpriority);
                incrementRules.add(rule);
            } else if(ruleStatus.equals(MapleRule.Status.INSTALLED)){
                int oldPri = rule.getPriority();
                if(oldPri!=globalpriority){
                    rule.setStatus(MapleRule.Status.UPDATE);
                    rule.setPriority(globalpriority);
                    incrementRules.add(rule);
                }
            } else {
                throw new RuntimeException("update priority error");
            }
            allRules.add(rule);
        } else if (node instanceof TraceTreeTNode) {
            TraceTreeTNode t = (TraceTreeTNode) node;
            TraceTreeNode branchfalse = t.getBranchFalse();
            if (branchfalse != null) {
                recurseUpdatePriority(branchfalse);
                globalpriority++;
            }
            int barrierpri = globalpriority;
            Map<MapleMatch, TraceTreeTNode.TNodeEntry> bm = t.getBranchTrueMap();
            for (TraceTreeTNode.TNodeEntry te : bm.values()) {
                globalpriority = barrierpri;
                if (te.barrierRule != null) {
                    MapleRule.Status status = te.barrierRule.getStatus();
                    if(status.equals(MapleRule.Status.NONE)){
                        te.barrierRule.setPriority(barrierpri);
                        te.barrierRule.setStatus(MapleRule.Status.INSTALL);
                        incrementRules.add(te.barrierRule);
                    } else if(status.equals(MapleRule.Status.INSTALLED)){
                        int oldPri = te.barrierRule.getPriority();
                        if(oldPri!=barrierpri){
                            te.barrierRule.setPriority(barrierpri);
                            te.barrierRule.setStatus(MapleRule.Status.UPDATE);
                            incrementRules.add(te.barrierRule);
                        }
                    } else {
                        throw new RuntimeException("update priority error1");
                    }
                    allRules.add(te.barrierRule);
                    globalpriority++;
                }
                recurseUpdatePriority(te.child);
            }
        } else if (node instanceof TraceTreeVNode) {
            TraceTreeVNode v = (TraceTreeVNode) node;

            Iterator<TraceTreeNode> iter = v.iterator();
            while (iter.hasNext()) {
                TraceTreeNode next = iter.next();
                recurseUpdatePriority(next);
            }
        } else {
            throw new RuntimeException("unexpected node type 1");
        }
    }

}
