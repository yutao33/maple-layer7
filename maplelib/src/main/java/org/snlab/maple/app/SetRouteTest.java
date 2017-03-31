/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.api.MapleEnv;
import org.snlab.maple.api.MaplePacket;
import org.snlab.maple.packet.MaplePacketImpl;


/**
 * SetRouteTest.
 * mn --topo=tree,fanout=2,depth=2 --controller=remote,port=6653 --switch=ovs,protocols=OpenFlow13 --mac
 * h1 h2 ~ h3 h4
 */
public class SetRouteTest extends MapleAppBase{
    private static final String[] path1={"openflow:2:3","openflow:1:2"};
    private static final String[] path2={"openflow:3:3","openflow:1:1"};
    @Override
    public boolean onPacket(MaplePacket pkt, MapleEnv env) {
        pkt.setRoute(path1);
        pkt.addRoute(path2);
        return true;
    }
}
