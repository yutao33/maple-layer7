/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.maple.tracetree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Node {

    public Map<String, Node> pkt2nextNodeinTrace = new HashMap<String, Node>();

    public int count; //how many trace share this node, if 0, should remove this node

    public Map<String, Node> pkt2fatherinTrace = new HashMap<String, Node>();

    //public List<Node> nextNodesinOrderGraph = new LinkedList<Node>();

    public List<Node> fatherNodesinOrderGraph = new LinkedList<Node>();

    public boolean ruleChanged;

    public int priority;

    public boolean firstAdd;

    public abstract void delete(Node node);

    public abstract void augment(Node node, String pktHash, Trace trace, TraceTree tt);
}
