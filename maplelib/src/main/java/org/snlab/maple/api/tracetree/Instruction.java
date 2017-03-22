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

public class Instruction {

	private Map<Match.Field, SetPacketHeaderAction> SPHActions;
	
	private RouteAction routeAction;
	
	private int vlanId;
	
	private boolean isVlanIdSet = false;
	
	private boolean needToPopVlan = false;

	public boolean isVlanIdSet() {
		return isVlanIdSet;
	}



	public void setVlanIdSet(boolean isVlanIdSet) {
		this.isVlanIdSet = isVlanIdSet;
	}



	public Instruction() {
		this.SPHActions = new HashMap<Match.Field, SetPacketHeaderAction>();
	}
	
	

	public int getVlanId() {
		return vlanId;
	}
	
	public void popVlanId() {
		this.needToPopVlan = true;
	}
	
	public boolean needToPopVlanId() {
		return this.needToPopVlan;
	}



	public void setVlanId(int vlanId) {
		this.vlanId = vlanId;
	}

	public Map<Match.Field, SetPacketHeaderAction> getSPHActions() {
		return SPHActions;
	}

	public void setSPHActions(Map<Match.Field, SetPacketHeaderAction> sPHActions) {
		SPHActions = sPHActions;
	}

	public RouteAction getRouteAction() {
		return routeAction;
	}

	public void setRouteAction(RouteAction routeAction) {
		this.routeAction = routeAction;
	}
	
	public void addSPHAction(SetPacketHeaderAction SPHAction) {
		Match.Field field = SPHAction.field;
		this.SPHActions.put(field, SPHAction);
	}
	
	public boolean isFieldModified(Match.Field field) {
		if (this.SPHActions.containsKey(field)) {
			return true;
		} else {
			return false;
		}
	}
	
	public SetPacketHeaderAction getSPHAction(Match.Field field) {
		if (this.SPHActions.containsKey(field)) {
			return this.SPHActions.get(field);
		} else {
			return null;
		}
	}
	
	public static Map<Match.Field, SetPacketHeaderAction> convertString2SPHActions(String sphString) {
		Map<Match.Field, SetPacketHeaderAction> returnMap = 
				new HashMap<Match.Field, SetPacketHeaderAction>();
		if (sphString.equals("null")) {
			return returnMap;
		}
		String[] modifiedFields = sphString.split(";");
		for (String modifiedField: modifiedFields) {
			String fieldValue = modifiedField.substring(1, modifiedField.length() - 1);
			String[] fieldValuePair = fieldValue.split(",");
			String fieldString = fieldValuePair[0];
			String value = fieldValuePair[1];
			Match.Field field = Match.toMatchField(fieldString);
			SetPacketHeaderAction SPHAction = new SetPacketHeaderAction(field, value);
			returnMap.put(field, SPHAction);
		}
		return returnMap;
	}
	
	// action := route|modifiedFields
	// modifiedFields := modifiedFields;<field,value> 
	// modifiedFields can be null
	public TraceItem toItem() {
		String routeString = this.routeAction.toString();
		String modifiedFields = "";
		String vlanIdString = "";
		for (Map.Entry<Match.Field, SetPacketHeaderAction> entry: this.SPHActions.entrySet()) {
			SetPacketHeaderAction SPHAction = entry.getValue();
			String field = Match.toString(SPHAction.field);
			String value = SPHAction.value;
			String fieldValue = "<" + field + "," + value + ">";
			modifiedFields += ";" + fieldValue;
		}
		if (this.SPHActions.isEmpty()) {
			modifiedFields = "null";
		} else {
			modifiedFields = modifiedFields.substring(1);
		}
		
		if (this.isVlanIdSet) {
			vlanIdString = String.valueOf(vlanId);
		} else {
			vlanIdString = "null";
		}
		String returedAction = routeString + "|" + modifiedFields + "|" + vlanIdString + "|" + (this.needToPopVlan?"1":"0");
		
		TraceItem traceItem = new TraceItem();
		traceItem.type = "L";
		traceItem.action = returedAction;
		return traceItem;
	}
}
