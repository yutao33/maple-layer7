/*
 * Copyright © 2017 SNLab and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maple.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.controller.md.sal.binding.api.NotificationService;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.MapleSystem;

public class ODLMapleProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ODLMapleProvider.class);

    private final DataBroker dataBroker;
    private final RpcProviderRegistry registry;
    private final NotificationService notificationService;
    private final NotificationPublishService publishService;

    ListenerRegistration<PacketHandler> packetHandlerListenerRegistration;

    public ODLMapleProvider(final DataBroker dataBroker,
                            RpcProviderRegistry registry,
                            NotificationService notificationService,
                            NotificationPublishService publishService) {
        this.dataBroker = dataBroker;
        this.registry = registry;
        this.notificationService = notificationService;
        this.publishService = publishService;
    }

    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
        ODLMapleSystemAdaptor odlMapleSystemAdaptor = new ODLMapleSystemAdaptor(this.dataBroker);

        MapleSystem mapleSystem = new MapleSystem(odlMapleSystemAdaptor);

        PacketHandler packetHandler = new PacketHandler(mapleSystem.getHandler());

        packetHandlerListenerRegistration = notificationService.registerNotificationListener(packetHandler);

        LOG.info("ODLMapleProvider Session Initiated");
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        packetHandlerListenerRegistration.close();
        LOG.info("ODLMapleProvider Closed");
    }
}