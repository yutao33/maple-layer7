/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;


import org.snlab.maple.flowrule.MapleMatchField;
import org.snlab.maple.flowrule.MapleRule;
import org.snlab.maple.packet.MaplePacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TraceTree.
 */
public class TraceTree{
    private TraceTreeNode treeroot;

    public synchronized void update(List<TraceItem> items,MaplePacket pkt){

        if(items.isEmpty()){
            return ;
        }

        if(treeroot==null){
            treeroot=newNode();
        }

        TraceTreeNode nodep=treeroot;

        for (TraceItem traceItem : items) {
            if(nodep instanceof TNode){

            } else if(nodep instanceof VNode){

            } else {  //LNode

            }
        }
    }

    private TraceTreeNode newNode(){
        return null;
    }

    private TraceTreeNode setTNode(){
        return null;
    }

    public void generateRules(){

    }






    //-------------------------------inner class-----------------------------

    public abstract static class TraceTreeNode{
        protected int priority;
    }

    public static class LNode extends TraceTreeNode{
        //action
        //genrule
        private MapleRule rule;

    }



    public static class TNode extends TraceTreeNode {
        private MapleMatchField field;
        private byte[] mask;
        private List<byte[]> values;
        private TraceTreeNode branchtrue;
        private TraceTreeNode branchfalse;
        public TNode(MapleMatchField field){
            this.field=field;
            values=new ArrayList<>();
        }
    }



    public static class VNode extends TraceTreeNode {
        private MapleMatchField field;
        private byte[] mask;
        private Map<byte[],TraceTreeNode> matchentrys;

        public VNode(MapleMatchField field) {
            this.field=field;
            matchentrys=new HashMap<>();
        }
    }

}
