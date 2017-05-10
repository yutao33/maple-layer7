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
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class TrackSet {
    private static final Logger LOG = Logger.getLogger(TrackSet.class.toString());

    private Set<MaplePacket> pkts = new HashSet<>();

    public synchronized void track(MaplePacket pkt){
        if(pkt==null){
            LOG.info("track pkt null");
            return;
        }
        pkt.addTracked(this);
        pkts.add(pkt);
    }

    public synchronized void remove(MaplePacket pkt){
        if(pkt==null){
            LOG.info("remove pkt null");
            return;
        }
        pkts.remove(pkt);
    }

    public synchronized void reexec(IReExecHandler handler){
        Iterator<MaplePacket> iterator = pkts.iterator();
        while (iterator.hasNext()) {
            MaplePacket next = iterator.next();
            next.removeTracked(this);
            handler.onReExec(next);
            iterator.remove();
        }
    }
}
