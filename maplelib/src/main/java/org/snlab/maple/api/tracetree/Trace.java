/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.maple.api.tracetree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Trace {

	public Node firstNode;
	
	public Node lastNode;
	
	String nextPositionString;
	
	int nextPositionInt;
	
	public void addNode(Node node, String pktHash, String value, boolean isTrue){
		if(firstNode == null){
			firstNode = node;
			lastNode = node;
			nextPositionString = value;
			nextPositionInt = isTrue?1:0;//true is 1; false is 0
		}else{
			lastNode.pkt2nextNodeinTrace.put(pktHash, node);
			node.pkt2fatherinTrace.put(pktHash, lastNode);
			//connect to TT
			if(lastNode instanceof VNode){
				System.out.println("set subtree of vnode" + nextPositionString);
				VNode vNode = (VNode)lastNode;
				vNode.subtree.put(nextPositionString, node);
			}else if(lastNode instanceof TNode){
				TNode tNode = (TNode)lastNode;
				tNode.subtree[nextPositionInt] = node;
			}else{
				//wrong
			}
			lastNode = node;
			nextPositionString = value;
			nextPositionInt = isTrue?1:0;
		}
	}
	
	public void addTraceItem(TraceItem traceItem, String pktHash){
		if (traceItem.type.equals("T")) {
			TNode tNode = new TNode();
			tNode.field = Match.toMatchField(traceItem.field);
			tNode.value = traceItem.value;
			this.addNode(tNode, pktHash, traceItem.value, traceItem.branch == "1"?true:false);
		}else if (traceItem.type.equals("V")) {
			VNode vNode = new VNode();
			vNode.field = Match.toMatchField(traceItem.field);
			vNode.value = traceItem.value;
			this.addNode(vNode, pktHash, traceItem.value, false);
		}else {
			System.out.println("LNode: " + traceItem.action);
			LNode lNode = new LNode();
			lNode.action = traceItem.action;
			this.addNode(lNode, pktHash, null, false);
		}
	}
	
	/*
	 * trace -> rules and store them at nodes (TNode, LNode)
	 * rule = match + action
	 * */
	public void generateAndStoreRules(String pktHash){
		//System.out.println("calls generateAndStoreRules");
		Node node = this.firstNode;
		Match tempMatch = new Match();
		List<TNode> tNodesWithRule = new LinkedList<TNode>();
		while(node.pkt2nextNodeinTrace.get(pktHash) != null){
			if(node instanceof VNode){
				VNode vNode = (VNode)node;
				Match.Field fieldNum = vNode.field;
				String value = vNode.value;
				tempMatch.fields.put(fieldNum, value);
			}else if(node instanceof TNode){
				TNode tNode = (TNode)node;
				Match.Field fieldNum = tNode.field;
				String value = tNode.value;
				
				
				//check to generate a barrier rule for false branch
				if(tNode.getChild(false) != null){
					tNodesWithRule.add(tNode);
					
					
					//has false branch
					tNode.ruleChanged = true;
					tNode.firstAdd = true;
					Match match = new Match();
					match.fields.putAll(tempMatch.fields);
					match.fields.put(fieldNum, value);
					Instruction inst = new Instruction();
					inst.setRouteAction(RouteAction.Punt());
					Rule rule = new Rule(match, inst, pktHash);
					tNode.rule = rule;
					
					// handle pktHash
					tNode.pktHash = pktHash;
				} else {
					// has true branch
					tempMatch.fields.put(fieldNum, value);
				}
			}
			node = node.pkt2nextNodeinTrace.get(pktHash);
		}
		//handle LNode
		if (node instanceof LNode) {
			node.ruleChanged = true;
			node.firstAdd = true;
			LNode lNode = (LNode)node;
			Rule rule;
			String action = lNode.action;
			String[] pathModifiedField = action.split("\\|");
			
			/*0: routeAction;
			1: modifiedField;
			2: vlanId;
			3: needToPopVlanId*/
			
			if (pathModifiedField[0].equals("drop")) {
				Instruction inst = new Instruction();
				inst.setRouteAction(RouteAction.Drop());
				rule = new Rule(tempMatch, inst, pktHash);
			} else if (pathModifiedField[0].equals("punt")) {
				Instruction inst = new Instruction();
				inst.setRouteAction(RouteAction.Punt());
				rule = new Rule(tempMatch, inst, pktHash);
			} else if (pathModifiedField[0].equals("flood")) {
				Instruction inst = new Instruction();
				inst.setRouteAction(RouteAction.Flood());
				rule = new Rule(tempMatch, inst, pktHash);
				for (TNode tNode: tNodesWithRule) {
					tNode.ruleChanged = false;
					tNode.rule = null;
				}
			} else {
				//Route
				Instruction inst = new Instruction();
				if (pathModifiedField[0].contains("-")) {
					// multipath
					String[] pathStrings = pathModifiedField[0].split("-");
					MultiPath mp = new MultiPath();
					for (String pathString: pathStrings) {
						Path path = new Path(pathString);
						mp.addPath(path);
					}
					inst.setRouteAction(mp);
					inst.setSPHActions(Instruction.convertString2SPHActions(pathModifiedField[1]));
					if (pathModifiedField[2].equals("null")) {
						// do not have vlanId
					} else {
						// have vlanId
						inst.setVlanIdSet(true);
						inst.setVlanId(Integer.parseInt(pathModifiedField[2]));
					}
					rule = new Rule(tempMatch, inst, pktHash);
					System.out.println("setup rule for multi path");
				} else {
					// single path
					inst.setRouteAction(new Path(pathModifiedField[0]));
					inst.setSPHActions(Instruction.convertString2SPHActions(pathModifiedField[1]));
					if (pathModifiedField[2].equals("null")) {
						// do not have vlanId
					} else {
						// have vlanId
						inst.setVlanIdSet(true);
						inst.setVlanId(Integer.parseInt(pathModifiedField[2]));
					}
					
					if (pathModifiedField[3].equals("1")) {
						// need to pop vlan id
						inst.popVlanId();
					} else {
						
					}
					
					rule = new Rule(tempMatch, inst, pktHash);
					System.out.println("setup rule for single path");
				}
				
			}
			lNode.rule = rule;
			
			lNode.pktHash = pktHash;
		} else {
			//wrong
		}
	}
	
	public void show(String pktHash) {
		System.out.println("show trace:");
		Node node = firstNode;
		while (node !=  null) {
			System.out.println(node.toString());
			node = node.pkt2nextNodeinTrace.get(pktHash);
		}
	}
	
	public static List<TraceItem> compress(List<TraceItem> originalTrace) {
		List<TraceItem> compressedItemList = new LinkedList<TraceItem>();
		Map<Match.Field, List<CompressionItem>> field2ItemList = 
				new HashMap<Match.Field, List<CompressionItem>>();
		for(TraceItem traceItem: originalTrace) {
			if (traceItem.type.equals("T")) {
				Match.Field field = Match.toMatchField(traceItem.field);
				CompressionItem ci = new CompressionItem();
				ci.is = traceItem.branch == "1"?true:false;
				ci.value = traceItem.value;
				if (field2ItemList.containsKey(field)) {
					if (CompressionItem.canRemove(field2ItemList.get(field), ci)) continue;
					else {
						compressedItemList.add(traceItem);
					}
				} else {
					List<CompressionItem> tempList = new LinkedList<CompressionItem>();
					tempList.add(ci);
					field2ItemList.put(field, tempList);
					compressedItemList.add(traceItem);
				}
			}else if (traceItem.type.equals("V")) {
				Match.Field field = Match.toMatchField(traceItem.field);
				CompressionItem ci = new CompressionItem();
				ci.is = true;
				ci.value = traceItem.value;
				if (field2ItemList.containsKey(field)) {
					if (CompressionItem.canRemove(field2ItemList.get(field), ci)) continue;
					else {
						compressedItemList.add(traceItem);
					}
				} else {
					List<CompressionItem> tempList = new LinkedList<CompressionItem>();
					tempList.add(ci);
					field2ItemList.put(field, tempList);
					compressedItemList.add(traceItem);
				}
			}else {
				compressedItemList.add(traceItem);
			}
		}
		return compressedItemList;
	}
}
