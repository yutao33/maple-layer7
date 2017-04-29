/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;

import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.ByteArray;
import org.snlab.maple.rule.match.MapleMatch;
import org.snlab.maple.rule.match.MapleMatchIngress;
import org.snlab.maple.rule.match.ValueMaskPair;
import org.snlab.maple.rule.route.Forward;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TraceTreeTNode extends TraceTreeNode {

    private final MapleMatchField field;
    private final TestCondition condition;

    private Map<MapleMatch,TNodeEntry> branchtrueMap;
    private TraceTreeNode branchfalse;

    public TraceTreeTNode(MapleMatchField field, TestCondition condition) {
        this.field = field;
        this.condition = condition;
    }

    @Nullable
    TraceTreeNode getBranchFalse() {
        return branchfalse;
    }

    void setBranchFalse(TraceTreeNode branch){
        branchfalse=branch;
    }

    @Nullable
    Map.Entry<MapleMatch, TNodeEntry> findEntry(ByteArray key){ //NOTE only when field==INGRESS, key==null
        //TODO not efficient
        if(field.equals(MapleMatchField.INGRESS)){
            return branchtrueMap.entrySet().iterator().next();
        }
        for (Map.Entry<MapleMatch, TNodeEntry> entry : branchtrueMap.entrySet()) {
            if(entry.getKey().getMatch().testMatch(key)){
                return entry;
            }
        }
        return null;
    }

    public MapleMatchField getField() {
        return field;
    }

    public Map<MapleMatch, TNodeEntry> getBranchtrueMap() {
        return branchtrueMap;
    }

    //    void setBranch(boolean b, TraceTreeNode branch) {
//        if (b) branchtrue = branch;
//        else branchfalse = branch;
//    }
//
//    public MapleMatch getMatch() {
//        return match;
//    }
//
    public void genBarrierRule(@Nonnull Map<MapleMatchField, MapleMatch> matchMapBefore) {
        //Map<MapleMatchField, MapleMatch> match = new EnumMap<>(matchMapBefore);
        //match.put(this.field, this.match);
        //this.barrierRule = new MapleRule(match, Forward.DEFAULT_PuntForwards);
        for (Map.Entry<MapleMatch, TNodeEntry> mentry : branchtrueMap.entrySet()) {
            Map<MapleMatchField, MapleMatch> match = new EnumMap<>(matchMapBefore);
            match.put(this.field,mentry.getKey());
            mentry.getValue().barrierRule=new MapleRule(match,Forward.DEFAULT_PuntForwards);
        }
    }

    @Override
    public boolean isConsistentWith(Trace.TraceItem item) {
        if (item instanceof Trace.TestItem) {
            if (this.field.equals(item.getField()) &&
                    this.condition.isConsistentWith((Trace.TestItem) item)) {
                return true;
            }
        }
        return false;
    }


    //-------------------------------build node-----------------------------

    @Nonnull
    private static TestCondition genTNodeCondition(@Nonnull Trace.TestItem item) {
        if (item instanceof Trace.TraceIs) {
            Trace.TraceIs i = (Trace.TraceIs) item;
            return new SingleValue(i.getValue(), i.getMask());
        } else if (item instanceof Trace.TraceIn) {
            Trace.TraceIn i = (Trace.TraceIn) item;
            return new ValueSet(i.getValues(), i.getMask());
        } else if (item instanceof Trace.TraceRange) {
            Trace.TraceRange i = (Trace.TraceRange) item;
            return new ValueRange(i.getValue1(), i.getValue2(), i.getMask());
        } else {
            throw new RuntimeException("impossible");
        }
    }

//    @Nullable
//    public static TraceTreeTNode buildNodeIfNeedOrNull(@Nonnull Trace.TestItem item, @Nonnull Map<MapleMatchField, MapleMatch> matchMapBefore) {
//        TestCondition condition = genTNodeCondition(item);
//        MapleMatchField field = item.getField();
//        Set<ValueMaskPair> valueMaskPairs = condition.toMatchSet(field);
//        MapleMatch oldMatch = matchMapBefore.get(field);
//        MapleMatch subMatch = null;
//        if (oldMatch != null) {
//            Set<ValueMaskPair> newset = new HashSet<>();
//            boolean ret = oldMatch.getMatchProperSubSetOrfalse(valueMaskPairs, newset);
//            if (ret && !newset.isEmpty()) {
//                subMatch = new MapleMatch(field, newset);
//            }
//        } else {
//            subMatch = new MapleMatch(field, valueMaskPairs);
//        }
//        if (subMatch != null) {
//            TraceTreeTNode TNode = new TraceTreeTNode(field, condition);
//            TNode.match = subMatch;
//            return TNode;
//        }
//        return null;
//    }

    @Nullable
    public static TraceTreeTNode buildNodeIfNeedOrNull(@Nonnull Trace.TestItem item, @Nonnull Map<MapleMatchField, MapleMatch> matchMapBefore) {
        MapleMatchField field = item.getField();

        if(field.equals(MapleMatchField.INGRESS)){   //NOTE special ingress
            return buildIngress(item,(MapleMatchIngress)matchMapBefore.get(MapleMatchField.INGRESS));
        }

        TestCondition condition = genTNodeCondition(item);
        Set<ValueMaskPair> valueMaskPairs = condition.toMatchSet(field);

        MapleMatch oldMatch = matchMapBefore.get(field);
        ValueMaskPair oldpair=null;
        if(oldMatch!=null){
            oldpair=oldMatch.getMatch();
        }

        Map<MapleMatch,TNodeEntry> matchmap=new HashMap<>();

        if (oldpair != null) {
            for (ValueMaskPair valueMaskPair : valueMaskPairs) {
                ValueMaskPair pair = ValueMaskPair.getSubSet(valueMaskPair, oldpair);
                if(pair!=null&&pair.equals(oldpair)){
                    return null;
                }
                if(pair!=null) {
                    matchmap.put(new MapleMatch(field, pair), new TNodeEntry());
                }
            }
        } else {
            for (ValueMaskPair valueMaskPair : valueMaskPairs) {
                matchmap.put(new MapleMatch(field,valueMaskPair),new TNodeEntry());
            }
        }

        if (!matchmap.isEmpty()) {
            TraceTreeTNode tNode = new TraceTreeTNode(field, condition);
            tNode.branchtrueMap=matchmap;
            return tNode;
        }
        return null;
    }

    private static TraceTreeTNode buildIngress(Trace.TestItem item, MapleMatchIngress oldMatch) {
        TestCondition condition = genTNodeCondition(item);

        MapleMatchIngress subMatch=null;
        Set<MapleTopology.Port> parmPorts =null;
        Set<MapleTopology.Node> parmNodes =null;
        ByteArray mask = item.getMask();
        if(mask==null){
            parmPorts = new HashSet<>();
            if(item instanceof Trace.TraceIs){
                Trace.TraceIs item1 = (Trace.TraceIs) item;
                String str = new String(item1.getValue().getBytes());
                parmPorts.add(new MapleTopology.Port(str));
            } else if(item instanceof Trace.TraceIn){
                Trace.TraceIn item1 = (Trace.TraceIn) item;
                for (ByteArray value : item1.getValues()) {
                    String str = new String(value.getBytes());
                    parmPorts.add(new MapleTopology.Port(str));
                }
            } else {
                throw new RuntimeException("type error");
            }
        } else {
            parmNodes = new HashSet<>();
            if(item instanceof Trace.TraceIs){
                Trace.TraceIs item1 = (Trace.TraceIs) item;
                String str = new String(item1.getValue().getBytes());
                parmNodes.add(new MapleTopology.Node(str,null));
            } else if(item instanceof Trace.TraceIn){
                Trace.TraceIn item1 = (Trace.TraceIn) item;
                for (ByteArray value : item1.getValues()) {
                    String str = new String(value.getBytes());
                    parmNodes.add(new MapleTopology.Node(str,null));
                }
            } else {
                throw new RuntimeException("type error");
            }
        }

        if(oldMatch!=null){
            subMatch = oldMatch.getSubMatchIngress(parmPorts,parmNodes);
            if(subMatch!=null&&oldMatch.equals(subMatch)){
                subMatch=null;
            }
        } else {
            subMatch = new MapleMatchIngress(parmPorts,parmNodes);
        }
        if (subMatch!=null) {
            Map<MapleMatch,TNodeEntry> matchmap=new HashMap<>();
            matchmap.put(subMatch,new TNodeEntry());
            TraceTreeTNode tNode = new TraceTreeTNode(MapleMatchField.INGRESS, condition);
            tNode.branchtrueMap=matchmap;
            return tNode;
        }
        return null;
    }


    //-------------------------------inner class-----------------------------

    public static class TNodeEntry{
        MapleRule barrierRule;
        TraceTreeNode child;
    }

    /**
     * abstract static class TestCondition.
     */
    public abstract static class TestCondition {
        protected ByteArray mask;

        public Set<ValueMaskPair> toMatchSet(MapleMatchField field) {
            throw new UnsupportedOperationException();
        }

        public boolean isConsistentWith(Trace.TestItem item) {
            throw new UnsupportedOperationException();
        }

    }

    public static class SingleValue extends TestCondition {
        private ByteArray value;

        private SingleValue(ByteArray value, ByteArray mask) {
            this.value = value;
            this.mask = mask;
        }

        @Override
        public Set<ValueMaskPair> toMatchSet(MapleMatchField field) {
            Set<ValueMaskPair> set = new HashSet<>();
            set.add(new ValueMaskPair(value, mask));
            return set;
        }

        @Override
        public boolean isConsistentWith(Trace.TestItem item) {
            if (item instanceof Trace.TraceIs) {
                Trace.TraceIs i = (Trace.TraceIs) item;
                if (Objects.equals(this.value, i.getValue()) &&
                        Objects.equals(this.mask, i.getMask())) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class ValueSet extends TestCondition {
        private Set<ByteArray> values;

        private ValueSet(Set<ByteArray> values, ByteArray mask) {
            this.values = values;
            this.mask = mask;
        }

        @Override
        public Set<ValueMaskPair> toMatchSet(MapleMatchField field) {
            Set<ValueMaskPair> set = new HashSet<>();
            for (ByteArray value : values) {
                set.add(new ValueMaskPair(value, mask));
            }
            return set;
        }

        @Override
        public boolean isConsistentWith(Trace.TestItem item) {
            if (item instanceof Trace.TraceIn) {
                Trace.TraceIn i = (Trace.TraceIn) item;
                if (Objects.equals(this.values, i.getValues()) &&
                        Objects.equals(this.mask, i.getMask())) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class ValueRange extends TestCondition {
        private ByteArray value1;
        private ByteArray value2;
        private ValueRange(ByteArray value1, ByteArray value2, ByteArray mask) {
            this.value1 = value1;//0x 1 01101001
            this.value2 = value2;//0x 0 10010101
            this.mask = mask;    //0x 0 11110100
        }

        @Override
        public Set<ValueMaskPair> toMatchSet(MapleMatchField field) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isConsistentWith(Trace.TestItem item) {
            if (item instanceof Trace.TraceRange) {
                Trace.TraceRange i = (Trace.TraceRange) item;
                if (Objects.equals(this.value1, i.getValue1()) &&
                        Objects.equals(this.value2, i.getValue2()) &&
                        Objects.equals(this.mask, i.getMask())) {
                    return true;
                }
            }
            return false;
        }
    }

}
