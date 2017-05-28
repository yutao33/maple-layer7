/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRemoved;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorUpdated;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRemoved;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeUpdated;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.OpendaylightInventoryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortEventListener implements OpendaylightInventoryListener {

    private final static Logger LOG = LoggerFactory.getLogger(PortEventListener.class);

    @Override
    public void onNodeConnectorRemoved(NodeConnectorRemoved notification) {
        LOG.info(notification.toString());
    }

    @Override
    public void onNodeConnectorUpdated(NodeConnectorUpdated notification) {
        LOG.info(notification.toString());
    }

    @Override
    public void onNodeRemoved(NodeRemoved notification) {
        LOG.info(notification.toString());
    }

    @Override
    public void onNodeUpdated(NodeUpdated notification) {
        LOG.info(notification.toString());
    }
}
