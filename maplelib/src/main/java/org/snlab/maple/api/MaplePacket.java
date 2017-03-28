/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api;


import org.snlab.maple.api.network.MapleMatchField;
import org.snlab.maple.api.network.MapleTopology;
import org.snlab.maple.api.packet.MACAddress;
import org.snlab.maple.tracetree.TraceItem;

import java.util.ArrayList;
import java.util.List;

public class MaplePacket implements TracePacket{

    private MapleTopology.Port ingress;

    private List<TraceItem> traceList=new ArrayList<TraceItem>();

    public MaplePacket(){

    }

    public List<TraceItem> getTraceList() {
        return traceList;
    }

//    public byte[] ethSrc() {
//
//        return null;
//    }
//
//    public byte[] ethSrc_mask(){
//        return null;
//    }
//
//    public boolean ethSrcIs(byte[] mac) {
//
//        return false;
//    }
//
//    public boolean ethSrc_mask_is(){
//        return false;
//    }

    public MACAddress getEthSrc_0(){
        return null;
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


    public MaplePacket.MatchFieldNoMask ingress(){
        return null;
    }

    public MapleTopology.Port getIngress_0(){
        return this.ingress;
    }

    public boolean isTunnel(){
        return false;
    }


    public MaplePacket.MatchFieldMaskable ipSrc(){
        return new MaplePacket.MatchFieldMaskable(MapleMatchField.IP_SRC);
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

        protected MapleMatchField field;
        protected byte[] mask;

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




    public void setRoute(String[] path) {

    }
}
