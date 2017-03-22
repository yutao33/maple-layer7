/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api.tracetree;

import java.util.LinkedList;
import java.util.List;

public class CompressionItem {

	boolean is;
	
	String value;
	
	public static boolean canRemove(List<CompressionItem> originalItemList, 
			CompressionItem newItem) {
		if (originalItemList.isEmpty()) return false;
		for (CompressionItem originalItem: originalItemList) {
			if (originalItem.is && newItem.is) return true;
			else if (originalItem.is && !newItem.is) return true;
			else if (!originalItem.is && newItem.is) {
				if (originalItem.value.equals(newItem.value)) return true;
			}
			else if (!originalItem.is && !newItem.is) {
				if (originalItem.value.equals(newItem.value)) return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		CompressionItem item1 = new CompressionItem();
		item1.is = false;
		item1.value = "82";
		CompressionItem item2 = new CompressionItem();
		item2.is = true;
		item2.value = "81";
		CompressionItem item3 = new CompressionItem();
		item3.is = true;
		item3.value = "81";
		CompressionItem item4 = new CompressionItem();
		item4.is = false;
		item4.value = "80";
		CompressionItem item5 = new CompressionItem();
		item5.is = true;
		item5.value = "81";
		CompressionItem item6 = new CompressionItem();
		item6.is = true;
		item6.value = "81";
		
		List<CompressionItem> originalSet = new LinkedList<CompressionItem>();
		originalSet.add(item1);
		originalSet.add(item2);
		originalSet.add(item3);
		originalSet.add(item4);
		originalSet.add(item5);
		originalSet.add(item6);
		
		List<CompressionItem> compressSet = new LinkedList<CompressionItem>();
		for(CompressionItem item: originalSet) {
			if (!canRemove(compressSet, item)) {
				compressSet.add(item);
			}
		}
		
		System.out.println(compressSet.size());
	}
}
