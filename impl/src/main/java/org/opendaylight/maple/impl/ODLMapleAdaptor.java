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
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.IMapleAdaptor;
import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.packet.OutPutPacket;
import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.tracetree.TraceTree;

import java.util.List;

public class ODLMapleAdaptor implements IMapleAdaptor {

    private static final Logger LOG = LoggerFactory.getLogger(ODLMapleAdaptor.class);

    private final DataBroker dataBroker;

    private final SalFlowService salFlowService;

    private final PacketProcessingService packetProcessingService;

    private TraceTreeWriter traceTreeWriter;

    private SalFlowManager flowManager1;

    private PacketSender packetSender;

    public ODLMapleAdaptor(DataBroker dataBroker, SalFlowService salFlowService, PacketProcessingService packetProcessingService) {
        this.dataBroker = dataBroker;
        this.salFlowService = salFlowService;
        this.packetProcessingService = packetProcessingService;
        this.traceTreeWriter = new TraceTreeWriter(dataBroker);
        this.flowManager1 = new SalFlowManager(salFlowService);
        this.packetSender = new PacketSender(packetProcessingService);
    }

    @Override
    public void sendPacket(List<OutPutPacket> outputPackets) {

        for (OutPutPacket outputPacket : outputPackets) {
            packetSender.sendPacket(outputPacket);
        }

    }

    @Override
    public void outPutTraceTree(TraceTree traceTree, MaplePacket pkt) {

        traceTreeWriter.writeTraceTree(traceTree,pkt);

    }

    @Override
    public void updateRules(List<MapleRule> rules) {

        flowManager1.updateRules(rules);

    }

}
