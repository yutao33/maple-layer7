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
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.env.TrackedMap;
import org.snlab.maple.packet.types.IPv4Address;
import org.snlab.maple.rule.route.Forward;

public class M2 extends MapleAppBase {

    private static final String H1 = "10.0.0.1";
    private static final byte[] H1_IP = IPv4Address.of(H1).getBytes();

    private static final String H2 = "10.0.0.2";
    private static final byte[] H2_IP = IPv4Address.of(H2).getBytes();

    private static final byte[] HTTPPORT=new byte[]{0,80};
    private static final byte[] TCP=new byte[]{6};

    private static final String[] H12_HIGH_PATH = {null, "openflow:1:3",null, "openflow:2:2",null, "openflow:4:1"};
    private static final String[] H12_LOW_PATH = {null, "openflow:1:4", null,"openflow:3:2", null, "openflow:4:1"};
    private static final String[] H21_HIGH_PATH = {null, "openflow:4:4",null, "openflow:2:1", null, "openflow:1:1"};
    private static final String[] H21_LOW_PATH = {null, "openflow:4:5", null, "openflow:3:1", null, "openflow:1:1"};

	public boolean staticRoute(IMaplePacket pkt) {

		if ( pkt.ipSrc().is(H1_IP) && pkt.ipDst().is(H2_IP) ) {
			if (pkt.ipProto().is(TCP) && pkt.tcpDPort().is(HTTPPORT) ) {
				pkt.setRoute(H12_HIGH_PATH);
			} else {
				pkt.setRoute(H12_LOW_PATH);
			}
			return true;
		} else if ( pkt.ipSrc().is(H2_IP) && pkt.ipDst().is(H1_IP) ) {
            if (pkt.ipProto().is(TCP) && pkt.tcpSPort().is(HTTPPORT)) {
                pkt.setRoute(H21_HIGH_PATH);
            } else {
                pkt.setRoute(H21_LOW_PATH);
            }
            return true;
        }
        return false;
	}

	@Override
	public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {
		if ( pkt.ethType().is(new byte[]{8,0}) ) {
			boolean ret = staticRoute( pkt );
			if (! ret ) {
				if (pkt.ipProto().is(TCP) && pkt.tcpDPort().is(HTTPPORT)
                        || pkt.ipProto().is(TCP) && pkt.tcpSPort().is(HTTPPORT)) {
                    byte[] bs = pkt.ipSrc().get();
                    IPv4Address src = IPv4Address.of(bs);
                    byte[] bs1 = pkt.ipDst().get();
                    IPv4Address dst = IPv4Address.of(bs1);
                    TrackedMap<IPv4Address, MapleTopology.PortId> iPv4HostTable = db.getIPv4HostTable();
                    MapleTopology.PortId srcPort = iPv4HostTable.get(src);
                    MapleTopology.PortId dstPort = iPv4HostTable.get(dst);
                    MapleTopology topo = db.getTopology();
                    pkt.setRoute(topo.shortestPath(srcPort,dstPort));
				} else {
					pkt.setRoute(Forward.DROP);
				}
			}

		}
		return true;
	}

}

