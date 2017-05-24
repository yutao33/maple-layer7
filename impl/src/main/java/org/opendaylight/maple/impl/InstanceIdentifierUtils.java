/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maple.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.port.statistics.rev131214.FlowCapableNodeConnectorStatisticsData;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;


public final class InstanceIdentifierUtils {

    private InstanceIdentifierUtils() {
        throw new UnsupportedOperationException("Utility class should never be instantiated");
    }


    public static InstanceIdentifier<Node> genNodeIId(final NodeConnectorRef nodeConnectorRef) {
        return nodeConnectorRef.getValue().firstIdentifierOf(Node.class);
    }


    public static InstanceIdentifier<Table> genFlowTableIId(final NodeConnectorRef nodeConnectorRef,
                                                            final TableKey flowTableKey) {
        return genNodeIId(nodeConnectorRef).builder().augmentation(FlowCapableNode.class)
                .child(Table.class, flowTableKey).build();
    }


    public static InstanceIdentifier<Flow> genFlowIId(final NodeConnectorRef nodeConnectorRef,
                                                      final TableKey flowTableKey, final FlowKey flowKey) {
        return genFlowTableIId(nodeConnectorRef, flowTableKey).child(Flow.class, flowKey);
    }

    public InstanceIdentifier<FlowCapableNodeConnectorStatisticsData> genFlowStatisticsIId(){
        return InstanceIdentifier.builder(Nodes.class)
                .child(Node.class)
                .child(NodeConnector.class).augmentation(FlowCapableNodeConnectorStatisticsData.class).build();
    }

    public static InstanceIdentifier<Node> genNodeIId(final String nodeId){
        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(Nodes.class)
                .child(Node.class, new NodeKey(new org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId(nodeId)))
                .build();
        return iid;
    }

}
