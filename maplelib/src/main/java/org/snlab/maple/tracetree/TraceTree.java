/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.maple.tracetree;

import java.util.*;

public class TraceTree {

    public Node root;
    //srcNode, dstNode, indicate weight = 1
    public Map<Node, List<Node>> edgeWithOneWeightInOrderGraph = new HashMap<Node, List<Node>>();
    public Map<Node, List<Node>> fatherNode2ChildNodesInOrderGraph = new HashMap<Node, List<Node>>();
    Set<RulePri> deleteRules = Collections.synchronizedSet(new HashSet<RulePri>());
    Set<RulePri> installRules = Collections.synchronizedSet(new HashSet<RulePri>());
    private HashMap<String, Trace> pkt2trace = new HashMap<String, Trace>();
    private int globalPriority = 0;

    public void updateTT(String pktHash, Trace tNew) {
        if (pkt2trace.containsKey(pktHash)) {
            //update trace
            Trace tOld = pkt2trace.get(pktHash);
            deleteTrace(pktHash, tOld);
        }
        tNew.generateAndStoreRules(pktHash);
        initializeOrderGraphOfTrace(tNew, pktHash);
        addTrace(pktHash, tNew);//this step modifies the trace's first node by merging
        tNew.show(pktHash);
        //traverseTTtoUpdatePriority2(tNew.firstNode, pktHash, root);
        traverseTTtoUpdatePriority3(root);
        this.globalPriority = 0;
        //traverseTTtoInstallRule(root);
    }

    public void updateTTMultiple(Map<String, Trace> pkt2TraceInput) {
        for (Map.Entry<String, Trace> entry : pkt2TraceInput.entrySet()) {
            String pktHash = entry.getKey();
            Trace trace = entry.getValue();
            if (pkt2trace.containsKey(pktHash)) {
                Trace tOld = pkt2trace.get(pktHash);
                deleteTrace(pktHash, tOld);
            }
            initializeOrderGraphOfTrace(trace, pktHash);
            addTrace(pktHash, trace);//this step modifies the trace's first node by merging
        }
        traverseTTtoUpdatePriority3(root);
        traverseTTtoInstallRule(root);
    }

    private void deleteTrace(String pktHash, Trace trace) {
        Node node = trace.firstNode;
        List<Node> allTNodesWithFalseBranch = new LinkedList<Node>();
        while (node != null) {
            if (node instanceof TNode) {
                TNode tNode = (TNode) node;
                if (tNode.getChild(false) != null) {
                    allTNodesWithFalseBranch.add(node);
                }
            }
            node.count--;
            if (node.count == 0) {
                //should remove this node
                if (node.pkt2fatherinTrace.get(pktHash) != null) {
                    node.pkt2fatherinTrace.get(pktHash).pkt2nextNodeinTrace.remove(pktHash);
                    node.pkt2fatherinTrace.get(pktHash).delete(node);
                } else {
                    //root
                    this.root = null;
                }
                //handle map
                this.removeNodeFromFatherNode2ChildNodesInOrderGraph(node);


                //handle edge
                this.removeNodeFromEdgeWithOneWeightInOrderGraph(node);

                if (node instanceof TNode) {
                    TNode tNode = (TNode) node;
                    if (tNode.rule != null) {
                        deleteRule(tNode.priority, tNode.rule);
                    }
                }
            }
            if (node.pkt2nextNodeinTrace.get(pktHash) == null) {
                //this should be leaf node
                deleteRule(((LNode) node).priority, ((LNode) node).rule);
                for (Node tNode : allTNodesWithFalseBranch) {
                    tNode.fatherNodesinOrderGraph.remove(node);
                    //handle map
                    this.removeNodeFromFatherNode2ChildNodesInOrderGraph(node);

                    //handle edge, only up link with weight = 1
                    this.removeNodeFromEdgeWithOneWeightInOrderGraph(node);
                }
            }
            node = node.pkt2nextNodeinTrace.get(pktHash);

        }
        pkt2trace.remove(pktHash);
    }

