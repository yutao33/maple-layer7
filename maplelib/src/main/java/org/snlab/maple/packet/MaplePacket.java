/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.packet;


import com.google.common.base.Preconditions;
import org.snlab.maple.api.IMaplePacket;
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.env.TrackSet;
import org.snlab.maple.packet.parser.Ethernet;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.route.Forward;
import org.snlab.maple.tracetree.Trace;
import org.snlab.maple.tracetree.Trace.TraceItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * MaplePacket.
 */
public class MaplePacket implements IMaplePacket {

    private final static Logger LOG = Logger.getLogger(MaplePacket.class.toString());

    private MapleTopology.PortId inPortId;

    private Ethernet frame;

    private List<TraceItem> traceList = new ArrayList<>();

    private Map<MapleMatchField, byte[]> fieldMap;

    private List<Forward> route = null;  //when it is null, default to drop

    private Set<TrackSet> trackSets=new HashSet<>();

    public MaplePacket(byte[] data, MapleTopology.PortId inPortId) {
        this.inPortId = inPortId;
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

    @Override
    public String toString() {
        return "MaplePacket{" +
                "inPortId=" + inPortId +
                ", frame=" + frame +
                '}';
    }

    //-------------------------------get Raw packet functions-----------------------------

    public Ethernet _getFrame() {
        return frame;
    }

    public MapleTopology.PortId _getInPortId() {
        return inPortId;
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

    @Override
    public PktField ethType() {
        return new PktField(MapleMatchField.ETH_TYPE);
    }

    public PktFieldMaskable vlanId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PktFieldMaskable ipSrc() {
        return new PktFieldMaskable(MapleMatchField.IPv4_SRC);
    }

    @Override
    public PktFieldMaskable ipDst() {
        return new PktFieldMaskable(MapleMatchField.IPv4_DST);
    }

    @Override
    public PktInPort inport() {
        return new PktInPort();
    }


    public boolean isTunnel() {
        throw new UnsupportedOperationException();
    }


    //-------------------------------Route functions-----------------------------

    private void checkRoute() {
        if (route == null) {
            route = new ArrayList<>();
        }
    }

    @Override
    public void setRoute(String... path) {
        Preconditions.checkArgument(path.length % 2 == 0);
        if (path.length == 0) {
            return;
        }
        if (route != null) {
            route.clear();
        }
        addRoute(path);
    }

    @Override
    public void addRoute(String... path) {
        Preconditions.checkArgument(path.length % 2 == 0);
        if (path.length == 0) {
            return;
        }
        checkRoute();
        int flen = path.length / 2;
        for (int i = 0; i < flen; i++) {
            Forward forward = new Forward(path[i * 2], path[i * 2 + 1]);
            route.add(forward);
        }
    }

    @Override
    public void setRoute(Forward... path) {
        if (path.length == 0) {
            return;
        }
        if (route != null) {
            route.clear();
        }
        addRoute(path);
    }

    @Override
    public void addRoute(Forward... path) {
        if (path.length == 0) {
            return;
        }
        checkRoute();
        Collections.addAll(route, path);
    }

    public List<Forward> getRoute() {
        return route;
    }



    public void addTracked(TrackSet trackSet) {
        trackSets.add(trackSet);
    }

    public void removeTrack(){
        for (TrackSet trackSet : trackSets) {
            trackSet.remove(this);
        }
    }


    //-------------------------------inner class-----------------------------

    public class PktInPort {
        private PktInPort() {

        }

        public boolean is(String inport) {
            Preconditions.checkArgument(MapleTopology.isValidPortId(inport));
            boolean ret = MaplePacket.this.inPortId.toString().equals(inport);
            TraceItem ti = new Trace.TraceIs(MapleMatchField.INPORT, null, inport.getBytes(), null, ret);
            addTraceItem(ti);
            return ret;
        }

        public boolean in(String... inports) {
            for (String s : inports) {
                Preconditions.checkArgument(MapleTopology.isValidPortId(s));
            }
            boolean ret = false;
            List<byte[]> values = new ArrayList<>();
            for (String s : inports) {
                values.add(s.getBytes());
                if (MaplePacket.this.inPortId.toString().equals(s)) {
                    ret = true;
                }
            }
            TraceItem ti = new Trace.TraceIn(MapleMatchField.INPORT, null, values, null, ret);
            addTraceItem(ti);
            return ret;
        }

        public PktInPortNode owner() {
            return new PktInPortNode();
        }

        public String getValue() {
            TraceItem ti = new Trace.TraceGet(MapleMatchField.INPORT, null, MaplePacket.this.inPortId.toString().getBytes());
            addTraceItem(ti);
            return MaplePacket.this.inPortId.toString();
        }
    }

    public class PktInPortNode {

        private PktInPortNode() {

        }

        public boolean is(String node) {
            Preconditions.checkArgument(MapleTopology.isValidNodeId(node));
            MapleTopology.NodeId owner = MaplePacket.this.inPortId.getNodeId();
            boolean ret = owner.toString().equals(node);
            TraceItem ti = new Trace.TraceIs(MapleMatchField.INPORT, "mask".getBytes(), node.getBytes(), null, ret);
            addTraceItem(ti);
            return ret;
        }

        public boolean in(String... nodes) {
            for (String s : nodes) {
                Preconditions.checkArgument(MapleTopology.isValidNodeId(s));
            }
            boolean ret = false;
            List<byte[]> values = new ArrayList<>();
            MapleTopology.NodeId owner = MaplePacket.this.inPortId.getNodeId();
            for (String s : nodes) {
                values.add(s.getBytes());
                if (owner.toString().equals(s)) {
                    ret = true;
                }
            }
            TraceItem ti = new Trace.TraceIn(MapleMatchField.INPORT, "mask".getBytes(), values, null, ret);
            addTraceItem(ti);
            return ret;
        }

        public String getValue() {
            MapleTopology.NodeId owner = MaplePacket.this.inPortId.getNodeId();
            TraceItem ti = new Trace.TraceGet(MapleMatchField.INPORT, "mask".getBytes(), owner.toString().getBytes());
            addTraceItem(ti);
            return owner.toString();
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
                    //b &= mask[i];  NOTE b shouldn't be masked
                }
                if (a != b) {
                    return false;
                }
            }
            return true;
        }

        public boolean is(byte[] context) {
            Preconditions.checkArgument(field.getByteLength() == context.length);
            byte[] value = fieldMap.get(field);
            boolean ret = false;
            if (value != null) {
                ret = test(value, context);
            }
            TraceItem ti = new Trace.TraceIs(field, mask, context, value, ret);
            addTraceItem(ti);
            return ret;
        }

        public boolean in(byte[]... values) {
            if (values.length == 0) {
                return false;
            }
            byte[] value = fieldMap.get(field);
            boolean ret = false;
            List<byte[]> list = new ArrayList<>(values.length);
            for (byte[] i : values) {
                list.add(i);
                if (value != null && test(value, i)) {
                    ret = true;
                }
            }
            TraceItem ti = new Trace.TraceIn(field, mask, list, value, ret);
            addTraceItem(ti);
            return ret;
        }

        public boolean range(@Nullable byte[] value1, @Nullable byte[] value2) {
            int len = field.getByteLength();
            if (value1 != null) {
                Preconditions.checkArgument(len == value1.length);
            }
            if (value2 != null) {
                Preconditions.checkArgument(len == value2.length);
            }

            byte[] value = fieldMap.get(field);
            boolean ret = false;  //TODO

            TraceItem ti = new Trace.TraceRange(field, mask, value1, value2, value, ret);

            throw new UnsupportedOperationException();
        }

        public byte[] get() {
            byte[] value = fieldMap.get(field);
            Preconditions.checkArgument(value != null);
            byte[] value1 = new byte[value.length];
            if (mask != null) {
                for (int i = 0; i < value1.length; i++) {
                    value1[i] = (byte) (value[i] & mask[i]);
                }
            }
            TraceItem ti = new Trace.TraceGet(field, mask, value);
            addTraceItem(ti);
            return value1;
        }

    }

    public class PktFieldMaskable extends PktField {

        private PktFieldMaskable(MapleMatchField field) {
            super(field);
        }

        public PktFieldMaskable mask(@Nonnull byte[] context) {
            Preconditions.checkArgument(field.getByteLength() == context.length);
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
