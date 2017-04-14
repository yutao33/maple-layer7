/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;

import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.ByteArray;
import org.snlab.maple.rule.match.MapleMatch;
import org.snlab.maple.rule.match.ValueMaskPair;
import org.snlab.maple.rule.route.Forward;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TraceTreeTNode extends TraceTreeNode {

    private final MapleMatchField field;
    private final TestCondition condition;

    private TraceTreeNode branchtrue;
    private TraceTreeNode branchfalse;

    private MapleMatch match;//NOTE for generate rules
    private MapleRule barrierRule;

    public TraceTreeTNode(MapleMatchField field, TestCondition condition) {
        this.field = field;
        this.condition = condition;
    }

    @Nullable
    TraceTreeNode getBranch(boolean b) {
        if (b) return branchtrue;
        else return branchfalse;
    }

    void setBranch(boolean b, TraceTreeNode branch) {
        if (b) branchtrue = branch;
        else branchfalse = branch;
    }

    public MapleMatch getMatch() {
        return match;
    }

    public MapleRule getBarrierRule() {
        return barrierRule;
    }

    public void genBarrierRule(@Nonnull Map<MapleMatchField,MapleMatch> matchMapBefore){
        Map<MapleMatchField,MapleMatch> match=new EnumMap<>(matchMapBefore);
        match.put(this.field,this.match);
        this.barrierRule=new MapleRule(match, Forward.DEFAULT_PuntForwards);
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


    //-------------------------------build node-----------------------------

    @Nonnull
    private static TestCondition genTNodeCondition(@Nonnull Trace.TestItem item){
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
            throw new RuntimeException("impossible");
        }
    }

    @Nullable
    public static TraceTreeTNode buildNodeIfNeedOrNull(@Nonnull Trace.TestItem item, @Nonnull Map<MapleMatchField,MapleMatch> matchMapBefore){
        TestCondition condition = genTNodeCondition(item);
        MapleMatchField field = item.getField();
        Set<ValueMaskPair> valueMaskPairs = condition.toMatchSet(field);
        MapleMatch oldMatch = matchMapBefore.get(field);
        MapleMatch subMatch = null;
        if(oldMatch!=null){
            Set<ValueMaskPair> newset=new HashSet<>();
            boolean ret = oldMatch.getMatchProperSubSetOrfalse(valueMaskPairs, newset);
            if(ret&&!newset.isEmpty()){
                subMatch=new MapleMatch(field,newset);
            }
        } else {
            subMatch=new MapleMatch(field,valueMaskPairs);
        }
        if(subMatch!=null){
            TraceTreeTNode TNode = new TraceTreeTNode(field, condition);
            TNode.match = subMatch;
            return TNode;
        }
        return null;
    }

    //-------------------------------inner class-----------------------------

    /**
     * abstract static class TestCondition.
     */
    public abstract static class TestCondition {
        protected ByteArray mask;

        public Set<ValueMaskPair> toMatchSet(MapleMatchField field) {
            throw new UnsupportedOperationException();
        }

        public boolean isConsistentWith(Trace.TestItem item){
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

}
