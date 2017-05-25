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

public class IPv4Switch extends MapleAppBase {

    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {


        if(pkt.ethType().is(new byte[]{0x8,0})) {

            byte[] bs = pkt.ipSrc().get();
            IPv4Address src = IPv4Address.of(bs);
            byte[] bs1 = pkt.ipDst().get();
            IPv4Address dst = IPv4Address.of(bs1);
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

