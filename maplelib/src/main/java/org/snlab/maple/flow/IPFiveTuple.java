/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.flow;

import javax.annotation.concurrent.Immutable;

@Immutable
public class IPFiveTuple {
    private final byte ipproto;
    private final int ipsrc;
    private final int ipdst;
    private final short sport;
    private final short dport;

    public IPFiveTuple(byte ipproto, int ipsrc, int ipdst, short sport, short dport) {
        this.ipproto = ipproto;
        this.ipsrc = ipsrc;
        this.ipdst = ipdst;
        this.sport = sport;
        this.dport = dport;
    }

    public byte getIpproto() {
        return ipproto;
    }

    public int getIpsrc() {
        return ipsrc;
    }

    public int getIpdst() {
        return ipdst;
    }

    public short getSport() {
        return sport;
    }

    public short getDport() {
        return dport;
    }


    public IPFiveTuple reverse(){
        return new IPFiveTuple(ipproto,ipdst,ipsrc,dport,sport);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IPFiveTuple that = (IPFiveTuple) o;

        if (ipproto != that.ipproto) return false;
        if (ipsrc != that.ipsrc) return false;
        if (ipdst != that.ipdst) return false;
        if (sport != that.sport) return false;
        return dport == that.dport;
    }

    @Override
    public int hashCode() {
        int result = (int) ipproto;
        result = 31 * result + ipsrc;
        result = 31 * result + ipdst;
        result = 31 * result + (int) sport;
        result = 31 * result + (int) dport;
        return result;
    }
}
