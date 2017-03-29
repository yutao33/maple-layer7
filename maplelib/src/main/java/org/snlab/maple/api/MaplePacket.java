/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api;


import org.snlab.maple.tracetree.MapleMatch;
import org.snlab.maple.tracetree.MapleMatchField;
import org.snlab.maple.api.network.MapleTopology;
import org.snlab.maple.tracetree.TraceItem;

import java.util.ArrayList;
import java.util.List;

public class MaplePacket implements TracePacket{

    private MapleTopology.Port ingress;

    private List<TraceItem> traceList =new ArrayList<TraceItem>();

    public MaplePacket(){

    }

    public List<TraceItem> getTraceList() {
        return traceList;
    }

    public MaplePacket.MatchFieldMaskable ethSrc(){
        return null;
    }

    public MaplePacket.MatchFieldMaskable ethDst(){
        return null;
    }

    public MaplePacket.MatchFieldMaskable vlanid(){
        return null;
    }


    public MaplePacket.Ingress ingress(){
        return new Ingress();
    }


    public boolean isTunnel(){
        return false;
    }


    public MaplePacket.MatchFieldMaskable ipSrc(){
        return new MaplePacket.MatchFieldMaskable(MapleMatchField.IP_SRC);
    }

    public class Node{

    }

    public class Ingress{
        private Ingress(){

        }

        /**  ingress : 'openflow:1:1' **/
        public boolean is(String ingress){
            boolean ret= MaplePacket.this.ingress.getId().equals(ingress);
            TraceItem ti = new TraceItem(MapleMatchField.INGRESS, null, ingress.getBytes(), TraceItem.Type.TEST, ret);
            MaplePacket.this.traceList.add(ti);
            return ret;
        }

        public boolean in(String[] ingresses){
            return false;
        }

        public boolean belongto(String node){
            boolean ret = MaplePacket.this.ingress.getOwner().getId().equals(node);
            TraceItem ti = new TraceItem(MapleMatchField.INGRESS, null, node.getBytes(), TraceItem.Type.VALUE, ret);
            MaplePacket.this.traceList.add(ti);
            return ret;
        }

        public MapleTopology.Port getValue(){
            return MaplePacket.this.ingress;
        }
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
        public MaplePacket.MatchFieldMaskable mask(byte[] context){
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

    /**
     * setRoute in String format.
     * @param ingress the ingress of the packet, when it is null, it means not to match in_port field at the begin node;
     * @param path a continuous forwarding path
     */
    public void setRoute(String ingress,String[] path) {

    }
}
