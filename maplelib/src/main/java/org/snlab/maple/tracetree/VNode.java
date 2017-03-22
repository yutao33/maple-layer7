/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.maple.tracetree;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VNode extends Node {

    public Match.Field field;

    public String value;

    public Map<String, Node> subtree = new ConcurrentHashMap<String, Node>();

    public Node getChild(String value) {
        return subtree.get(value);
    }

    @Override
    public void delete(Node node) {
        for (String name : subtree.keySet()) {
            if (subtree.get(name) == node) subtree.remove(name);
        }
        node.fatherNodesinOrderGraph.remove(this);
    }

    @Override
    public void augment(Node node, String pktHash, Trace trace, TraceTree tt) {
        // TODO Auto-generated method stub
        if (node instanceof VNode) {
            this.count++;
            //merge
            /*Node father = node.pkt2fatherinTrace.get(pkt);
			father.pkt2nextNodeinTrace.put(pkt, this);
			this.pkt2nextNodeinTrace.put(pkt, node.pkt2nextNodeinTrace.get(pkt));*/
            Node father = node.pkt2fatherinTrace.get(pktHash);
            if (father == null) {
                //first node
                trace.firstNode = this;
                //does not handle last node
            } else {
                father.pkt2nextNodeinTrace.put(pktHash, this);
            }
            this.pkt2nextNodeinTrace.put(pktHash, node.pkt2nextNodeinTrace.get(pktHash));
            // handle father
            node.pkt2nextNodeinTrace.get(pktHash).pkt2fatherinTrace.put(pktHash, this);
			
			/*//handle map
			for(Node childNode: tt.fatherNode2ChildNodesInOrderGraph.get(node)){
				childNode.fatherNodesinOrderGraph.remove(node);
				childNode.fatherNodesinOrderGraph.add(this);
			}
			List<Node> tempChilds = tt.fatherNode2ChildNodesInOrderGraph.get(node);
			tt.fatherNode2ChildNodesInOrderGraph.remove(node);
			tt.fatherNode2ChildNodesInOrderGraph.put(this, tempChilds);
			
			//handle edgeWeight
			List<Node> tempChildsForEdge = tt.edgeWithOneWeightInOrderGraph.get(node);
			tt.edgeWithOneWeightInOrderGraph.remove(node);
			tt.edgeWithOneWeightInOrderGraph.put(this, tempChildsForEdge);
			for(Map.Entry<Node, List<Node>> entry: tt.edgeWithOneWeightInOrderGraph.entrySet()){
				Node keyNode = entry.getKey();
				List<Node> valueNodes = entry.getValue();
				if(valueNodes.contains(node)){
					valueNodes.remove(node);
					valueNodes.add(this);
				}
			}*/

            //handle map
            if (tt.fatherNode2ChildNodesInOrderGraph.containsKey(node)) {
                for (Node childNode : tt.fatherNode2ChildNodesInOrderGraph.get(node)) {//TODO: null
                    childNode.fatherNodesinOrderGraph.remove(node);
                    childNode.fatherNodesinOrderGraph.add(this);
                }
                List<Node> tempChilds = tt.fatherNode2ChildNodesInOrderGraph.get(node);
                tt.fatherNode2ChildNodesInOrderGraph.remove(node);
                tt.fatherNode2ChildNodesInOrderGraph.put(this, tempChilds);
            }
            for (Map.Entry<Node, List<Node>> entry : tt.fatherNode2ChildNodesInOrderGraph.entrySet()) {
                Node keyNode = entry.getKey();
                List<Node> valueNodes = entry.getValue();
                if (valueNodes.contains(node)) {
                    valueNodes.remove(node);
                    valueNodes.add(this);
                }
            }


            //handle edgeWeight
            if (tt.edgeWithOneWeightInOrderGraph.containsKey(node)) {
                List<Node> tempChildsForEdge = tt.edgeWithOneWeightInOrderGraph.get(node);
                tt.edgeWithOneWeightInOrderGraph.remove(node);
                tt.edgeWithOneWeightInOrderGraph.put(this, tempChildsForEdge);
            }
            for (Map.Entry<Node, List<Node>> entry : tt.edgeWithOneWeightInOrderGraph.entrySet()) {
                Node keyNode = entry.getKey();
                List<Node> valueNodes = entry.getValue();
                if (valueNodes.contains(node)) {
                    valueNodes.remove(node);
                    valueNodes.add(this);
                }
            }

            VNode vNode = (VNode) node;
            String key = null;
            Node valueNode = null;
            for (Map.Entry<String, Node> entry : vNode.subtree.entrySet()) {
                //should only one entry
                key = entry.getKey();
                valueNode = entry.getValue();
            }
            if (this.subtree.containsKey(key)) {
                Node containtedNode = this.subtree.get(key);
                containtedNode.augment(valueNode, pktHash, trace, tt);
            } else {
                this.subtree.put(key, valueNode);
                //finish?
            }
            //handle order graph
            this.fatherNodesinOrderGraph.addAll(node.fatherNodesinOrderGraph);
        } else {
            //wrong
        }
    }
}
