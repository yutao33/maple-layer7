/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;


import com.google.common.base.Preconditions;
import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.ByteArray;
import org.snlab.maple.rule.match.MapleMatch;
import org.snlab.maple.rule.match.ValueMaskPair;
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


    //-------------------------------update-----------------------------

    private Map<MapleMatchField, MapleMatch> matchMap = new EnumMap<>(MapleMatchField.class);

    private Map<MapleMatchField, Set<ValueMaskPair>> unmatchMap = new EnumMap<>(MapleMatchField.class);//TODO for generate rules

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
                    Set<ValueMaskPair> unmatchset = unmatchMap.get(matchfield);
                    if (unmatchset == null) {
                        unmatchset = new HashSet<>();
                        unmatchMap.put(matchfield, unmatchset);
                    }
                    for (MapleMatch mmatch : t.getBranchtrueMap().keySet()) {
                        unmatchset.add(mmatch.getMatch());
                    }
//                    Set<ValueMaskPair> unmatchset = unmatchMap.get(matchfield);
//                    if (unmatchset == null) {
//                        unmatchset = new HashSet<>(match.getMatchSet());
//                        unmatchMap.put(matchfield, unmatchset);
//                    } else {
//                        unmatchset.addAll(match.getMatchSet());
//                    }
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
                ByteArray value = ti.getValue();
                TraceTreeVNode.VNodeEntry oldentry = v.getEntryOrConstruct(value);

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

        recurseMarkDeleted(node);

        if (item instanceof Trace.TestItem) {

            Trace.TestItem ti = (Trace.TestItem) item;
            return TraceTreeTNode.buildNodeIfNeedOrNull(ti, matchMap); //NOTE will generate match

        } else if (item instanceof Trace.TraceGet) {
            Trace.TraceGet ti = (Trace.TraceGet) item;
            return TraceTreeVNode.buildNodeIfNeedOrNull(ti, matchMap);
        } else {
            throw new RuntimeException("impossible");
        }
    }

    private TraceTreeNode testifLNodeexpected_ornew(@Nullable TraceTreeNode node, List<Forward> route, MaplePacket pkt) {
        if (node instanceof TraceTreeLNode) {
            TraceTreeLNode l = (TraceTreeLNode) node;
            if (l.getRoute().equals(route)) {
                //NOTE generate drop rule if route is drop
                handleIfNeedDrop(l, pkt);
                return node;
            }
        }
        recurseMarkDeleted(node);
        TraceTreeLNode lNode = TraceTreeLNode.build(route, matchMap);
        //NOTE generate drop rule if route is drop
        handleIfNeedDrop(lNode, pkt);
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
            ((TraceTreeLNode) node).getRule().setIsDeleted(true);
        } else if (node instanceof TraceTreeTNode) {
            TraceTreeTNode t = (TraceTreeTNode) node;
//            recurseMarkDeleted(t.getBranch(false));
//            if (t.getBarrierRule() != null) {
//                t.getBarrierRule().setIsDeleted(true);
//            }
//            recurseMarkDeleted(t.getBranch(true));
            recurseMarkDeleted(t.getBranchFalse());
            Map<MapleMatch, TraceTreeTNode.TNodeEntry> bm = t.getBranchtrueMap();
            for (TraceTreeTNode.TNodeEntry te : bm.values()) {
                if (te.barrierRule != null) {
                    te.barrierRule.setIsDeleted(true);
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


    //-------------------------------generateRules-----------------------------

    private List<MapleRule> rules;
    private int globalpriority;

    public List<MapleRule> generateRules() {
        rules = new ArrayList<>();
        globalpriority = 1;
        recurseUpdatePriority(treeroot);
        return rules;
    }

    private void recurseUpdatePriority(TraceTreeNode node) {
        if (node == null) {
            return;
        }
        if (node instanceof TraceTreeLNode) {
            node.priority = globalpriority;
            MapleRule rule = ((TraceTreeLNode) node).getRule();
            rule.setPriority(globalpriority);
            rules.add(rule);
        } else if (node instanceof TraceTreeTNode) {
            TraceTreeTNode t = (TraceTreeTNode) node;
//            TraceTreeNode branchfalse = t.getBranch(false);
//            if (branchfalse != null) {
//                recurseUpdatePriority(branchfalse);
//                globalpriority++;
//                MapleRule barrierRule = t.getBarrierRule();
//                barrierRule.setPriority(globalpriority);
//                rules.add(barrierRule);
//                globalpriority++;
//            }
//            recurseUpdatePriority(t.getBranch(true));
            TraceTreeNode branchfalse = t.getBranchFalse();
            if (branchfalse != null) {
                recurseUpdatePriority(branchfalse);
                globalpriority++;
            }
            int barrierpri = globalpriority;
            Map<MapleMatch, TraceTreeTNode.TNodeEntry> bm = t.getBranchtrueMap();
            for (TraceTreeTNode.TNodeEntry te : bm.values()) {
                globalpriority = barrierpri;
                if (te.barrierRule != null) {
                    te.barrierRule.setPriority(barrierpri);
                    rules.add(te.barrierRule);
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
