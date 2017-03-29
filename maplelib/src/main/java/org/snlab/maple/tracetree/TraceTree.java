/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;

import java.util.*;

public class TraceTree{
    private TraceTreeNode treeroot;

    public void update(List<TraceItem> items){

    }

    public void generateRules(){

    }




    public abstract static class TraceTreeNode{

    }

    public static class LNode extends TraceTreeNode{

    }

    public static class TNode extends TraceTreeNode {
        class TNodeEntry{
            MapleMatch match;
            TraceTreeNode branch;
        }
        List<TNodeEntry> list;
        TraceTreeNode nomatchbranch;
    }



    public static class VNode extends TraceTreeNode {
        private MapleMatchField field; // !!! class TraceTree can access
        private byte[] mask;  // !!! class TraceTree can access
        private Map<MapleMatch,TraceTreeNode> matchentrys;  // !!! class TraceTree can access

        public VNode(MapleMatchField field) {
            this.field=field;
            matchentrys=new HashMap<>();
        }

        public MapleMatchField getField() {
            return field;
        }

        public byte[] getMask() {
            return Arrays.copyOf(mask,mask.length);
        }

        public Map<MapleMatch,TraceTreeNode> getMatchEntrys(){
            return Collections.unmodifiableMap(matchentrys);
        }
    }
}
