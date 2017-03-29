/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;

public enum MapleMatchField{

    INGRESS("ingress",0),  //special field
    ETH_SRC("eth_src",48),
    ETH_DST("eth_dst",48),
    IP_SRC("ip_src",48);

    private String field;
    private int bitlength;

    MapleMatchField(String str,int len){
        this.field =str;
        this.bitlength=len;
    }

    public int getBitLength(){
        return bitlength;
    }

    @Override
    public String toString() {
        return "MapleMatchField{" +
                "field='" + field + '\'' +
                '}';
    }
}
