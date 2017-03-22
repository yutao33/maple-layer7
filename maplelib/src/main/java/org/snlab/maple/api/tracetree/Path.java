/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.maple.api.tracetree;

import java.util.ArrayList;
import java.util.List;

public class Path extends RouteAction{

	public List<String> links = new ArrayList<String>();
	
	public String lastTpId;
	
	// link := <linkId,srcTpId,dstTpId>
	public Path(List<String> links, String lastTpId) {
		this.links = links;
		this.lastTpId = lastTpId;
	}
	
	// path includes lastTpId
	public Path(List<String> path) {
		for (int i = 0; i < path.size() - 1; i++) {
			links.add(path.get(i));
		}
		this.lastTpId = getSrcTpId(path.get(path.size() - 1));
	}
	
	// path_tpId := path+tpId
	// path := path;link
	public Path(String path_tpId) {
		String[] values = path_tpId.split("\\+");
		if (!values[0].equals("null")) {
			String[] links = values[0].split(";");
			for (String link: links) {
				this.links.add(link);
			}
		}
		this.lastTpId = values[1];
	}
	
	// <linkId,srcTpId,dstTpId>
	public static String getLinkId(String value) {
		String rawLink = value.substring(1, value.length() - 1);
		String[] values = rawLink.split(",");
		String linkId = values[0];
		return linkId;
	}
	
	public static String getSrcTpId(String value) {
		String rawLink = value.substring(1, value.length() - 1);
		String[] values = rawLink.split(",");
		String srcTpId = values[1];
		return srcTpId;
	}
	
	public static String getDstTpId(String value) {
		String rawLink = value.substring(1, value.length() - 1);
		String[] values = rawLink.split(",");
		String dstTpId = values[2];
		return dstTpId;
	}
	
	@Override
	public String toString() {
		String path = "";
		for (String link: this.links) {
			path += ";" + link;
		}
		if (path.equals("")) {
			path = "null";
		} else {
			path = path.substring(1);
		}
		return path + "+" + lastTpId;
	}

}
