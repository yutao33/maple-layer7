/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api.route;

import java.util.ArrayList;
import java.util.List;



//

public class Forward {

    public Forward(String output){

    }

    public Forward(String output,String inport,String actions){

    }

    public Forward(String output,String inport,String Actions,int bandwidthlimit){

    }


}

class MapleSetAction{

}




class MapleNetworkPort{
    MapleNetworkNode owner;
    String id;//  1 2 3 4 internel
    MapleNetworkPort end;

}

class MapleNetworkNode{
    List<MapleNetworkPort> ports;
}

class MapleNetworkLink{
    MapleNetworkPort p1;
    MapleNetworkPort p2;
}

class MapleNetworkTopology{
    List<MapleNetworkNode> nodes;
    List<MapleNetworkTopology>links;
}



enum MapleMatchField{
    ETH_SRC("eth_src"),
    ETH_DST("eth_dst"),
    IP_SRC("ip_src");

    private String field;
    private MapleMatchField(String str){
        this.field =str;
    }

    @Override
    public String toString() {
        return "MapleMatchField{" +
                "field='" + field + '\'' +
                '}';
    }
}

class MapleMatch{
    MapleMatchField field;
    byte[] value;
    byte[] mask;
}


abstract class TraceTreeNode{

}



class TT_MatchEntry{

}

class TT_TNode extends TraceTreeNode{
    class TNodeEntry{
        MapleMatch match;
        TraceTreeNode branch;
    }
    List<TNodeEntry> list;
    TraceTreeNode nomatchbranch;
}

class TT_VNode extends TraceTreeNode{
    //field
    //matchentrys
}

class TT_LNode extends TraceTreeNode{

}



class TraceTree{
    private TraceTreeNode treeroot;

    public void update(List<TraceItem> items){

    }

    public void generateRules(){

    }


}



class TraceItem{

}


class MaplePacket{

    private MapleNetworkPort ingress;

    private List<TraceItem> traceList=new ArrayList<TraceItem>();

    public MaplePacket(){

    }

    public List<TraceItem> getTraceList() {
        return traceList;
    }

    public byte[] ethSrc() {

        return null;
    }

    public byte[] ethSrc_mask(){
        return null;
    }

    public boolean ethSrcIs(byte[] mac) {

        return false;
    }

    public boolean ethSrc_mask_is(){
        return false;
    }


    /**
     * this is a special function.
     * @return
     */
    public MapleNetworkPort getIngress(){
        return this.ingress;
    }


    public MatchFieldMaskable ipSrc(){
        return new MatchFieldMaskable(MapleMatchField.IP_SRC);
    }

    public class MatchFieldNoMask{
        private MapleMatchField field;
        private MatchFieldNoMask(MapleMatchField field){
            this.field=field;
        }
        public boolean is(byte[] context){
            return false;
        }
        public boolean in(List<byte[]> set){
            return false;
        }
        public byte[] getValue(){
            return null;
        }
    }

    public class MatchFieldMaskable {

        private MapleMatchField field;
        private byte[] mask;

        private MatchFieldMaskable(MapleMatchField field){
            this.field=field;
        }
        public MatchFieldMaskable mask(byte[] context){
            return this;
        }
        public boolean is(byte[] context){
            return false;
        }
        public boolean in(List<byte[]> set){
            return false;
        }
        public byte[] getValue(){
            return null;
        }
    }


    public void setRoute(String[] path) {

    }
}

class PacketOutMaplePacket{


    void setRoute(){

    }

    void sendOut(){

    }
}


class MapleSystem{

    private TraceTree traceTree;

    void onPacket(MaplePacket pkt){

        //app chain
        //...
        //app chain

        List<TraceItem> traceList = pkt.getTraceList();
        traceTree.update(traceList);

    }
}


class App{
    boolean onPacket(MaplePacket pkt){
        if(pkt.ipSrc().is(new byte[]{33})){
            pkt.setRoute(null);
            return true;
        } else {
            pkt.setRoute(null);
            return true;
        }
    }
}


