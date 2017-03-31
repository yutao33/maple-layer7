/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.port.statistics.rev131214.FlowCapableNodeConnectorStatisticsData;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.snlab.maple.IMapleAdaptor;

public class ODLMapleAdaptor implements IMapleAdaptor {

    private final DataBroker dataBroker;

    public ODLMapleAdaptor(DataBroker dataBroker){
        this.dataBroker = dataBroker;
    }

    @Override
    public void sendPacket() {

        InstanceIdentifier<FlowCapableNodeConnectorStatisticsData> iid = InstanceIdentifier
                .builder(Nodes.class)
                .child(Node.class)
                .child(NodeConnector.class).augmentation(FlowCapableNodeConnectorStatisticsData.class).build();


    }

    @Override
    public void installPath() {

    }

    @Override
    public void deletePath() {

    }

    @Override
    public void installRule() {

    }

    @Override
    public void deleteRule() {

    }

    @Override
    public void resetWriteTransaction() {

    }

    @Override
    public void submitTransaction() {

    }

    @Override
    public void outputtracetree() {

    }
}
