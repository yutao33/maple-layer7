/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.maple.api.tracetree;

public class Rule {

	public Match match;
	
	public Instruction inst;
	
	public String pktHash;
	
	public Rule(Match match, Instruction inst, String pktHash){
		this.match = match;
		this.inst = inst;
		this.pktHash = pktHash;
	}
	
	@Override
	public String toString() {
		return "inst: " + inst.toItem() + " | matches: " + match.toString() + " | pktHash: " + pktHash; 
	}
}
