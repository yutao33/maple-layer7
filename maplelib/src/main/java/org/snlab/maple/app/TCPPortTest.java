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
import org.snlab.maple.packet.types.IPv4Address;

public class TCPPortTest extends MapleAppBase {

    private static final String[] path1 = {null, "openflow:1:3", null, "openflow:2:2", null, "openflow:4:1"};
    private static final String[] path2 = {null, "openflow:4:4", null, "openflow:2:1", null, "openflow:1:1"};
    private static final String[] tap = {null, "openflow:2:3"};

    private static final byte[] serverip = IPv4Address.of("10.0.0.2").getBytes();
    private static final byte[] serverport = new byte[]{0,80};

    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {

        if(pkt.ethType().is(new byte[]{0x8,0})) {
            if (pkt.ipProto().is(new byte[]{6})) {

                if (pkt.ipDst().is(serverip)) {
                    if(pkt.tcpDPort().is(serverport)) {
                        pkt.setRoute(path1);
                        pkt.addRoute(tap);
                        return true;
                    } else {
                        return false;
                    }
                } else if (pkt.ipSrc().is(serverip)) {
                    if(pkt.tcpSPort().is(serverport)) {
                        pkt.setRoute(path2);
                        pkt.addRoute(tap);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

}