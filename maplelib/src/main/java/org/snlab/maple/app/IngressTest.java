/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.api.MaplePacket;

public class IngressTest extends MapleAppBase {
    private static final String[] path1={""};
    private static final String[] path2={""};

    @Override
    public boolean onPacket(MaplePacket pkt) {
        if(pkt.ingress().is("openflow:1:1")){
            pkt.setRoute(null,path1);
            return true;
        } else if(pkt.ingress().is("openflow:3:1")){
            pkt.setRoute(null,path2);
            return true;
        }
        return false;
    }
}
