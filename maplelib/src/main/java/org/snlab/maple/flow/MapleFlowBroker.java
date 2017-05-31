/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.flow;

import com.google.common.base.Preconditions;
import org.snlab.maple.flow.flowinfo.AbstractFlowInfo;
import org.snlab.maple.flow.flowinfo.FlowType;
import org.snlab.maple.flow.flowinfo.HTTPFlowInfo;
import org.snlab.maple.flow.flowinfo.SSHFlowInfo;
import org.snlab.maple.packet.MaplePacket;

public class MapleFlowBroker {

    private final AbstractFlowInfo flowInfo;

    private final MaplePacket pkt;

    public MapleFlowBroker(AbstractFlowInfo flowInfo, MaplePacket pkt) {
        this.flowInfo = flowInfo;
        this.pkt = pkt;
    }

    public FlowType type(){
        return flowInfo.getType(this.pkt);
    }

    private HTTPFlowInfo http(){
        Preconditions.checkState(flowInfo instanceof HTTPFlowInfo);
        return (HTTPFlowInfo)flowInfo;
    }

    private SSHFlowInfo ssh(){
        Preconditions.checkState(flowInfo instanceof SSHFlowInfo);
        return (SSHFlowInfo)flowInfo;
    }

    public String httpMethod(){
        return http().getMethod(this.pkt);
    }

    public String httpRequestURL(){
        return http().getRequestURL(this.pkt);
    }
}
