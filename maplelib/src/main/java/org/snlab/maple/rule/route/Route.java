/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.route;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.snlab.maple.env.MapleTopology;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Route {

    private Map<MapleTopology.Node,Map<MapleTopology.Port,Forward>> rulesMap=new HashMap<>();

    private Multimap<MapleTopology.Node,MapleTopology.Port> DropRules = HashMultimap.create();

    public void addRule(@Nullable MapleTopology.Node node,@Nullable MapleTopology.Port port,Forward forward){
        for (ForwardAction.Action action : forward.getActions()) {
            Preconditions.checkArgument(!(action instanceof ForwardAction.Drop)); //TODO no need to check
        }
        Map<MapleTopology.Port, Forward> portForwardMap = rulesMap.get(node);
        if(portForwardMap!=null){
            Forward oldForward = portForwardMap.get(port);
            if(oldForward!=null){
                oldForward.concat(forward);
            } else {
                portForwardMap.put(port,forward);
            }
        } else {
            portForwardMap = new HashMap<>();
            portForwardMap.put(port,forward);
            rulesMap.put(node,portForwardMap);
        }
    }

    public void addDropIfneed(@Nullable MapleTopology.Port ingress){

    }

    public Map<MapleTopology.Node, Map<MapleTopology.Port, Forward>> getRulesMap() {
        return rulesMap;
    }

    public Multimap<MapleTopology.Node, MapleTopology.Port> getDropRules() {
        return DropRules;
    }

    @Override
    public String toString() {
        return "Route{\n" +
                "rulesMap=" + rulesMap +
                ", DropRules=" + DropRules +
                "\n}";
    }
}