    private void removeNodeFromEdgeWithOneWeightInOrderGraph(Node node) {
        if (this.edgeWithOneWeightInOrderGraph.containsKey(node)) {
            this.edgeWithOneWeightInOrderGraph.remove(node);
        }
        for (Map.Entry<Node, List<Node>> entry : this.edgeWithOneWeightInOrderGraph.entrySet()) {
            List<Node> childs = entry.getValue();
            if (childs.contains(node)) {
                childs.remove(node);
            }
        }
    }

    private void removeNodeFromFatherNode2ChildNodesInOrderGraph(Node node) {
        if (this.fatherNode2ChildNodesInOrderGraph.containsKey(node)) {
            this.fatherNode2ChildNodesInOrderGraph.remove(node);
        }
        for (Map.Entry<Node, List<Node>> entry : this.fatherNode2ChildNodesInOrderGraph.entrySet()) {
            List<Node> childs = entry.getValue();
            if (childs.contains(node)) {
                childs.remove(node);
            }
        }
    }

    private synchronized void deleteRule(int priority, Rule rule) {
        RulePri rp = new RulePri();
        rp.setRule(rule);
        rp.setPriority(priority);
        this.deleteRules.add(rp);
    }

    private synchronized void installRule(int priority, Rule rule) {
        RulePri rp = new RulePri();
        rp.setRule(rule);
        rp.setPriority(priority);
        this.installRules.add(rp);
        System.out.println("add a rule to installrules: " + rule.toString());
    }

    private void addTrace(String pktHash, Trace trace) {
        addCount(trace, pktHash);
        if (root == null) {
            root = trace.firstNode;
        } else {
            root.augment(trace.firstNode, pktHash, trace, this);
        }
        pkt2trace.put(pktHash, trace);
    }

    private void addCount(Trace trace, String pktHash) {
        Node node = trace.firstNode;
        while (node != null) {
            node.count++;
            node = node.pkt2nextNodeinTrace.get(pktHash);
        }
    }

    private void addEntryToFatherNode2ChildNodesInOrderGraph(Node father, Node child) {
        if (!this.fatherNode2ChildNodesInOrderGraph.containsKey(father)) {
            List<Node> childs = new LinkedList<Node>();
            childs.add(child);
            this.fatherNode2ChildNodesInOrderGraph.put(father, childs);
        } else {
            this.fatherNode2ChildNodesInOrderGraph.get(father).add(child);
        }
    }

    //src -> dst
    private void addEntryToeEdgeWithOneWeightInOrderGraph(Node src, Node dst) {
        if (!this.edgeWithOneWeightInOrderGraph.containsKey(src)) {
            List<Node> nodes = new ArrayList<Node>();
            nodes.add(dst);
            this.edgeWithOneWeightInOrderGraph.put(src, nodes);
        } else {
            this.edgeWithOneWeightInOrderGraph.get(src).add(dst);
        }
    }

    //setup order graph
    private void initializeOrderGraphOfTrace(Trace trace, String pktHash) {
        Node node = trace.firstNode;
        List<Node> allTNodesWithFalseBranch = new LinkedList<Node>();
        while (node.pkt2nextNodeinTrace.get(pktHash) != null) {
            Node nextNode = node.pkt2nextNodeinTrace.get(pktHash);
            if (node instanceof TNode) {
                //allTNodes.add(node);//all tNodes
                TNode tNode = (TNode) node;
                if (tNode.getChild(false) != null) {
                    //this is false branch, generates a rule
                    //only up link
                    //weight = 1
                    for (Node tNodeWithFalseBranch : allTNodesWithFalseBranch) {
                        tNodeWithFalseBranch.fatherNodesinOrderGraph.add(node);

                        //handle map
                        this.addEntryToFatherNode2ChildNodesInOrderGraph(node, tNodeWithFalseBranch);

                        //handle edge
                        this.addEntryToeEdgeWithOneWeightInOrderGraph(node, tNodeWithFalseBranch);

                    }
                    //add
                    allTNodesWithFalseBranch.add(tNode);

                    // mark to install rule
                    node.ruleChanged = true;
                } else {
                    //this is true branch
                    nextNode.fatherNodesinOrderGraph.add(node);

                    //handle map
                    this.addEntryToFatherNode2ChildNodesInOrderGraph(node, nextNode);

                    //handle edge
                    this.addEntryToeEdgeWithOneWeightInOrderGraph(node, nextNode);
                }
            } else if (node instanceof VNode) {
                nextNode.fatherNodesinOrderGraph.add(node);

                //handle map
                this.addEntryToFatherNode2ChildNodesInOrderGraph(node, nextNode);
            }
            node = node.pkt2nextNodeinTrace.get(pktHash);
        }
        // handle LNode
        for (Node tNode : allTNodesWithFalseBranch) {
            tNode.fatherNodesinOrderGraph.add(node);

            // handle map
            this.addEntryToFatherNode2ChildNodesInOrderGraph(node, tNode);

            // handle edge
            this.addEntryToeEdgeWithOneWeightInOrderGraph(node, tNode);
        }

        // mark to install rule
        node.ruleChanged = true;
    }

