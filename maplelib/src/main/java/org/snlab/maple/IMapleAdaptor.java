/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.snlab.maple;


import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.packet.OutPutPacket;
import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.tracetree.TraceTree;

import java.util.List;

public interface IMapleAdaptor {
    void sendPacket(List<OutPutPacket> outputPackets);

    void updateRules(List<MapleRule> rules);

    void outPutTraceTree(TraceTree traceTree, MaplePacket pkt);

}
