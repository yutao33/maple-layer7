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

class MapleNetworkTopology{
    List<MapleNetworkNode> nodes;
}

class MapleNetworkLink{

}


class MapleMatch{
    MapleMatchField field;
    byte[] value;
    byte[] mask;
}

class TraceItem{

}

class TraceTree{

}
