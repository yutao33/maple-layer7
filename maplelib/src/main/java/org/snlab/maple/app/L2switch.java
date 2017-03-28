/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.api.MaplePacket;
import org.snlab.maple.api.network.MapleTopology.Port;
import org.snlab.maple.api.packet.MACAddress;

import java.util.HashMap;
import java.util.Map;

public class L2switch extends MapleAppBase {

    Map<MACAddress,Port> hostTable=new HashMap<MACAddress,Port>();

    @Override
    public boolean onPacket(MaplePacket pkt) {

        MACAddress ethSrc = pkt.ethSrc().getValue();

        if(!hostTable.containsKey(ethSrc)){
            hostTable.put(ethSrc,pkt.getIngress_0());
        }

        if (pkt.ipSrc().is(new byte[]{33})) {
            pkt.setRoute(null);
            return true;
        } else {
            pkt.setRoute(null);
            return true;
        }


    }

}
