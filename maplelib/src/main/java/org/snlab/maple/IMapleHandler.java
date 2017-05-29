/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple;

import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.flow.IPFiveTuple;
import org.snlab.maple.flow.flowinfo.AbstractFlowInfo;
import org.snlab.maple.rule.MaplePacketInReason;

import java.util.List;

public interface IMapleHandler {

    void onPacket(String inportId, byte[] payload, MaplePacketInReason reason);


    void onTopologyChanged(List<MapleTopology.Element> putList,
                           List<MapleTopology.Element> deleteList);

    void onFlowChanged(IPFiveTuple key, AbstractFlowInfo flowInfo);
}
