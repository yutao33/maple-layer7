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
 * InPortTest.
 * mn --topo=tree,fanout=2,depth=2 --controller=remote,port=6653 --switch=ovs,protocols=OpenFlow13 --mac
 */
public class InPortTest extends MapleAppBase {
    private static final String[] path1 = {"openflow:2:1", "openflow:2:3", "openflow:1:1","openflow:1:2", "openflow:3:3","openflow:3:1"};
    private static final String[] path2 = {"openflow:3:1", "openflow:3:3", "openflow:1:2","openflow:1:1", "openflow:2:3","openflow:2:1"};

    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleEnv env) {
        if (pkt.inport().in(Forward.extractInPort(path1))) {
            pkt.setRoute(path1);
        } else if (pkt.inport().in(Forward.extractInPort(path2))) {
            pkt.setRoute(path2);
        } else {
            pkt.setRoute(Forward.DROP);
        }
        return true;
    }
}