    private void traverseTTtoInstallRule(Node root) {
        if (root instanceof TNode) {
            TNode tNode = (TNode) root;
            if (tNode.getChild(false) != null) {
                traverseTTtoInstallRule(tNode.getChild(false));
            }
            if (tNode.ruleChanged) {
                if (!(tNode.getChild(true) instanceof LNode)) {
                    this.installRule(tNode.priority, tNode.rule);
                }
                tNode.ruleChanged = false;
            }
            if (tNode.getChild(true) != null) {
                traverseTTtoInstallRule(tNode.getChild(true));
            }
        } else if (root instanceof VNode) {
            VNode vNodeRoot = (VNode) root;
            for (Map.Entry<String, Node> entry : vNodeRoot.subtree.entrySet()) {
                String value = entry.getKey();
                Node childNode = entry.getValue();
                traverseTTtoInstallRule(childNode);
            }
        } else {
            LNode lNode = (LNode) root;
            if (lNode.ruleChanged) {
                this.installRule(lNode.priority, lNode.rule);
                lNode.ruleChanged = false;
            }
        }
    }

    private int simpleUpdatePriority(Node node, int priority) {
        int returnInt = -1;
        int oldPriority = node.priority;
        if (oldPriority != priority) {
            node.priority = priority;
            returnInt = oldPriority;
        }
        return returnInt;
    }

    private void traverseTTtoUpdatePriority3(Node root) {
        if (root instanceof TNode) {
            TNode tNode = (TNode) root;
            if (tNode.getChild(false) != null) {
                traverseTTtoUpdatePriority3(tNode.getChild(false));
            }

            int oldPriority = root.priority;
            oldPriority = simpleUpdatePriority(root, globalPriority);
            if (((TNode) root).rule != null) {
                root.ruleChanged = true;

                if (!root.firstAdd) {
                    this.deleteRule(oldPriority, ((TNode) root).rule);
                } else {
                    root.firstAdd = false;
                }
                if (!(tNode.getChild(true) instanceof LNode)) {
                    this.installRule(globalPriority, ((TNode) root).rule);
                }

            }

			/*int oldPriority;
			if ((oldPriority = simpleUpdatePriority(root, globalPriority)) != -1) {
				if (((TNode) root).rule != null) {
					root.ruleChanged = true;
					
					if (!root.firstAdd) {
						this.deleteRule(oldPriority, ((TNode) root).rule);
					} else {
						root.firstAdd = false;
					}
					if (!(tNode.getChild(true) instanceof LNode)) {
						this.installRule(globalPriority, ((TNode) root).rule);
					}
					
				}
			}*/
            globalPriority++;
            if (tNode.getChild(true) != null) {
                traverseTTtoUpdatePriority3(tNode.getChild(true));
            }
        } else if (root instanceof VNode) {
            simpleUpdatePriority(root, globalPriority); // VNode does not have rule
            VNode vNodeRoot = (VNode) root;
            for (Map.Entry<String, Node> entry : vNodeRoot.subtree.entrySet()) {
                String value = entry.getKey();
                Node childNode = entry.getValue();
                traverseTTtoUpdatePriority3(childNode);
            }
        } else {
            int oldPriority;
            if ((oldPriority = simpleUpdatePriority(root, globalPriority)) != -1) {
                if (((LNode) root).rule != null) {
                    root.ruleChanged = true;

                    this.deleteRule(oldPriority, ((LNode) root).rule);
                    this.installRule(globalPriority, ((LNode) root).rule);
                }
            }
            // need to handle the case that not update the priority
            else if (root.firstAdd) {
                this.installRule(globalPriority, ((LNode) root).rule);
            }
            globalPriority++;
        }
    }

