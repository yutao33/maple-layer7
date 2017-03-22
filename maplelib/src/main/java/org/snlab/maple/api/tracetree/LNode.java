/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.maple.api.tracetree;

public class LNode extends Node{

	public String action;
    
	public Rule rule;
	
	public String pktHash;
	
	//public int priority;

	@Override
	public void delete(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void augment(Node node, String pkt, Trace trace, TraceTree tt) {
		// TODO Auto-generated method stub
		
	}
}
