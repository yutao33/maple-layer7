/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnectorKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.*;
import org.opendaylight.yangtools.yang.binding.KeyedInstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.IMapleHandler;
import org.snlab.maple.rule.MaplePacketInReason;

public class PacketHandler implements PacketProcessingListener {

    private final static Logger LOG = LoggerFactory.getLogger(PacketHandler.class);

    private final IMapleHandler mapleHandler;

    public PacketHandler(IMapleHandler mapleHandler) {
        this.mapleHandler = mapleHandler;
    }

    @Override
    public void onPacketReceived(PacketReceived packetReceived) {
        Class<? extends PacketInReason> packetInReason = packetReceived.getPacketInReason();
        if(packetInReason.equals(InvalidTtl.class)){

        } else if (packetInReason.equals(SendToController.class)){

        } else if(packetInReason.equals(NoMatch.class)){

        } else {
            LOG.error("unknown packetinreason");
        }

        NodeConnectorRef ingress = packetReceived.getIngress();

        KeyedInstanceIdentifier ingressiid = (KeyedInstanceIdentifier) ingress.getValue();
        NodeConnectorKey ingresskey = (NodeConnectorKey) ingressiid.getKey();
        String ingresskeyid = ingresskey.getId().getValue();


        System.out.println("ingress="+ingresskeyid);
        mapleHandler.onPacket(ingresskeyid,packetReceived.getPayload(), MaplePacketInReason.NOMATCH);

    }
}
