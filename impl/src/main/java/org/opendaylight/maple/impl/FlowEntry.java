/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.snlab.maple.env.MapleTopology;

import javax.annotation.concurrent.Immutable;


@Immutable
class FlowEntry {
    final MapleTopology.NodeId nodeId;
    final MapleTopology.PortId portId;
    final InstanceIdentifier<Flow> flowPath;

    FlowEntry(MapleTopology.NodeId nodeId, MapleTopology.PortId portId, InstanceIdentifier<Flow> flowPath) {
        this.nodeId = nodeId;
        this.portId = portId;
        this.flowPath = flowPath;
    }
}
