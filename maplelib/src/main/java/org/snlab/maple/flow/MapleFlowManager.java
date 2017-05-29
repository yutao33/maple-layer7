/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.flow;

import org.snlab.maple.flow.flowinfo.AbstractFlowInfo;
import org.snlab.maple.packet.MaplePacket;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class MapleFlowManager {

    private final static Logger LOG = Logger.getLogger(MapleFlowManager.class.toString());


    private Map<IPFiveTuple,MapleFlow> flowMap;

    public MapleFlow findFlow(MaplePacket pkt){
        //MapleFlow mapleFlow = flowMap.get(key);
        //return mapleFlow;
        return null;
    }

    public void updateFlow(IPFiveTuple key, AbstractFlowInfo flowInfo){
        LOG.info(flowInfo.toString());
    }

}
