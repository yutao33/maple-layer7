/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.api.IMapleDataBroker;
import org.snlab.maple.api.IMaplePacket;
import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.flow.flowinfo.FlowType;
import org.snlab.maple.packet.types.IPv4Address;

public class L7Test extends MapleAppBase {

    private static final String[] H12_LOW_PATH = {null, "openflow:1:3",null, "openflow:2:2",null, "openflow:4:1"};
    private static final String[] H12_HIGH_PATH = {null, "openflow:1:4", null,"openflow:3:2", null, "openflow:4:1"};
    private static final String[] H21_LOW_PATH = {null, "openflow:4:4",null, "openflow:2:1", null, "openflow:1:1"};
    private static final String[] H21_HIGH_PATH = {null, "openflow:4:5", null, "openflow:3:1", null, "openflow:1:1"};
    private static final String[] tap = {null, "openflow:2:3"};

    private static final byte[] H2_IP = IPv4Address.of("10.0.0.2").getBytes();

    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {

        if(pkt.ethType().is(IPv4) && pkt.ipProto().is(TCP) && pkt.ipSrcOrDstIs(H2_IP)) {
            if(pkt.flow().type()== FlowType.HTTP){
                String url = pkt.flow().httpRequestURL();
                String method = pkt.flow().httpMethod();
                if(method.equals("GET") && url.matches("/.+\\.dat")){
                    staticRouteHigh(pkt);
                } else {
                    staticRouteLow(pkt);
                }
            } else if(pkt.flow().type()==FlowType.UNKNOWN){
                staticRouteLow(pkt);
                pkt.addRoute(tap);
            }
        }
        return false;

    }

    private void staticRouteHigh(IMaplePacket pkt) {
        if(pkt.ipDst().is(H2_IP)){
            pkt.setRoute(H12_HIGH_PATH);
        } else {
            pkt.setRoute(H21_HIGH_PATH);
        }
    }

    private void staticRouteLow(IMaplePacket pkt) {
        if(pkt.ipDst().is(H2_IP)){
            pkt.setRoute(H12_LOW_PATH);
        } else {
            pkt.setRoute(H21_LOW_PATH);
        }
    }

}