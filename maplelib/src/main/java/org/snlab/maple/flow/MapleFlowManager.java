/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.flow;

import org.snlab.maple.env.IReExecHandler;
import org.snlab.maple.flow.flowinfo.AbstractFlowInfo;
import org.snlab.maple.flow.flowinfo.FlowType;
import org.snlab.maple.flow.flowinfo.UNKNOWNFlowInfo;
import org.snlab.maple.packet.MaplePacket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class MapleFlowManager {

    private final static Logger LOG = Logger.getLogger(MapleFlowManager.class.toString());


    private Map<IPFiveTuple,AbstractFlowInfo> flowMap = new HashMap<>();
    private IReExecHandler reExecHandler;

    public MapleFlowManager(IReExecHandler reExecHandler) {
        this.reExecHandler = reExecHandler;
    }

    public synchronized MapleFlowBroker findFlow(MaplePacket pkt, IPFiveTuple key){
        AbstractFlowInfo flowInfo = flowMap.get(key);
        if(flowInfo==null){
            flowInfo=new UNKNOWNFlowInfo();
            flowMap.put(key,flowInfo);
        }
        return new MapleFlowBroker(flowInfo,pkt);
    }

    public synchronized void updateFlow(IPFiveTuple key, AbstractFlowInfo flowInfo){

        updateSingleFlow(key,flowInfo);

        if(flowInfo.getType(null).equals(FlowType.HTTP)){
            updateSingleFlow(key.reverse(),flowInfo);
        }

        LOG.info(flowInfo.toString());
    }

    private void updateSingleFlow(IPFiveTuple key, AbstractFlowInfo flowInfo) {
        AbstractFlowInfo oldFlowInfo = flowMap.get(key);
        Set<MaplePacket> pkts=null;
        if(oldFlowInfo.getType(null).equals(flowInfo.getType(null))){
            pkts = oldFlowInfo.updateAndreturnTrack(flowInfo);
        } else {
            flowMap.put(key, flowInfo);
            pkts = oldFlowInfo.getAndremoveAllTrack();
        }
        for (MaplePacket pkt : pkts) {
            reExecHandler.onReExec(pkt);
        }
    }

}
