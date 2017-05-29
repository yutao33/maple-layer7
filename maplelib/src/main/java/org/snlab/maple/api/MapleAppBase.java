/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api;


public abstract class MapleAppBase {

    protected static final byte[] ARP=new byte[]{8,6};
    protected static final byte[] IPv4=new byte[]{8,0};
    protected static final byte[] TCP=new byte[]{6};
    protected static final byte[] UDP=new byte[]{17};

    public boolean init(IMapleDataBroker env) {
        return true;
    }

    public Object onCommand(Object parm, IMapleDataBroker env) {
        return null;
    }

    public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {
        return false;
    }


}
