/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;


import org.snlab.maple.flowrule.MapleMatchField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TraceTree.
 */
public class TraceTree{
    private TraceTreeNode treeroot;

    public void update(List<TraceItem> items){

    }

    public void generateRules(){

    }




    public abstract static class TraceTreeNode{
        protected int priority;
    }

    public static class LNode extends TraceTreeNode{
        //action
        //genrule

    }



    public static class TNode extends TraceTreeNode {
        //field
        //mask value set
        //truebranch
        //falsebranch
        private MapleMatchField field;



        private TraceTreeNode branchtrue;
        private TraceTreeNode branchfalse;
    }



    public static class VNode extends TraceTreeNode {
        private MapleMatchField field;
        private byte[] mask;
        private Map<byte[],TraceTreeNode> matchentrys;

        public VNode(MapleMatchField field) {
            this.field=field;
            matchentrys=new HashMap<>();
        }

        public MapleMatchField getField() {
            return field;
        }

        public byte[] getMask() {
            return mask.clone();
        }

    }

}
