/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.flowrule;

public enum MapleMatchField{

    INGRESS("ingress",0),  //this is special

    ETH_SRC("eth_src",48),
    ETH_DST("eth_dst",48),
    ETH_TYPE("eth_type",16),
    IP_SRC("ip_src",32),
    IP_DST("ip_dst",32),
    IP_PROTO("ip_proto",8);

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
