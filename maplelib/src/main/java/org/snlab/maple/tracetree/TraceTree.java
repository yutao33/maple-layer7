/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;


import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.ByteArray;
import org.snlab.maple.rule.match.MapleMatch;
import org.snlab.maple.rule.match.ValueMaskPair;
import org.snlab.maple.rule.route.Forward;
import org.snlab.maple.rule.route.ForwardAction;

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


    //-------------------------------update-----------------------------

    private Map<MapleMatchField,MapleMatch> matchMap=new EnumMap<>(MapleMatchField.class);

    private Map<MapleMatchField,Set<ValueMaskPair>> unmatchMap=new EnumMap<>(MapleMatchField.class);//TODO for generate rules

    public synchronized void update(List<Trace.TraceItem> items, MaplePacket pkt) {
        List<Forward> route = pkt.getRoute();
        if (route==null||route.isEmpty()) {
            route= Collections.singletonList(new Forward(null, ForwardAction.drop()));
        }
        if (items.isEmpty()) {
            treeroot = testifLNodeexpected_ornew(treeroot, route);
            return;
        }
        Trace.TraceItem item0 = items.get(0);
        treeroot = testifexpected_ornew(treeroot, item0);

        TraceTreeNode nodep = treeroot;

        Trace.TraceItem preitem=items.get(0);
        for (int i = 0; i < items.size(); i++) {
            if (nodep instanceof TraceTreeTNode) {
                Trace.TestItem ti = (Trace.TestItem) preitem;
                TraceTreeTNode t = (TraceTreeTNode) nodep;
                boolean testresult = ti.getresult();
                TraceTreeNode oldtestbranch = t.getBranch(testresult);

                MapleMatch match = t.getMatch();
                MapleMatchField matchfield = match.getField();
                if(testresult) {
                    matchMap.put(matchfield, match);
                } else {
                    Set<ValueMaskPair> unmatchset = unmatchMap.get(matchfield);
                    if(unmatchset==null){
                        unmatchset=new HashSet<>(match.getMatchSet());
                        unmatchMap.put(matchfield,unmatchset);
                    } else {
                        unmatchset.addAll(match.getMatchSet());
                    }
                }

                if (i == items.size() - 1) {
                    nodep = testifLNodeexpected_ornew(oldtestbranch, route);
                } else {
                    TraceTreeNode ttn = testifexpected_ornew(oldtestbranch, items.get(i + 1));
                    if(ttn==null){
                        continue;
                    } else {
                        nodep=ttn;
                        preitem=items.get(i+1);
                    }
                }
                if(nodep!=oldtestbranch) {
                    t.setBranch(testresult, nodep);
                }
                if(t.getBranch(false)!=null&&t.getBarrierRule()==null){  //NOTE testresult=false at this time
                    t.genBarrierRule(matchMap);
                }
            } else if (nodep instanceof TraceTreeVNode) {
                Trace.TraceGet ti = (Trace.TraceGet) preitem;
                TraceTreeVNode v = (TraceTreeVNode) nodep;
                ByteArray value = ti.getValue();
                TraceTreeVNode.VNodeEntry oldentry = v.getEntryOrConstruct(value);

                matchMap.put(oldentry.match.getField(),oldentry.match);

                TraceTreeNode oldchild=oldentry.child;
                if (i == items.size() - 1) {
                    nodep = testifLNodeexpected_ornew(oldchild, route);
                } else {
                    TraceTreeNode ttn = testifexpected_ornew(oldchild, items.get(i + 1));
                    if(ttn==null){
                        continue;
                    } else {
                        nodep=ttn;
                        preitem=items.get(i+1);
                    }
                }
                if(nodep!=oldchild) {
                    oldentry.child = nodep;
                }
            } else {
                throw new RuntimeException("impossible");
            }
        }
    }

    @Nullable
    private TraceTreeNode testifexpected_ornew(@Nullable TraceTreeNode node, @Nonnull Trace.TraceItem item) {
        if(node!=null&&node.isConsistentWith(item)){
            return node;  //NOTE no need to update
        }

        recurseMarkDeleted(node);

        if (item instanceof Trace.TestItem) {

            Trace.TestItem ti = (Trace.TestItem) item;
            return TraceTreeTNode.buildNodeIfNeedOrNull(ti,matchMap); //NOTE will generate match

        } else if (item instanceof Trace.TraceGet) {
            Trace.TraceGet ti=(Trace.TraceGet)item;
            return TraceTreeVNode.buildNodeIfNeedOrNull(ti,matchMap);
        } else {
            throw new RuntimeException("impossible");
        }
    }

    private TraceTreeNode testifLNodeexpected_ornew(@Nullable TraceTreeNode node, List<Forward> route) {
        if (node instanceof TraceTreeLNode) {
            TraceTreeLNode l = (TraceTreeLNode) node;
            if(l.getRoute().equals(route)){
                //TODO generate tmp drop rule if route is null
                return node;
            }
        }
        recurseMarkDeleted(node);
        return TraceTreeLNode.build(route,matchMap);
    }

    private void recurseMarkDeleted(@Nullable TraceTreeNode node) {
        if (node == null) {
            return;
        }
        if (node instanceof TraceTreeLNode) {
            ((TraceTreeLNode) node).getRule().setIsDeleted(true);
        } else if (node instanceof TraceTreeTNode) {
            TraceTreeTNode t = (TraceTreeTNode) node;
            recurseMarkDeleted(t.getBranch(false));
            if(t.getBarrierRule()!=null){
                t.getBarrierRule().setIsDeleted(true);
            }
            recurseMarkDeleted(t.getBranch(true));
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


    //-------------------------------generateRules-----------------------------

    private List<MapleRule> rules;
    private int globalpriority;

    public synchronized List<MapleRule> generateRules() {
        rules=new ArrayList<>();
        globalpriority=1;
        recurseUpdatePriority(treeroot);
        return rules;
    }

    private void recurseUpdatePriority(TraceTreeNode node){
        if(node==null){
            return;
        }
        if(node instanceof TraceTreeLNode){
            node.priority=globalpriority;
            MapleRule rule = ((TraceTreeLNode) node).getRule();
            rule.setPriority(globalpriority);
            rules.add(rule);
        } else if(node instanceof TraceTreeTNode){
            TraceTreeTNode t=(TraceTreeTNode)node;
            TraceTreeNode branchfalse = t.getBranch(false);
            if(branchfalse!=null) {
                recurseUpdatePriority(branchfalse);
                globalpriority++;
                MapleRule barrierRule = t.getBarrierRule();
                barrierRule.setPriority(globalpriority);
                rules.add(barrierRule);
                globalpriority++;
            }
            recurseUpdatePriority(t.getBranch(true));
        } else if(node instanceof TraceTreeVNode){
            TraceTreeVNode v=(TraceTreeVNode)node;

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
