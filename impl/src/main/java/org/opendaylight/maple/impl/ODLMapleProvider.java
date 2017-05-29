/*
 * Copyright © 2017 SNLab and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maple.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.controller.md.sal.binding.api.NotificationService;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.port.rev130925.flow.capable.port.State;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.flow.rev170512.Baseflow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.flow.rev170512.baseflow.FlowMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow.applications.lldp.speaker.rev141023.LldpSpeakerService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow.applications.lldp.speaker.rev141023.SetLldpFloodIntervalInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow.applications.lldp.speaker.rev141023.SetLldpFloodIntervalInputBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.IMapleHandler;
import org.snlab.maple.MapleSystem;

import java.util.concurrent.ExecutionException;

public class ODLMapleProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ODLMapleProvider.class);

    private NotificationPublishService publishService;
    private DataBroker dataBroker;
    private RpcProviderRegistry registry;
    private NotificationService notificationService;

    private ListenerRegistration<PacketHandler> packetHandlerListenerRegistration;
    private ListenerRegistration<TopologyListener> topologyListenerListenerRegistration;
    private ListenerRegistration<BaseflowListener> baseflowListenerListenerRegistration;

//    public ODLMapleProvider(DataBroker dataBroker,
//                            RpcProviderRegistry registry,
//                            NotificationService notificationService,
//                            NotificationPublishService publishService) {
//        this.dataBroker = dataBroker;
//        this.registry = registry;
//        this.notificationService = notificationService;
//        this.publishService = publishService;
//    }

    public void setPublishService(NotificationPublishService publishService) {
        this.publishService = publishService;
    }

    public void setDataBroker(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    public void setRegistry(RpcProviderRegistry registry) {
        this.registry = registry;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
        SalFlowService salFlowService = registry.getRpcService(SalFlowService.class);

        PacketProcessingService packetProcessingService = registry.getRpcService(PacketProcessingService.class);

        LldpSpeakerService lldpSpeakerService = registry.getRpcService(LldpSpeakerService.class);
        SetLldpFloodIntervalInput input = new SetLldpFloodIntervalInputBuilder().setInterval(1L).build();

        while(true) {
            try {
                Thread.sleep(1000);
                lldpSpeakerService.setLldpFloodInterval(input).get();
                break;
            } catch (InterruptedException e) {
                LOG.warn("set lldp interval failed");
            } catch (ExecutionException e) {
                LOG.warn("set lldp interval failed");
            }
        }

        ODLMapleAdaptor odlMapleAdaptor = new ODLMapleAdaptor(this.dataBroker, salFlowService, packetProcessingService);

        MapleSystem mapleSystem = new MapleSystem(odlMapleAdaptor);

        IMapleHandler mapleHandler = mapleSystem.getHandler();

        PacketHandler packetHandler = new PacketHandler(mapleHandler);

        packetHandlerListenerRegistration = notificationService.registerNotificationListener(packetHandler);

        InstanceIdentifier<NetworkTopology> networkTopologyIId = InstanceIdentifier.builder(NetworkTopology.class).build();
        //dataBroker.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL,iid,new TopologyListener(), AsyncDataBroker.DataChangeScope.BASE);
        DataTreeIdentifier<NetworkTopology> networkTopologyDTId = new DataTreeIdentifier<>(LogicalDatastoreType.OPERATIONAL, networkTopologyIId);
        topologyListenerListenerRegistration = dataBroker.registerDataTreeChangeListener(networkTopologyDTId, new TopologyListener(mapleHandler));

        InstanceIdentifier<Baseflow> baseflowIId = InstanceIdentifier.builder(Baseflow.class).build();
        DataTreeIdentifier<Baseflow> baseflowDTId = new DataTreeIdentifier<>(LogicalDatastoreType.CONFIGURATION, baseflowIId);
        baseflowListenerListenerRegistration = dataBroker.registerDataTreeChangeListener(baseflowDTId, new BaseflowListener(mapleHandler));


        //notificationService.registerNotificationListener(new PortEventListener());
        InstanceIdentifier<Nodes> iid1 = InstanceIdentifier.builder(Nodes.class).build();
        InstanceIdentifier<FlowCapableNode> iid2 = InstanceIdentifier.builder(Nodes.class).child(Node.class)
                .augmentation(FlowCapableNode.class).build();
        InstanceIdentifier<State> stateiid = iid1.child(Node.class).child(NodeConnector.class).augmentation(FlowCapableNodeConnector.class).child(State.class);

        DataTreeIdentifier<Nodes> ntti1 = new DataTreeIdentifier<>(LogicalDatastoreType.OPERATIONAL, iid1);

        DataTreeIdentifier<FlowCapableNode> ntti2 = new DataTreeIdentifier<>(LogicalDatastoreType.OPERATIONAL, iid2);

        DataTreeIdentifier<State> dti = new DataTreeIdentifier<>(LogicalDatastoreType.OPERATIONAL, stateiid);

        dataBroker.registerDataTreeChangeListener(dti,new NodesListener());

        LOG.info("ODLMapleProvider Session Initiated");
        System.out.println("ODLMapleProvider Session Initiated");
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        packetHandlerListenerRegistration.close();
        topologyListenerListenerRegistration.close();
        baseflowListenerListenerRegistration.close();
        LOG.info("ODLMapleProvider Closed");
    }
}