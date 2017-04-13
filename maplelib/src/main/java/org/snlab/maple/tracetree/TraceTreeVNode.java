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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class TraceTreeVNode extends TraceTreeNode {
    private final MapleMatchField field;
    private final ByteArray mask;
    private Map<ByteArray, VNodeEntry> matchentries;//children

    public TraceTreeVNode(MapleMatchField field, ByteArray mask) {
        this.field = field;
        this.mask = mask;
        matchentries=new HashMap<>();
    }

    @Nonnull
    public VNodeEntry getEntryOrConstruct(@Nonnull ByteArray value){
        VNodeEntry v = matchentries.get(value);
        if(v==null){
            v=new VNodeEntry(null,null);//TODO match
            matchentries.put(value,v);
        }
        return v;
    }

    @Override
    public boolean isConsistentWith(@Nullable Trace.TraceItem item) {
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


    //-------------------------------build node-----------------------------

    @Nullable
    public static TraceTreeTNode buildNodeIfNeedOrNull(@Nonnull Trace.TestItem item, @Nonnull Map<MapleMatchField,MapleMatch> matchMapBefore){
        return null;
    }



    //-------------------------------inner class-----------------------------

    /**
     * VNodeEntry.
     */
    public static class VNodeEntry{
        TraceTreeNode child;

        MapleMatch match;//NOTE for generate rules

        private VNodeEntry(TraceTreeNode child, MapleMatch match) {
            this.child = child;
            this.match = match;
        }
    }
}
