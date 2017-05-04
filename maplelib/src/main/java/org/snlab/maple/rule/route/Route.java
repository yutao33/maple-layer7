/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.route;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import org.snlab.maple.env.MapleTopology;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Route {

    private Table<MapleTopology.Node,MapleTopology.Port,Forward> ruleTable=HashBasedTable.create();//TODO table is not appreciate

    private Multimap<MapleTopology.Node,MapleTopology.Port> DropRules = HashMultimap.create();

    public void addRule(@Nullable MapleTopology.Node node,@Nullable MapleTopology.Port port,Forward forward){
        for (ForwardAction.Action action : forward.getActions()) {
            Preconditions.checkArgument(!(action instanceof ForwardAction.Drop));
        }
        Forward f1 = ruleTable.get(node, port);
        if(f1!=null){
            f1.concat(forward);
        } else {
            ruleTable.put(node, port, forward);
        }
    }

    public void addDropIfneed(@Nullable MapleTopology.Port ingress){

    }

    public Table<MapleTopology.Node, MapleTopology.Port, Forward> getRuleTable() {
        return ruleTable;
    }

    public Multimap<MapleTopology.Node, MapleTopology.Port> getDropRules() {
        return DropRules;
    }
}
