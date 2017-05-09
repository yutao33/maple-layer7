/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import org.snlab.maple.packet.MaplePacket;

import java.util.HashSet;
import java.util.Set;

public class TrackSet {
    private Set<MaplePacket> pkts = new HashSet<>();

    public void track(MaplePacket pkt){
        pkt.addTracked(this);
        pkts.add(pkt);
    }

    public void remove(MaplePacket pkt){
        pkts.remove(pkt);
    }

    public void reexec(IReExecHandler handler){
        for (MaplePacket pkt : pkts) {
            handler.onReExec(pkt);
        }
    }
}
