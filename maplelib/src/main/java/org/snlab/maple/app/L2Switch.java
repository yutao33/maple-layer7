/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.api.IMapleDataBroker;
import org.snlab.maple.api.IMaplePacket;
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.env.MapleTopology.PortId;
import org.snlab.maple.env.TrackedMap;
import org.snlab.maple.packet.types.MacAddress;

import java.util.HashMap;
import java.util.Map;

public class L2Switch extends MapleAppBase {

    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {

        byte[] bs = pkt.ethSrc().get();
        MacAddress src = MacAddress.of(bs);
        bs = pkt.ethDst().get();
        MacAddress dst = MacAddress.of(bs);
        if(dst.isBroadcast()){
            pkt.setRoute(db.getTopology().spanningTree());
        } else {
            TrackedMap<MacAddress, MapleTopology.PortId> macHostTable = db.getMacHostTable();
            MapleTopology.PortId srcPort = macHostTable.get(src);
            MapleTopology.PortId dstPort = macHostTable.get(dst);
            MapleTopology topology = db.getTopology();
            pkt.setRoute(topology.shortestPath(srcPort, dstPort));
        }
        return true;

    }

}
