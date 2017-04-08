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
import org.snlab.maple.rule.match.ValueMaskPair;
import org.snlab.maple.rule.route.Forward;

import java.util.*;


/**
 * TraceTree.
 */
public class TraceTree {
    private TraceTreeNode treeroot;

    //-------------------------------update-----------------------------

    public synchronized void update(List<Trace.TraceItem> items, MaplePacket pkt) {
        List<Forward> route = pkt.getRoute();
        if (route.isEmpty()) {
            //TODO find and delete
            return;
        }
        if (items.isEmpty()) {
            treeroot = testifLNode_and_updateornew(treeroot, route);
            return;
        }
        Trace.TraceItem item0 = items.get(0);
        treeroot = testifexpected_and_updateornew(treeroot, item0);

        TraceTreeNode nodep = treeroot;

        for (int i = 0; i < items.size(); i++) {
            if (nodep instanceof TNode) {
                Trace.TestItem ti = (Trace.TestItem) items.get(i);
                TNode t = (TNode) nodep;
                boolean testresult = ti.getresult();
                TraceTreeNode tb = t.getBranch(testresult);
                if (i == items.size() - 1) {
                    nodep = testifLNode_and_updateornew(tb, route);
                } else {
                    nodep = testifexpected_and_updateornew(tb, items.get(i + 1));
                }
                t.setBranch(testresult, nodep);
            } else if (nodep instanceof VNode) {
                Trace.TraceGet ti = (Trace.TraceGet) items.get(i);
                VNode v = (VNode) nodep;
                ByteArray value = ti.getValue();
                TraceTreeNode tv = v.matchentries.get(value);
                if (i == items.size() - 1) {
                    nodep = testifLNode_and_updateornew(tv, route);
                } else {
                    nodep = testifexpected_and_updateornew(tv, items.get(i + 1));
                }
                v.matchentries.put(value, nodep);
            } else {
                throw new RuntimeException("unexpected");
            }
        }
    }

    private TraceTreeNode testifexpected_and_updateornew(TraceTreeNode node, Trace.TraceItem item) {
        TraceTreeNode ret = null;
        if (item instanceof Trace.TestItem) {
            Trace.TestItem ti = (Trace.TestItem) item;
            if (node instanceof TNode) {
                ret = testifTNodeexpected_and_updateornew((TNode) node, ti);
            } else {
                recurseMarkDeleted(node);
                TNode t = new TNode(item.getField());

                ret = t;
            }

        } else if (item instanceof Trace.TraceGet) {
            if (node instanceof VNode) {

            } else {

            }
        } else {
            throw new RuntimeException("unexpected");
        }
        return ret;
    }

    private TraceTreeNode testifTNodeexpected_and_updateornew(TNode node, Trace.TestItem item) {
        TraceTreeNode ret = null;

        return ret;
    }

    private TraceTreeNode testifLNode_and_updateornew(TraceTreeNode node, List<Forward> route) {
        TraceTreeNode ret = null;
        if (node instanceof LNode) {
            LNode l = (LNode) node;
            l.route = route;//TODO
            ret = node;
        } else {
            recurseMarkDeleted(node);
            ret = new LNode(route);
        }
        return ret;
    }

    private void recurseMarkDeleted(TraceTreeNode node) {
        if (node == null) {
            return;
        }
        if (node instanceof LNode) {
            ((LNode) node).rule.setIsDeleted(true);
        } else if (node instanceof TNode) {
            TNode t = (TNode) node;
            recurseMarkDeleted(t.branchfalse);
            recurseMarkDeleted(t.branchtrue);
        } else if (node instanceof VNode) {
            VNode v = (VNode) node;
            Collection<TraceTreeNode> branches = v.matchentries.values();
            for (TraceTreeNode branch : branches) {
                recurseMarkDeleted(branch);
            }
        } else {
            throw new RuntimeException("unexpected");
        }
    }


    //-------------------------------generateRules-----------------------------

    public void generateRules() {

    }


    //-------------------------------inner class-----------------------------

    /**
     * abstract static class TestCondition.
     */
    public abstract static class TestCondition {
        protected ByteArray mask;

        public Set<ValueMaskPair> toMatchSet() {
            throw new UnsupportedOperationException();
        }

        ;
    }

    public static class SingleValue extends TestCondition {
        private ByteArray value;

        public SingleValue(ByteArray value, ByteArray mask) {
            this.value = value;
            this.mask = mask;
        }

        @Override
        public Set<ValueMaskPair> toMatchSet() {
            Set<ValueMaskPair> set = new HashSet<>();
            set.add(new ValueMaskPair(value, mask));
            return set;
        }
    }

    public static class ValueSet extends TestCondition {
        private Set<ByteArray> values;

        public ValueSet(Set<ByteArray> values, ByteArray mask) {
            this.values = values;
            this.mask = mask;
        }

        @Override
        public Set<ValueMaskPair> toMatchSet() {
            Set<ValueMaskPair> set = new HashSet<>();
            for (ByteArray value : values) {
                set.add(new ValueMaskPair(value, mask));
            }
            return set;
        }
    }

    public static class ValueRange extends TestCondition {
        private ByteArray value1;
        private ByteArray value2;

        public ValueRange(ByteArray value1, ByteArray value2, ByteArray mask) {
            this.value1 = value1;
            this.value2 = value2;
            this.mask = mask;
        }

        @Override
        public Set<ValueMaskPair> toMatchSet() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * abstract static class TraceTreeNode.
     */
    public abstract static class TraceTreeNode {
        protected int priority;
    }

    public static class LNode extends TraceTreeNode {
        private List<Forward> route;
        private MapleRule rule;

        public LNode(List<Forward> route) {
            this.route = route;
        }
    }

    public static class TNode extends TraceTreeNode {
        private MapleMatchField field;
        private TestCondition condition;
        private Set<ValueMaskPair> matchset;
        private TraceTreeNode branchtrue;
        private TraceTreeNode branchfalse;

        public TNode(MapleMatchField field) {
            this.field = field;
        }

        private TraceTreeNode getBranch(boolean b) {
            if (b) return branchtrue;
            else return branchfalse;
        }

        private void setBranch(boolean b, TraceTreeNode branch) {
            if (b) branchtrue = branch;
            else branchfalse = branch;
        }
    }

    public static class VNode extends TraceTreeNode {
        private MapleMatchField field;
        private ByteArray mask;
        private Map<ByteArray, TraceTreeNode> matchentries;

        public VNode(MapleMatchField field) {
            this.field = field;
        }
    }

}
