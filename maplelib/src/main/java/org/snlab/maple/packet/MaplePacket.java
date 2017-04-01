/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.packet;


import org.snlab.maple.api.IMaplePacket;
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.flowrule.MapleMatchField;
import org.snlab.maple.packet.parser.Ethernet;
import org.snlab.maple.tracetree.TraceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * MaplePacket.
 */
public class MaplePacket implements IMaplePacket {

    private MapleTopology.Port ingress;

    private Ethernet frame;

    private List<TraceItem> traceList =new ArrayList<TraceItem>();

    public MaplePacket(byte[] data, MapleTopology.Port ingress){
        this.ingress=ingress;
        this.frame=new Ethernet();
        frame.deserialize(data,0,data.length);
    }

    public List<TraceItem> getTraceList() {
        return traceList;
    }



    //-------------------------------trace functions-----------------------------

    public PktFieldMaskable ethSrc(){
        return null;
    }

    public PktFieldMaskable ethDst(){
        return null;
    }

    public PktFieldMaskable vlanid(){
        return null;
    }


    @Override
    public MaplePacket.Ingress ingress(){
        return new Ingress();
    }


    public boolean isTunnel(){
        return false;
    }


    public PktFieldMaskable ipSrc(){
        return new PktFieldMaskable(MapleMatchField.IP_SRC);
    }


    //-------------------------------setRoute functions-----------------------------

    /**
     * setRoute
     * @param path  path which can include ingress at the beginning
     */
    @Override
    public void setRoute(String... path){

    }

    @Override
    public void addRoute(String... path){

    }


    //-------------------------------inner class-----------------------------

    public class Ingress{
        private Ingress(){

        }

        public boolean is(String ingress){
            boolean ret= MaplePacket.this.ingress.getId().equals(ingress);
            TraceItem ti = new TraceItem(MapleMatchField.INGRESS, null, ingress.getBytes(), TraceItem.Type.TEST, ret);
            MaplePacket.this.traceList.add(ti);
            return ret;
        }

        public boolean is(MapleTopology.Port port){
            boolean ret=MaplePacket.this.ingress.equals(port);
            TraceItem ti = new TraceItem(MapleMatchField.INGRESS, null, port.toString().getBytes(), TraceItem.Type.TEST, ret);
            MaplePacket.this.traceList.add(ti);
            return ret;
        }

        public boolean in(String... ingresses){
            return false;
        }

        public boolean in(MapleTopology.Port ...ports){
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

    public class PktField {

        protected MapleMatchField field;

        private PktField(MapleMatchField field){
            this.field=field;
        }

        public boolean is(byte[] context){
            return false;
        }

        public boolean in(List<byte[]> set){
            return false;
        }

        public boolean range(byte[] value1,byte[] value2){
            return false;
        }

        public byte[] get(){
            return null;
        }

    }

    public class PktFieldMaskable extends PktField {

        private byte[] mask;

        private PktFieldMaskable(MapleMatchField field){
            super(field);
        }
        public PktFieldMaskable mask(byte[] context){
            return this;
        }

        @Override
        public boolean is(byte[] context){
            return false;
        }

        @Override
        public boolean in(List<byte[]> set){
            return false;
        }

        @Override
        public byte[] get(){
            return null;
        }

    }

}
