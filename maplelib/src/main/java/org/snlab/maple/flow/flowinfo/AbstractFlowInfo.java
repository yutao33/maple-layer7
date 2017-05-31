/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.flow.flowinfo;

import org.snlab.maple.packet.MaplePacket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFlowInfo {
    protected FlowType type;

    protected Set<MaplePacket> typeTrackSet= Collections.synchronizedSet(new HashSet<MaplePacket>());

    public AbstractFlowInfo(FlowType type) {
        this.type = type;
    }

    public FlowType getType(MaplePacket pkt) {
        if(pkt!=null) {
            typeTrackSet.add(pkt);
        }
        return type;
    }

    public Set<MaplePacket> getAndremoveAllTrack(){
        HashSet<MaplePacket> ret = new HashSet<>(typeTrackSet);
        typeTrackSet.clear();
        return ret;
    }

    public Set<MaplePacket> updateAndreturnTrack(AbstractFlowInfo flowInfo){
        return Collections.EMPTY_SET;
    }
}
