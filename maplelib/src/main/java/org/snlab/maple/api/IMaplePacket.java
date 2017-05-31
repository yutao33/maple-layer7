/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api;

import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.flow.MapleFlowBroker;
import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.packet.parser.Ethernet;
import org.snlab.maple.rule.route.Forward;

public interface IMaplePacket {

    Ethernet _getFrame();

    MapleTopology.PortId _getInPortId();

    MaplePacket.PktFieldMaskable ethSrc();

    MaplePacket.PktFieldMaskable ethDst();

    MaplePacket.PktField ethType();

    MaplePacket.PktFieldMaskable ipSrc();

    MaplePacket.PktFieldMaskable ipDst();

    MaplePacket.PktField ipProto();

    MaplePacket.PktField tcpSPort();

    MaplePacket.PktField tcpDPort();

    MaplePacket.PktField udpSPort();

    MaplePacket.PktField udpDPort();

    MaplePacket.PktInPort inport();

    boolean ipSrcOrDstIs(byte[] ip);

    MapleFlowBroker flow();

    void setRoute(String... path);

    void addRoute(String... path);

    void setRoute(Forward... path);

    void addRoute(Forward... path);
}
