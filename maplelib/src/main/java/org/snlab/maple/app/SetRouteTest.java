/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.api.IMapleEnv;
import org.snlab.maple.api.IMaplePacket;
import org.snlab.maple.rule.route.Forward;


/**
 * SetRouteTest.
 * mn --topo=tree,fanout=2,depth=2 --controller=remote,port=6653 --switch=ovs,protocols=OpenFlow13 --mac
 * h1 h2 ~ h3 h4
 */
public class SetRouteTest extends MapleAppBase {
    private static final String[] path1 = {null, "openflow:2:3", null, "openflow:1:2",null, "openflow:3:1"};
    private static final String[] path2 = {null, "openflow:3:3", null, "openflow:1:1",null, "openflow:2:1"};

    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleEnv env) {
        if (pkt.ethType().is(new byte[]{0x8, 0}) ||
                pkt.ethType().is(new byte[]{8, 6})) {
            pkt.setRoute(path1);
            pkt.addRoute(path2);
        } else {
            pkt.setRoute(Forward.DROP);
        }
        return true;
    }
}