    private void traverseTTtoUpdatePriority2(Node currentNodeInTrace, String pktHash, Node root) {
        Node nextNodeInTrace = null;
        if (currentNodeInTrace == null) {
            System.out.println("get null error in traverse");
        } else {
            nextNodeInTrace = currentNodeInTrace.pkt2nextNodeinTrace.get(pktHash);
            System.out.println("node in traverse: " + currentNodeInTrace.toString());
        }

        if (root instanceof TNode) {
            TNode tNodeRoot = (TNode) root;
            if (tNodeRoot.getChild(false) != null) {
                if (tNodeRoot.getChild(false).equals(nextNodeInTrace)) {
                    //should traverse, in false branch
                    traverseTTtoUpdatePriority2(nextNodeInTrace, pktHash, tNodeRoot.getChild(false));
                }
            }
            int oldPriority;
            if ((oldPriority = updatePriority(root)) != -1) {
                if (((TNode) root).rule != null) {
                    root.ruleChanged = true;
                    if (!root.firstAdd) {
                        this.deleteRule(oldPriority, ((TNode) root).rule);
                    } else {
                        root.firstAdd = false;
                    }
                }
            }
            if (tNodeRoot.getChild(true) != null) {
                traverseTTtoUpdatePriority2(nextNodeInTrace, pktHash, tNodeRoot.getChild(true));
            }
        } else if (root instanceof VNode) {
            updatePriority(root); // VNode does not have rule
            VNode vNodeRoot = (VNode) root;
            for (Map.Entry<String, Node> entry : vNodeRoot.subtree.entrySet()) {
                String value = entry.getKey();
                Node childNode = entry.getValue();
                traverseTTtoUpdatePriority2(nextNodeInTrace, pktHash, childNode);
            }
        } else {
            //LNode
            int oldPriority;
            if ((oldPriority = updatePriority(root)) != -1) {
                if (((LNode) root).rule != null) {
                    root.ruleChanged = true;
                    if (!root.firstAdd) {
                        this.deleteRule(oldPriority, ((LNode) root).rule);
                    } else {
                        root.firstAdd = false;
                    }

                }
            }
        }
    }

    // return old priority, -1 means unchanged
    private int updatePriority(Node node) {
        int returnInt = -1;
        int oldPriority = node.priority;
        int max = Integer.MIN_VALUE;
        for (Node srcNode : node.fatherNodesinOrderGraph) {
            int weight = 0;
            if (this.edgeWithOneWeightInOrderGraph.containsKey(srcNode)) {
                if (this.edgeWithOneWeightInOrderGraph.get(srcNode).contains(node)) {
                    weight = 1;
                }
            }
            int temp = weight + srcNode.priority;
            if (max < temp) max = temp;
        }
        if (max < node.priority) max = node.priority;
        node.priority = max;
        if (max != oldPriority) {
            // changed
            returnInt = oldPriority;
        }
        return returnInt;
    }

    public void show(Node root) {
        System.out.println(root.toString() + ", priority: " + root.priority);
        if (root instanceof TNode) {
            TNode tNode = (TNode) root;

            if (tNode.getChild(false) != null) {
                show(tNode.getChild(false));
            }
            if (tNode.getChild(true) != null) {
                show(tNode.getChild(true));
            }
        } else if (root instanceof VNode) {
            VNode vNode = (VNode) root;
            for (Map.Entry<String, Node> entry : vNode.subtree.entrySet()) {
                show(entry.getValue());
            }
        } else {
            return;
        }
    }

    public Set<RulePri> getDeleteRules() {
        return deleteRules;
    }

    public void cleanDeleteRules() {
        this.deleteRules.clear();
    }

    public Set<RulePri> getInstallRules() {
        return installRules;
    }

    public void cleanInstallRules() {
        this.installRules.clear();
    }
}
