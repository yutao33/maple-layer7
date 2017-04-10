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
import org.snlab.maple.packet.parser.Ethernet;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.route.Forward;
import org.snlab.maple.tracetree.Trace;
import org.snlab.maple.tracetree.Trace.TraceItem;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * MaplePacket.
 */
public class MaplePacket implements IMaplePacket {

    private MapleTopology.Port ingress;

    private Ethernet frame;

    private List<TraceItem> traceList = new ArrayList<>();

    private Map<MapleMatchField, byte[]> fieldMap;

    private List<Forward> route = null;  //when it is null, default to drop

    public MaplePacket(byte[] data, MapleTopology.Port ingress) {
        this.ingress = ingress;
        this.frame = new Ethernet();
        frame.deserialize(data, 0, data.length);
        fieldMap = frame.buildMatchFieldMap();
    }

    public List<TraceItem> getTraceList() {
        return traceList;
    }

    private void addTraceItem(TraceItem item) {
        this.traceList.add(item);
    }


    //-------------------------------get Raw packet functions-----------------------------

    public Ethernet getFrame() {
        return frame;
    }

    public MapleTopology.Port getIngress() {
        return ingress;
    }

    //-------------------------------trace functions-----------------------------

    @Override
    public PktFieldMaskable ethSrc() {
        return new PktFieldMaskable(MapleMatchField.ETH_SRC);
    }

    @Override
    public PktFieldMaskable ethDst() {
        return new PktFieldMaskable(MapleMatchField.ETH_DST);
    }

    public PktFieldMaskable vlanId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PktFieldMaskable ipSrc() {
        return new PktFieldMaskable(MapleMatchField.IP_SRC);
    }

    @Override
    public PktFieldMaskable ipDst() {
        return new PktFieldMaskable(MapleMatchField.IP_DST);
    }

    @Override
    public MaplePacket.Ingress ingress() {
        return new Ingress();
    }


    public boolean isTunnel() {
        throw new UnsupportedOperationException();
    }


    //-------------------------------Route functions-----------------------------

    private void checkRoute(){
        if(route==null){
            route=new ArrayList<>();
        }
    }

    @Override
    public void setRoute(String... path) {
        if(route!=null){
            route.clear();
        }
        addRoute(path);
    }

    @Override
    public void addRoute(String... path) {
        checkRoute();
        int flen = path.length / 2;
        for(int i=0;i<flen*2;i++){
            Forward forward = new Forward(path[i * 2], path[i * 2 + 1]);
            route.add(forward);
        }
    }

    @Override
    public void setRoute(Forward... path) {
        if(route!=null){
            route.clear();
        }
        addRoute(path);
    }

    @Override
    public void addRoute(Forward... path){
        checkRoute();
        Collections.addAll(route,path);
    }

    public void setPktField(MapleMatchField field, byte[] value) {
        //TODO
    }

    public void setTimeOut(int timeout) {
        //TODO
    }

    public List<Forward> getRoute() {
        return route;
    }


    //-------------------------------inner class-----------------------------

    public class Ingress {
        private Ingress() {

        }

        public boolean is(String ingress) {
            assert ingress.matches("^openflow:\\d+:\\w+$");//TODO
            boolean ret = MaplePacket.this.ingress.getId().equals(ingress);
            TraceItem ti = new Trace.TraceIs(MapleMatchField.INGRESS, null, ingress.getBytes(), ret);
            addTraceItem(ti);
            return ret;
        }

        public boolean is(MapleTopology.Port port){
            throw new UnsupportedOperationException();
        }

        public boolean in(String... ingresses) {
            for (String s : ingresses) {
                assert s.matches("^openflow:\\d+:\\w+$");//TODO
            }
            boolean ret = false;
            List<byte[]> values = new ArrayList<>();
            for (String s : ingresses) {
                values.add(s.getBytes());
                if (MaplePacket.this.ingress.getId().equals(s)) {
                    ret = true;
                }
            }
            TraceItem ti = new Trace.TraceIn(MapleMatchField.INGRESS, null, values, ret);
            addTraceItem(ti);
            return ret;
        }

        public boolean in(MapleTopology.Port ...ports){
            throw new UnsupportedOperationException();
        }

        public boolean belongto(String node) {
            boolean ret = MaplePacket.this.ingress.getOwner().getId().equals(node);
            TraceItem ti = new Trace.TraceRange(MapleMatchField.INGRESS, null, node.getBytes(), null, ret);
            addTraceItem(ti);
            return ret;
        }

        public MapleTopology.Port getValue() {
            TraceItem ti = new Trace.TraceGet(MapleMatchField.INGRESS, null, MaplePacket.this.ingress.getId().getBytes());
            addTraceItem(ti);
            return MaplePacket.this.ingress;
        }
    }

    public class PktField {

        protected MapleMatchField field;
        protected byte[] mask;

        private PktField(MapleMatchField field) {
            this.field = field;
        }

        private boolean test(byte[] value, byte[] context) {
            for (int i = 0; i < value.length; i++) {
                byte a = value[i];
                byte b = context[i];
                if (mask != null) {
                    a &= mask[i];
                    b &= mask[i];
                }
                if (a != b) {
                    return false;
                }
            }
            return true;
        }

        public boolean is(byte[] context) {
            assert (field.getBitLength() + 7) / 8 == context.length; //TODO
            byte[] value = fieldMap.get(field).clone();
            boolean ret = test(value, context);
            TraceItem ti = new Trace.TraceIs(field, mask, context, ret);
            addTraceItem(ti);
            return ret;
        }

        public boolean in(byte[]... values) {
            byte[] value = fieldMap.get(field).clone();
            boolean ret = false;
            List<byte[]> list = new ArrayList<>();
            for (byte[] i : values) {
                list.add(i);
                if (test(value, i)) {
                    ret = true;
                }
            }
            TraceItem ti = new Trace.TraceIn(field, mask, list, ret);
            addTraceItem(ti);
            return ret;
        }

        public byte[] get() {
            byte[] value = fieldMap.get(field).clone();
            assert value != null;
            if (mask != null) {
                for (int i = 0; i < value.length; i++) {
                    value[i] &= mask[i];
                }
            }
            TraceItem ti = new Trace.TraceGet(field, mask, value);
            addTraceItem(ti);
            return value;
        }

    }

    public class PktFieldMaskable extends PktField {

        private PktFieldMaskable(MapleMatchField field) {
            super(field);
        }

        public PktFieldMaskable mask(byte[] context) {
            assert field.getByteLength() == context.length; //TODO
            if (mask == null) {
                mask = context;
            } else {
                for (int i = 0; i < mask.length; i++) {
                    mask[i] &= context[i];
                }
            }
            return this;
        }

    }

}
