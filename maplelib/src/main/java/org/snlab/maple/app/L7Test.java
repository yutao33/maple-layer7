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
import org.snlab.maple.flow.MapleFlow;
import org.snlab.maple.packet.types.IPv4Address;

public class L7Test extends MapleAppBase {

    private static final String[] path1 = {null, "openflow:1:3", null, "openflow:2:2", null, "openflow:4:1"};
    private static final String[] path2 = {null, "openflow:4:4", null, "openflow:2:1", null, "openflow:1:1"};
    private static final String[] tap = {null, "openflow:2:3"};

    private static final byte[] H2_IP = IPv4Address.of("10.0.0.2").getBytes();

    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {

        if(pkt.ethType().is(IPv4) && pkt.ipProto().is(TCP)) {
            MapleFlow flow = pkt.flow();
            return true;
        }
        return false;
    }

}