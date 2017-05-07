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
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;


public final class InstanceIdentifierUtils {

    private InstanceIdentifierUtils() {
        throw new UnsupportedOperationException("Utility class should never be instantiated");
    }


    /**
     * @param nodeConnectorRef
     * @return
     */
    public static InstanceIdentifier<Node> genNodeIId(final NodeConnectorRef nodeConnectorRef) {
        return nodeConnectorRef.getValue().firstIdentifierOf(Node.class);
    }

    /**
     * @param nodeConnectorRef
     * @param flowTableKey
     * @return
     */
    public static InstanceIdentifier<Table> genFlowTableIId(final NodeConnectorRef nodeConnectorRef,
                                                            final TableKey flowTableKey) {
        return genNodeIId(nodeConnectorRef).builder().augmentation(FlowCapableNode.class)
                .child(Table.class, flowTableKey).build();
    }

    /**
     * @param nodeConnectorRef
     * @param flowTableKey
     * @param flowKey
     * @return
     */
    public static InstanceIdentifier<Flow> genFlowIId(final NodeConnectorRef nodeConnectorRef,
                                                      final TableKey flowTableKey, final FlowKey flowKey) {
        return genFlowTableIId(nodeConnectorRef, flowTableKey).child(Flow.class, flowKey);
    }

}
