/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.maple.api.tracetree;

import java.util.HashMap;
import java.util.Map;

public class Match {
    public static enum Field {
	    IN_PORT,
	    ETH_SRC,
	    ETH_DST,
	    TOPOLOGY,
	    ETH_TYPE,
	    IPv4_SRC,
	    IPv4_DST,
	    TCP_SRC_PORT,
	    TCP_DST_PORT,
	    VLAN_ID
    }
    
    public static String toString(Field field){
    	if (field.equals(Field.IN_PORT)) {
    		return "IN_PORT";
    	} else if (field.equals(Field.ETH_SRC)) {
    		return "ETH_SRC";
    	} else if (field.equals(Field.ETH_DST)) {
    		return "ETH_DST";
    	} else if (field.equals(Field.ETH_TYPE)) {
    		return "ETH_TYPE";
    	} else if (field.equals(Field.IPv4_SRC)) {
    		return "IPv4_SRC";
    	} else if (field.equals(Field.IPv4_DST)) {
    		return "IPv4_DST";
    	} else if (field.equals(Field.TCP_SRC_PORT)) {
    		return "TCP_SRC_PORT";
    	} else if (field.equals(Field.TCP_DST_PORT)) {
    		return "TCP_DST_PORT";
    	} else if (field.equals(Field.VLAN_ID)) {
    		return "VLAN_ID";
    	} else {
    		return null;
    	}
    }
    
    public static Field toMatchField(String field){
    	if (field.equals("IN_PORT")) {
    		return Field.IN_PORT;
    	} else if (field.equals("ETH_SRC")) {
    		return Field.ETH_SRC;
    	} else if (field.equals("ETH_DST")) {
    		return Field.ETH_DST;
    	} else if (field.equals("ETH_TYPE")) {
    		return Field.ETH_TYPE;
    	} else if (field.equals("IPv4_SRC")) {
    		return Field.IPv4_SRC;
    	} else if (field.equals("IPv4_DST")) {
    		return Field.IPv4_DST;
    	} else if (field.equals("TCP_SRC_PORT")) {
    		return Field.TCP_SRC_PORT;
    	} else if (field.equals("TCP_DST_PORT")) {
    		return Field.TCP_DST_PORT;
    	} else if (field.equals("VLAN_ID")) {
    		return Field.VLAN_ID;
    	} else {
    		return null;
    	}
    }
    
    public Map<Field, String> fields = new HashMap<Field, String>();

    //public Field field;
    //public String value;
    //public boolean Tvalue;
    
    @Override
    public String toString() {
    	String tempString = "";
    	for (Map.Entry<Field, String> entry: this.fields.entrySet()) {
    		String field = this.toString(entry.getKey());
    		String value = entry.getValue();
    		String fieldValue = "<" + field + "," + value + ">";
    		tempString += fieldValue;
    	}
    	return tempString;
    }
}
