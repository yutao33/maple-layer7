/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.maple.api.tracetree;

public class Test {
	
	String content = new String();

	public static void main(String[] args){
		Test t1 = new Test();
		Test t2 = new Test();
		t1.content = "111";
		t2.content = "222";
		t1.content = t2.content;
		System.out.println(t1.content);
		t2.content = "333";
		System.out.println(t1.content);
	}
}
