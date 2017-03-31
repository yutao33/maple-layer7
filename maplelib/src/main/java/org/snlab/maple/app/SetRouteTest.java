/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.packet.MaplePacket;

public class SetRouteTest extends MapleAppBase{
    public static final String[] path1={"openflow:2:1","openflow:1:1"};
    public static final String[] path2={"openflow:3:1","openflow:2:2"};
    @Override
    public boolean onPacket(MaplePacket pkt) {
        pkt.setRoute(path1);
        pkt.addRoute(path2);
        return true;
    }
}
