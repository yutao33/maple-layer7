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
import org.snlab.maple.env.TrackedMap;
import org.snlab.maple.packet.types.MacAddress;
import org.snlab.maple.rule.route.Forward;

public class ArpHandler extends MapleAppBase {
    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {

        if(pkt.ethType().is(new byte[]{0x8,0x6})) {

            MapleTopology topo = db.getTopology();

            Forward[] spanningTree = db.getTopology().spanningTree();
            pkt.setRoute(spanningTree);

            if (pkt.inport().in(topo.getBorderPorts())) {

                TrackedMap<MacAddress, MapleTopology.PortId> macHostTable = db.getMacHostTable();
                MacAddress src = pkt._getFrame().getSourceMACAddress();
                MapleTopology.PortId inPortId = pkt._getInPortId();
                macHostTable.put(src,inPortId);

                //System.out.println("src="+src+" inport="+inPortId);

                pkt.addRoute(Forward.PUNT);
            }

            return true;
        }

        return false;
    }
}
