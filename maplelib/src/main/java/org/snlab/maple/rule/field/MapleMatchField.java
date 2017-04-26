/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.field;

public enum MapleMatchField {

    INGRESS(0,false),  //this is special

    ETH_SRC(48, true),
    ETH_DST(48, true),
    ETH_TYPE(16, true),
    IPv4_SRC(32, true),
    IPv4_DST(32, true),
    IP_PROTO(8, false),
    IPv6_SRC(128, true),
    IPv6_DST(128, true);

    private int bitlength;
    private int bytelength;
    private boolean canMask;

    MapleMatchField(int len, boolean canMask) {
        this.bitlength = len;
        this.bytelength = (len + 7) / 8;
        this.canMask = canMask;
    }

    public int getBitLength() {
        return bitlength;
    }

    public int getByteLength() {
        return bytelength;
    }

    public boolean canMask() {
        return canMask;
    }
}
