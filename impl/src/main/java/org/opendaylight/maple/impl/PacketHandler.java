/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.MapleHandler;

public class PacketHandler implements PacketProcessingListener {

    private final static Logger LOG = LoggerFactory.getLogger(PacketHandler.class);

    private final MapleHandler mapleHandler;

    public PacketHandler(MapleHandler mapleHandler) {
        this.mapleHandler = mapleHandler;
    }

    @Override
    public void onPacketReceived(PacketReceived packetReceived) {
        Class<? extends PacketInReason> packetInReason = packetReceived.getPacketInReason();
        if(packetInReason.isInstance(InvalidTtl.class)){

        } else if (packetInReason.isInstance(SendToController.class)){

        } else if(packetInReason.isInstance(NoMatch.class)){

        } else {
            LOG.error("unknown packetinreason");
        }
    }
}
