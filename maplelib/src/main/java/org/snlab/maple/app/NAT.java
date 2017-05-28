/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.api.IMapleDataBroker;
import org.snlab.maple.api.IMaplePacket;
import org.snlab.maple.api.MapleAppBase;

public class NAT extends MapleAppBase{



    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {
        return false;
    }
}
