/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;

import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.ByteArray;
import org.snlab.maple.rule.match.MapleMatch;
import org.snlab.maple.rule.match.MapleMatchIngress;
import org.snlab.maple.rule.match.ValueMaskPair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TraceTreeVNode extends TraceTreeNode {
    private final MapleMatchField field;
    private final ByteArray mask;
    private Map<ByteArray, VNodeEntry> matchentries;//children

    public TraceTreeVNode(MapleMatchField field, ByteArray mask) {
        this.field = field;
        this.mask = mask;
        matchentries = new HashMap<>();
    }

    @Nonnull
    VNodeEntry getEntryOrConstruct(@Nonnull ByteArray value) {
        VNodeEntry v = matchentries.get(value);
        if (v == null) {
            v = new VNodeEntry(null, null);//TODO no need
            matchentries.put(value, v);
        }
        return v;
    }

    @Override
    public boolean isConsistentWith(@Nullable Trace.TraceItem item) {
        if (item instanceof Trace.TraceGet) {
            return this.field.equals(item.getField())
                    && Objects.equals(mask, item.getMask());
        }
        return false;
    }

    public Iterator<TraceTreeNode> iterator() {
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


    //-------------------------------build node-----------------------------
//
//    @Nullable
//    public static TraceTreeVNode buildNodeIfNeedOrNull(@Nonnull Trace.TraceGet item, @Nonnull Map<MapleMatchField, MapleMatch> matchMapBefore) {
//        MapleMatchField field = item.getField();
//        MapleMatch oldMatch = matchMapBefore.get(field);
//        MapleMatch subMatch = null;
//        ValueMaskPair vmp = new ValueMaskPair(item.getValue(), item.getMask());
//        Set<ValueMaskPair> subset = new HashSet<>();
//        subset.add(vmp);
//        if (oldMatch != null) {
//            Set<ValueMaskPair> newset = new HashSet<>();
//            boolean ret = oldMatch.getMatchProperSubSetOrfalse(subset, newset);
//            if (ret && !newset.isEmpty()) {
//                subMatch = new MapleMatch(field, newset);
//            }
//        } else {
//            subMatch = new MapleMatch(field, subset);
//        }
//        if (subMatch != null) {
//            TraceTreeVNode VNode = new TraceTreeVNode(field, item.getMask());
//            VNode.matchentries.put(item.getValue(), new VNodeEntry(null, subMatch));
//            return VNode;
//        }
//        return null;
//    }


    @Nullable
    public static TraceTreeVNode buildNodeIfNeedOrNull(@Nonnull Trace.TraceGet item, @Nonnull Map<MapleMatchField, MapleMatch> matchMapBefore) {
        MapleMatchField field = item.getField();
        if(field.equals(MapleMatchField.INGRESS)){   //NOTE special ingress
            return buildIngress(item,(MapleMatchIngress)matchMapBefore.get(MapleMatchField.INGRESS));
        }

        MapleMatch oldMatch = matchMapBefore.get(field);
        ValueMaskPair oldpair=null;
        if(oldMatch!=null){
            oldpair=oldMatch.getMatch();
        }

        MapleMatch subMatch = null;
        ValueMaskPair newpair = new ValueMaskPair(item.getValue(), item.getMask());
        if (oldpair != null) {
            ValueMaskPair subpair = ValueMaskPair.getSubSet(oldpair, newpair);
            if(subpair!=null&&!subpair.equals(oldpair)){
                subMatch=new MapleMatch(field,subpair);
            }
        } else {
            subMatch = new MapleMatch(field, newpair);
        }
        if (subMatch != null) {
            TraceTreeVNode vNode = new TraceTreeVNode(field, item.getMask());
            vNode.matchentries.put(item.getValue(), new VNodeEntry(null, subMatch));
            return vNode;
        }
        return null;
    }

    private static TraceTreeVNode buildIngress(Trace.TraceGet item, MapleMatchIngress oldMatch) {
        String str=new String(item.getValue().getBytes());
        ByteArray mask = item.getMask();

        MapleMatchIngress subMatch=null;
        if(oldMatch!=null){
            oldMatch.getSubMatch();
        } else {

        }
        return null;
    }


    //-------------------------------inner class-----------------------------

    /**
     * VNodeEntry.
     */
    public static class VNodeEntry {
        TraceTreeNode child;

        MapleMatch match;//NOTE for generate rules

        private VNodeEntry(TraceTreeNode child, MapleMatch match) {
            this.child = child;
            this.match = match;
        }
    }
}
