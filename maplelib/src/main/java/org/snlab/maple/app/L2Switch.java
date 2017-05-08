/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.api.IMapleDataBroker;
import org.snlab.maple.api.IMaplePacket;
import org.snlab.maple.env.MapleTopology.PortId;

import java.util.HashMap;
import java.util.Map;

public class L2Switch extends MapleAppBase {

    Map<Long, PortId> hostTable = new HashMap<>();

    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {

        long ethSrc = 0;

        if (!hostTable.containsKey(ethSrc)) {

        }

        return false;
    }

}
