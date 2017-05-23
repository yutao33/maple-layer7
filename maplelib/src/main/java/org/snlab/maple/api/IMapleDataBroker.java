/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api;

import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.env.TrackedMap;
import org.snlab.maple.packet.types.IPv4Address;
import org.snlab.maple.packet.types.MacAddress;


public interface IMapleDataBroker {
    MapleTopology getTopology();

    TrackedMap<MacAddress, MapleTopology.PortId> getMacHostTable();

    TrackedMap<IPv4Address, MapleTopology.PortId> getIPv4HostTable();

    Object readData(String url);


}
