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
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.controller.md.sal.binding.api.NotificationService;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.IMapleHandler;
import org.snlab.maple.MapleSystem;

public class ODLMapleProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ODLMapleProvider.class);

    private NotificationPublishService publishService;
    private DataBroker dataBroker;
    private RpcProviderRegistry registry;
    private NotificationService notificationService;

    private ListenerRegistration<PacketHandler> packetHandlerListenerRegistration;
    private ListenerRegistration<TopologyListener> topologyListenerListenerRegistration;

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

        ODLMapleAdaptor odlMapleAdaptor = new ODLMapleAdaptor(this.dataBroker, salFlowService);

        MapleSystem mapleSystem = new MapleSystem(odlMapleAdaptor);

        IMapleHandler mapleHandler = mapleSystem.getHandler();

        PacketHandler packetHandler = new PacketHandler(mapleHandler);

        packetHandlerListenerRegistration = notificationService.registerNotificationListener(packetHandler);

        InstanceIdentifier<NetworkTopology> iid = InstanceIdentifier.builder(NetworkTopology.class).build();
        //dataBroker.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL,iid,new TopologyListener(), AsyncDataBroker.DataChangeScope.BASE);

        DataTreeIdentifier<NetworkTopology> ntti = new DataTreeIdentifier<>(LogicalDatastoreType.OPERATIONAL, iid);

        topologyListenerListenerRegistration = dataBroker.registerDataTreeChangeListener(ntti, new TopologyListener(mapleHandler));

        LOG.info("ODLMapleProvider Session Initiated");
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        packetHandlerListenerRegistration.close();
        topologyListenerListenerRegistration.close();
        LOG.info("ODLMapleProvider Closed");
    }
}