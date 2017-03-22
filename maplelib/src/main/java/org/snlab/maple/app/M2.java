/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;


import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.api.MaplePacket;
import org.snlab.maple.api.packet.Ethernet;
import org.snlab.maple.api.packet.IPv4;

public class M2 extends MapleAppBase {

	private static final String TOPO_URL       = "/root/network-topology/topology";
	private static final String HOST_TABLE_URL = "/root/host-table";

	private static final String H1     = "10.0.0.1";
	private static final int    H1_IP  = IPv4.toIPv4Address(H1);

	private static final String H2     = "10.0.0.2";
	private static final int    H2_IP  = IPv4.toIPv4Address(H2);

	private static final int HTTP_PORT = 80;

	private static final String[] H12_HIGH_PATH = { H1, "openflow:1:3", "openflow:2:2", "openflow:4:1" };
	private static final String[] H12_LOW_PATH  = { H1, "openflow:1:4", "openflow:3:2", "openflow:4:1" };
	private static final String[] H21_HIGH_PATH = { H2, "openflow:4:4", "openflow:2:1", "openflow:1:1" };
	private static final String[] H21_LOW_PATH  = { H2, "openflow:4:5", "openflow:3:1", "openflow:1:1" };

	public void staticRoute(MaplePacket pkt) {
		// H1 (client) -> H2 (server)
		if ( pkt.IPv4SrcIs(H1_IP) && pkt.IPv4DstIs(H2_IP) ) {

			String[] path = null;

			if ( ! pkt.TCPDstPortIs(HTTP_PORT) ) {  // All non HTTP IP, e.g., UDP, PING, SSH
				path = H12_LOW_PATH; 
			} else {                                // Only HTTP traffic
				path = H12_HIGH_PATH;
			}

			// ***TODO***: Need to agree on either Route or Path, not both
			pkt.setRoute(path);

			// Reverse: H2 -> H1
		} else if ( pkt.IPv4SrcIs(H2_IP) && pkt.IPv4DstIs(H1_IP) ) {

				String[] path = null;

				if ( ! pkt.TCPSrcPortIs(HTTP_PORT) ) {
					path = H21_LOW_PATH;
				} else {
					path = H21_HIGH_PATH;
				}
				pkt.setRoute(path);

			// Other host pairs
			} 
	} // end of staticRoute

	@Override
	public boolean onPacket(MaplePacket pkt) {

		int ethType = pkt.ethType();

		// For IPv4 traffic only
		if ( ethType == Ethernet.TYPE_IPv4 ) {
			staticRoute( pkt );
			
			if (pkt.route() == null) {
//				if (pkt.TCPDstPortIs(80) || pkt.TCPSrcPortIs(80)) {
//					int srcIP = pkt.IPv4Src();
//					int dstIP = pkt.IPv4Dst();
//
//					Topology topo = (Topology) readData(TOPO_URL);
//					Map<Integer, Port> hostTable = (Map<Integer, Port>) readData(HOST_TABLE_URL);
//					Port srcPort = hostTable.get(srcIP);
//					Port dstPort = hostTable.get(dstIP);
//
//					pkt.setRoute(MapleUtil.shortestPath(topo.getLink(), srcPort, dstPort));
//				} else {
//					pkt.setRoute(Route.DROP);
//				}
			}

		}

		return false;

	}
	
}

