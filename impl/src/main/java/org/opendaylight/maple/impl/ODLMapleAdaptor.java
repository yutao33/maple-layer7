/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.port.statistics.rev131214.FlowCapableNodeConnectorStatisticsData;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.IMapleAdaptor;
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.tracetree.TraceTree;

import java.util.List;

public class ODLMapleAdaptor implements IMapleAdaptor {

    private static final Logger LOG = LoggerFactory.getLogger(ODLMapleAdaptor.class);

    private final DataBroker dataBroker;

    private final SalFlowService salFlowService;

    private TraceTreeWriter traceTreeWriter;

    private FlowManager flowManager;

    public ODLMapleAdaptor(DataBroker dataBroker, SalFlowService salFlowService) {
        this.dataBroker = dataBroker;
        this.salFlowService = salFlowService;
        this.traceTreeWriter = new TraceTreeWriter(dataBroker);
        this.flowManager = new FlowManager(dataBroker);
    }

    @Override
    public void sendPacket(MapleTopology.PortId port, MaplePacket pkt) {

        InstanceIdentifier<FlowCapableNodeConnectorStatisticsData> iid = InstanceIdentifier
                .builder(Nodes.class)
                .child(Node.class)
                .child(NodeConnector.class).augmentation(FlowCapableNodeConnectorStatisticsData.class).build();

    }

    @Override
    public void outPutTraceTree(TraceTree traceTree, MaplePacket pkt) {

        traceTreeWriter.writeTraceTree(traceTree,pkt);

    }

    @Override
    public void updateRules(List<MapleRule> rules) {

        flowManager.updateRules(rules);

    }

}
