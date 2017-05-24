/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import org.opendaylight.openflowplugin.api.OFConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnectorKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.ConnectionCookie;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.TransmitPacketInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.TransmitPacketInputBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.packet.OutPutPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketSender {

    private static final Logger LOG = LoggerFactory.getLogger(PacketSender.class);

    private final PacketProcessingService packetProcessingService;

    private AtomicInteger flowCookieInc = new AtomicInteger(0x090000);

    public PacketSender(PacketProcessingService packetProcessingService) {

        this.packetProcessingService = packetProcessingService;

    }

    public void sendPacket(OutPutPacket pkt){

        List<Action> list = new ArrayList<>();
        OutputActionBuilder outputBuilder = new OutputActionBuilder();
        OutputActionCaseBuilder outputCaseBuilder = new OutputActionCaseBuilder();
        ActionBuilder actionBuilder = new ActionBuilder();
        int i=0;
        for (MapleTopology.PortId portId : pkt.getPortIdList()) {
            OutputAction outputAction = outputBuilder
                    .setOutputNodeConnector(new Uri(portId.toString()))
                    .setMaxLength(OFConstants.OFPCML_NO_BUFFER)
                    .build();
            OutputActionCase outputActionCase = outputCaseBuilder
                    .setOutputAction(outputAction)
                    .build();
            Action action = actionBuilder.setAction(outputActionCase).setOrder(i++).build();
            list.add(action);
        }

        InstanceIdentifier<Node> nodeIId = InstanceIdentifierUtils.genNodeIId(pkt.getNodeId().toString());
        InstanceIdentifier<NodeConnector> portIId = nodeIId.builder().child(NodeConnector.class, new NodeConnectorKey(new NodeConnectorId("openflow:0:0"))).build();
        TransmitPacketInput packet = new TransmitPacketInputBuilder()
                .setBufferId(OFConstants.OFP_NO_BUFFER)
                .setConnectionCookie(new ConnectionCookie((long)(flowCookieInc.getAndIncrement())))
                .setPayload(pkt.getPacket())
                .setNode(new NodeRef(nodeIId))
                .setEgress(new NodeConnectorRef(portIId))  //a bug
                .setAction(list)
                .build();
        packetProcessingService.transmitPacket(packet);

        LOG.info(pkt.toString());
    }
}
