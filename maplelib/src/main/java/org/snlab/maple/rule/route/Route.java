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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Route {

    private Map<MapleTopology.NodeId,Map<MapleTopology.PortId,Forward>> rulesMap=new HashMap<>();

    private Multimap<MapleTopology.NodeId,MapleTopology.PortId> dropRules = HashMultimap.create();

    public void addRule(@Nullable MapleTopology.NodeId node, @Nullable MapleTopology.PortId port, Forward forward){
        for (ForwardAction.Action action : forward.getActions()) {
            Preconditions.checkArgument(!(action instanceof ForwardAction.Drop)); //TODO no need to check
        }
        Map<MapleTopology.PortId, Forward> portForwardMap = rulesMap.get(node);
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

    public void updateDropIfneed(@Nonnull MapleTopology.PortId inportId){
        MapleTopology.NodeId node = inportId.getNodeId();
        Map<MapleTopology.PortId, Forward> portForwardMap = rulesMap.get(node);
        if(portForwardMap!=null){
            //TODO
        }
    }

    public Map<MapleTopology.NodeId, Map<MapleTopology.PortId, Forward>> getRulesMap() {
        return rulesMap;
    }

    public Multimap<MapleTopology.NodeId, MapleTopology.PortId> getDropRules() {
        return dropRules;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("Route[");
        for (Map.Entry<MapleTopology.NodeId, Map<MapleTopology.PortId, Forward>> entry : rulesMap.entrySet()) {
            MapleTopology.NodeId node = entry.getKey();
            for (Map.Entry<MapleTopology.PortId, Forward> entry1 : entry.getValue().entrySet()) {
                MapleTopology.PortId port = entry1.getKey();
                sb.append("(");
                sb.append(node==null?"*":node.getId());
                sb.append(";");
                sb.append(port==null?"*":port.getId());
                sb.append(";");
                sb.append(entry1.getValue());
                sb.append("),");
            }
        }
        for (Map.Entry<MapleTopology.NodeId, MapleTopology.PortId> entry : dropRules.entries()) {
            MapleTopology.NodeId node = entry.getKey();
            MapleTopology.PortId port = entry.getValue();
            sb.append("(");
            sb.append(node==null?"*":node.getId());
            sb.append(";");
            sb.append(port==null?"*":port.getId());
            sb.append(";");
            sb.append("Drop");
            sb.append("),");
        }
        sb.append("]");
        return sb.toString();
    }
}
