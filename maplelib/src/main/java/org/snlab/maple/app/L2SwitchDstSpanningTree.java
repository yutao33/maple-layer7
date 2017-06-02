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
import org.snlab.maple.packet.types.MacAddress;
import org.snlab.maple.rule.route.Forward;

public class L2SwitchDstSpanningTree extends MapleAppBase {

    private final static byte[] multicastmask =new byte[]{1,0,0,0,0,0};

    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {

        if(pkt.ethDst().mask(multicastmask).is(multicastmask)){
            pkt.setRoute(db.getTopology().spanningTree());
        } else {
            byte[] bs = pkt.ethDst().get();
            MacAddress dst = MacAddress.of(bs);
            TrackedMap<MacAddress, MapleTopology.PortId> macHostTable = db.getMacHostTable();
            MapleTopology.PortId dstPort = macHostTable.get(dst);
            MapleTopology topology = db.getTopology();
            Forward[] ret = topology.spanningTreeToHost(dstPort);
            pkt.setRoute(ret);
        }
        return true;

    }

}
