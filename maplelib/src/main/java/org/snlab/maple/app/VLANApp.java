/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.packet.MaplePacket;

import java.util.Map;

public class VLANApp {

    Map<MapleTopology.Port, Short> Port2VlanidTable;
    Map<MapleTopology.Port, byte[]> Port2MacaddrTable;


    String[] boundaryport() {
        return null;
    }


    boolean onPacket(MaplePacket pkt) {
        if (pkt.ingress().in(boundaryport())) {


        }

        return true;
    }
}
