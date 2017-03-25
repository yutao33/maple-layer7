/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api.route;

import java.util.List;

public class Forward {

    public Forward(String output){

    }

    public Forward(String output,String inport,String actions){

    }

    public Forward(String output,String inport,String Actions,int bandwidth){

    }
}

//setRoute()
//setFath()
//addPath()

class MapleSetAction{

}


enum MapleMatchField{
    ETH_SRC("eth_src"),
    ETH_DST("eth_dst");

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




class MapleMatch{
    MapleMatchField field;
    byte[] value;
    byte[] mask;
}

class TraceItem{

}

abstract class TraceTreeNode{

}

class TTN_TNode extends TraceTreeNode{
    class TNodeEntry{
        MapleMatch match;
        TraceTreeNode branch;
    }
    List<TNodeEntry> list;
    TraceTreeNode nomatchbranch;
}

class TTN_VNode extends TraceTreeNode{
    class VNodeEntry{

    }
}



class TraceTree{
    TraceTreeNode treeroot;
}


interface TraceMaplePacket {

    long ethSrc();

    long ethDst();

    int ethType();

    boolean ethSrcIs(long exp);

    boolean ethDstIs(long exp);

    boolean ethTypeIs(int exp);

    void setRoute(String[] path);
}

class MaplePacket implements TraceMaplePacket{

    public long ethSrc() {
        return 0;
    }

    public long ethDst() {
        return 0;
    }

    public int ethType() {
        return 0;
    }

    public boolean ethSrcIs(long exp) {
        return false;
    }

    public boolean ethDstIs(long exp) {
        return false;
    }

    public boolean ethTypeIs(int exp) {
        return false;
    }

    public void setRoute(String[] path) {

    }
}


