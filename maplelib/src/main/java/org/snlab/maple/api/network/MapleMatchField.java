/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api.network;

public enum MapleMatchField{
    ETH_SRC("eth_src"),
    ETH_DST("eth_dst"),
    IP_SRC("ip_src");

    private String field;
    private MapleMatchField(String str){
        this.field =str;
    }

    @Override
    public String toString() {
        return "MapleMatchField{" +
                "field='" + field + '\'' +
                '}';
    }
}
