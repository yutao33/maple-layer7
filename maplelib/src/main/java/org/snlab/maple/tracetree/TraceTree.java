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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


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
                //Barrier rule
            } else if (nodep instanceof VNode) {
                Trace.TraceGet ti = (Trace.TraceGet) items.get(i);
                VNode v = (VNode) nodep;
                ByteArray value = ti.getValue();
                TraceTreeNode tv = v.getEntryChild(value);
                if (i == items.size() - 1) {
                    nodep = testifLNode_and_updateornew(tv, route);
                } else {
                    nodep = testifexpected_and_updateornew(tv, items.get(i + 1));
                }
                v.putMatchEntry(value,nodep);
                //TODO rules
            } else {
                throw new RuntimeException("unexpected");
            }
        }
    }

    private TraceTreeNode testifexpected_and_updateornew(@Nullable TraceTreeNode node,@Nonnull Trace.TraceItem item) {
        TraceTreeNode ret = null;
        if(node!=null&&node.isConsistentWith(item)){
            // TODO maybe need to update match set
            ret=node;
        }
        else if (item instanceof Trace.TestItem) {
            Trace.TestItem ti = (Trace.TestItem) item;
            recurseMarkDeleted(node);
            TNode t = new TNode(ti.getField());
            t.condition=genTNodeCondition(ti);
            //TODO t.match
            ret = t;
        } else if (item instanceof Trace.TraceGet) {
            Trace.TraceGet ti=(Trace.TraceGet)item;
            recurseMarkDeleted(node);
            VNode v = new VNode(ti.getField(),ti.getMask());
            //TODO v.entry.match
            ret = v;
        } else {
            throw new RuntimeException("unexpected");
        }
        return ret;
    }

    private TestCondition genTNodeCondition(Trace.TestItem item){
        if(item instanceof Trace.TraceIs){
            Trace.TraceIs i=(Trace.TraceIs)item;
            return new SingleValue(i.getValue(),i.getMask());
        } else if(item instanceof Trace.TraceIn){
            Trace.TraceIn i=(Trace.TraceIn)item;
            return new ValueSet(i.getValues(),i.getMask());
        } else if(item instanceof Trace.TraceRange){
            Trace.TraceRange i=(Trace.TraceRange)item;
            return new ValueRange(i.getValue1(),i.getValue2(),i.getMask());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private TraceTreeNode testifLNode_and_updateornew(@Nullable TraceTreeNode node, List<Forward> route) {
        TraceTreeNode ret = null;
        if (node instanceof LNode) {
            LNode l = (LNode) node;
            l.route = route;//TODO generate rules
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
            Collection<VNodeEntry> entries = v.matchentries.values();
            for (VNodeEntry entry : entries) {
                recurseMarkDeleted(entry.child);
            }
        } else {
            throw new RuntimeException("unexpected node type");
        }
    }


    //-------------------------------generateRules-----------------------------

    private List<MapleRule> rules;
    private int globalpriority;

    public synchronized List<MapleRule> generateRules() {
        rules=new ArrayList<>();
        globalpriority=1;
        recurseGenerateRules(treeroot);
        return rules;
    }

    private void recurseGenerateRules(TraceTreeNode node){
        if(node instanceof LNode){
            node.priority=globalpriority;
            rules.add(((LNode)node).rule);
        } else if(node instanceof TNode){
            TNode t=(TNode)node;
            recurseGenerateRules(t.getBranch(false));
            globalpriority++;
            recurseGenerateRules(t.getBranch(true));
        } else if(node instanceof VNode){
            VNode v=(VNode)node;
            for (VNodeEntry entry : v.matchentries.values()) {
                recurseGenerateRules(entry.child);
            }
        } else {
            throw new RuntimeException("unexpected node type 1");
        }
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

        public boolean isConsistentWith(Trace.TestItem item){
            throw new UnsupportedOperationException();
        }

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

        @Override
        public boolean isConsistentWith(Trace.TestItem item) {
            if(item instanceof Trace.TraceIs){
                Trace.TraceIs i=(Trace.TraceIs)item;
                if(Objects.equals(this.value,i.getValue())&&
                        Objects.equals(this.mask,i.getMask())){
                    return true;
                }
            }
            return false;
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

        @Override
        public boolean isConsistentWith(Trace.TestItem item) {
            if(item instanceof Trace.TraceIn){
                Trace.TraceIn i=(Trace.TraceIn)item;
                if(Objects.equals(this.values,i.getValues())&&
                        Objects.equals(this.mask,i.getMask())){
                    return true;
                }
            }
            return false;
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

        @Override
        public boolean isConsistentWith(Trace.TestItem item) {
            if(item instanceof Trace.TraceRange){
                Trace.TraceRange i=(Trace.TraceRange)item;
                if(Objects.equals(this.value1,i.getValue1())&&
                        Objects.equals(this.value2,i.getValue2())&&
                        Objects.equals(this.mask,i.getMask())){
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * abstract static class TraceTreeNode.
     */
    public abstract static class TraceTreeNode {
        protected int priority;

        public boolean isConsistentWith(Trace.TraceItem item){
            throw new UnsupportedOperationException();
        }
    }

    public static class LNode extends TraceTreeNode {
        private List<Forward> route;
        private MapleRule rule;

        public LNode(List<Forward> route) {
            this.route = route;
        }

        @Override
        public boolean isConsistentWith(Trace.TraceItem item) {
            return false;
        }
    }

    public static class TNode extends TraceTreeNode {
        private MapleMatchField field;
        private TestCondition condition;
        private TraceTreeNode branchtrue;
        private TraceTreeNode branchfalse;

        private MapleMatch match;// for generate rules

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

        @Override
        public boolean isConsistentWith(Trace.TraceItem item) {
            if(item instanceof Trace.TestItem){
                if(this.field.equals(item.getField())&&
                        this.condition.isConsistentWith((Trace.TestItem)item)){
                    return true;
                }
            }
            return false;
        }
    }

    public static class VNodeEntry{
        private TraceTreeNode child;

        private MapleMatch match;// for generate rules

        public VNodeEntry(TraceTreeNode child, MapleMatch match) {
            this.child = child;
            this.match = match;
        }
    }

    public static class VNode extends TraceTreeNode {
        private MapleMatchField field;
        private ByteArray mask;
        private Map<ByteArray, VNodeEntry> matchentries;//children

        public VNode(MapleMatchField field,ByteArray mask) {
            this.field = field;
            this.mask = mask;
            matchentries=new HashMap<>();
        }

        public void putMatchEntry(ByteArray value,TraceTreeNode child){
            matchentries.put(value,new VNodeEntry(child,null));
        }

        public TraceTreeNode getEntryChild(ByteArray value){
            return matchentries.get(value).child;
        }

        @Override
        public boolean isConsistentWith(Trace.TraceItem item) {
            if(item instanceof Trace.TraceGet) {
                return this.field.equals(item.getField())
                        && Objects.equals(mask, item.getMask());
            }
            return false;
        }

        public Iterator<TraceTreeNode> iterator(){
            return new Iterator<TraceTreeNode>() {

                private Iterator<VNodeEntry> iter = matchentries.values().iterator();

                @Override
                public void remove() {
                    iter.remove();
                }

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public TraceTreeNode next() {
                    return iter.next().child;
                }
            };
        }
    }

}
