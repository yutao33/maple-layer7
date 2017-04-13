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
import org.snlab.maple.rule.route.Forward;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * TraceTree.
 */
public class TraceTree {

    private TraceTreeNode treeroot;


    //-------------------------------update-----------------------------

    private Map<MapleMatchField,MapleMatch> matchMap=new EnumMap<>(MapleMatchField.class);

    public synchronized void update(List<Trace.TraceItem> items, MaplePacket pkt) {
        List<Forward> route = pkt.getRoute();
        if (route==null||route.isEmpty()) {
            //TODO find and delete
            return;
        }
        if (items.isEmpty()) {
            treeroot = testifLNodeexpected_ornew(treeroot, route);
            return;
        }
        Trace.TraceItem item0 = items.get(0);
        treeroot = testifexpected_ornew(treeroot, item0);

        TraceTreeNode nodep = treeroot;

        for (int i = 0; i < items.size(); i++) {
            if (nodep instanceof TraceTreeTNode) {
                Trace.TestItem ti = (Trace.TestItem) items.get(i);
                TraceTreeTNode t = (TraceTreeTNode) nodep;
                boolean testresult = ti.getresult();
                TraceTreeNode odltestbranch = t.getBranch(testresult);

                if(testresult) {
                    MapleMatch match = t.getMatch();
                    matchMap.put(match.getField(), match);
                }

                if (i == items.size() - 1) {
                    nodep = testifLNodeexpected_ornew(odltestbranch, route);
                } else {
                    nodep = testifexpected_ornew(odltestbranch, items.get(i + 1));
                }
                if(nodep!=odltestbranch) {
                    t.setBranch(testresult, nodep);
                }
                if(!testresult&&t.getBranch(true)==null){
                    t.setBranch(true,barrierRuleLNode());
                }
            } else if (nodep instanceof TraceTreeVNode) {
                Trace.TraceGet ti = (Trace.TraceGet) items.get(i);
                TraceTreeVNode v = (TraceTreeVNode) nodep;
                ByteArray value = ti.getValue();
                TraceTreeVNode.VNodeEntry odlentry = v.getEntryOrConstruct(value);

                matchMap.put(odlentry.match.getField(),odlentry.match);

                TraceTreeNode oldchild=odlentry.child;
                if (i == items.size() - 1) {
                    nodep = testifLNodeexpected_ornew(oldchild, route);
                } else {
                    nodep = testifexpected_ornew(oldchild, items.get(i + 1));
                }
                if(nodep!=oldchild) {
                    odlentry.child = nodep;
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
            TraceTreeVNode v = new TraceTreeVNode(ti.getField(),ti.getMask());
            //TODO v.entry.match
            return v;
        } else {
            throw new RuntimeException("impossible");
        }
    }

    private TraceTreeNode testifLNodeexpected_ornew(@Nullable TraceTreeNode node, List<Forward> route) {
        TraceTreeNode ret = null;
        if (node instanceof TraceTreeLNode) {
            TraceTreeLNode l = (TraceTreeLNode) node;
            if(l.getRoute().equals(route)){
                //TODO generate tmp drop rule if route is null
            } else {
                //l.route = route;
                //l.rule=new MapleRule(matchMap,route); //NOTE new rule
            }
            ret = node;
        } else {
            recurseMarkDeleted(node);
            ret = new TraceTreeLNode(route);
        }
        return ret;
    }

    private TraceTreeNode barrierRuleLNode(){
        TraceTreeLNode l = new TraceTreeLNode(Forward.DEFAULT_PuntForwards);
        return l;
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
        if(node instanceof TraceTreeLNode){
            node.priority=globalpriority;
            rules.add(((TraceTreeLNode)node).getRule());
        } else if(node instanceof TraceTreeTNode){
            TraceTreeTNode t=(TraceTreeTNode)node;
            recurseUpdatePriority(t.getBranch(false));
            globalpriority++;
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
