/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.packet;

import com.google.common.base.Preconditions;
import org.snlab.maple.env.MapleTopology;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Immutable
public class OutPutPacket {
    private final MapleTopology.NodeId nodeId;
    private final List<MapleTopology.PortId> portIdList;
    private final byte[] packet;


    public OutPutPacket(@Nonnull MapleTopology.NodeId nodeId,
                        @Nonnull List<MapleTopology.PortId> portIdList,
                        @Nonnull byte[] packet) {
        this.nodeId = nodeId;
        for (MapleTopology.PortId portId : portIdList) {
            Preconditions.checkArgument(portId.getNodeId().equals(nodeId));
        }
        this.portIdList = new ArrayList<>(portIdList);
        this.packet = packet;
    }

    public MapleTopology.NodeId getNodeId() {
        return nodeId;
    }

    public List<MapleTopology.PortId> getPortIdList() {
        return portIdList;
    }

    public byte[] getPacket() {
        return packet;
    }

    @Override
    public String toString() {
        return "OutPutPacket{" +
                "portIdList=" + portIdList +
                ", packet=" + Arrays.toString(packet) +
                '}';
    }
}
