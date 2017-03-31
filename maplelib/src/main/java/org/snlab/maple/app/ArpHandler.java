/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.api.MapleEnv;
import org.snlab.maple.api.MaplePacket;
import org.snlab.maple.packet.MaplePacketImpl;

public class ArpHandler extends MapleAppBase {
    @Override
    public boolean onPacket(MaplePacket pkt,MapleEnv env) {

        return false;

    }
}
