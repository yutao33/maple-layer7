/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.route;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import org.snlab.maple.env.MapleTopology;

public class Route {

    private Table<MapleTopology.Node,MapleTopology.Port,Forward> ruleTable=HashBasedTable.create();

    private Multimap<MapleTopology.Node,MapleTopology.Port> tmpDropRuleTable= LinkedHashMultimap.create();

    public Route(){

    }
}
