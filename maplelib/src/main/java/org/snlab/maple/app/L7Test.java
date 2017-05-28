/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.api.IMapleDataBroker;
import org.snlab.maple.api.IMaplePacket;
import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.env.TrackedMap;
import org.snlab.maple.packet.types.IPv4Address;

public class L7Test extends MapleAppBase {

    private static final String[] path1 = {null, "openflow:1:3", null, "openflow:2:2", null, "openflow:4:1"};
    private static final String[] path2 = {null, "openflow:4:4", null, "openflow:2:1", null, "openflow:1:1"};
    private static final String[] tap = {null, "openflow:2:3"};

    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {

        if(pkt.ethType().is(new byte[]{0x8,0})) {
            byte[] bs = pkt.ipSrc().get();
            IPv4Address src = IPv4Address.of(bs);
            byte[] bs1 = pkt.ipDst().get();
            IPv4Address dst = IPv4Address.of(bs1);

            if(src.toString().equals("10.0.0.1") && dst.toString().equals("10.0.0.2")){
                pkt.setRoute(path1);
                pkt.addRoute(tap);
                return true;
            } else if(src.toString().equals("10.0.0.2") && dst.toString().equals("10.0.0.1")){
                pkt.setRoute(path2);
                pkt.addRoute(tap);
                return true;
            }

            TrackedMap<IPv4Address, MapleTopology.PortId> iPv4HostTable = db.getIPv4HostTable();
            MapleTopology.PortId srcPort = iPv4HostTable.get(src);
            MapleTopology.PortId dstPort = iPv4HostTable.get(dst);
            MapleTopology topo = db.getTopology();
            pkt.setRoute(topo.shortestPath(srcPort,dstPort));
            return true;
        }
        return false;
    }

}