/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.field;

public enum MapleMatchField {

    INPORT(0, false),  //this is special

    ETH_SRC(48, true),
    ETH_DST(48, true),
    ETH_TYPE(16, true),
    IPv4_SRC(32, true),
    IPv4_DST(32, true),
    IPv6_SRC(128, true),
    IPv6_DST(128, true),
    IPv6_LABEL(20,true),
    IP_PROTO(8, false),
    NW_TTL(8,false),
    IP_FRAG(2,true),
    NW_TOS(8,false),
    IP_DSCP(6,false),
    IP_ECN(2,false),
    ARP_OP(16,false),
    ARP_SPA(32,true),
    ARP_TPA(32,true),
    ARP_SHA(48,true),
    ARP_THA(48,true),
    TCP_SPORT(16,true),
    TCP_DPORT(16,true),
    TCP_FLAGS(12,true),
    UDP_SPORT(16,true),
    UDP_DPORT(16,true),
    SCTP_SPORT(16,true),
    SCTP_DPORT(16,true),
    ICMP_TYPE(8,false),
    ICMP_CODE(8,false),
    ICMPv6_TYPE(8,false),
    ICMPv6_CODE(8,false),
    ND_TARGET(128,true),
    ND_SLL(48,true),
    ND_TLL(48,true);

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
