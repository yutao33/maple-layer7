/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule;

import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.MapleMatch;
import org.snlab.maple.rule.match.MapleMatchInPort;
import org.snlab.maple.rule.route.Forward;
import org.snlab.maple.rule.route.ForwardAction;
import org.snlab.maple.rule.route.Route;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapleRule {

    public enum Status {  //TODO UPDATE
        NONE,
        DELETE,
        INSTALL,
        UPDATE,
        DELETED,
        INSTALLED
    }

    private Map<MapleMatchField, MapleMatch> matches;
    private Route route;

    private int priority;

    private Status status = Status.NONE;

    public MapleRule(Map<MapleMatchField, MapleMatch> matches, List<Forward> route) {
        this.matches = new EnumMap<>(matches);
        buildRoute(route);
    }

    private void buildRoute(List<Forward> forwards) {
        this.route = new Route();
        for (Forward forward : forwards) {
            if (!isdrop(forward)) {
                MapleTopology.PortId inport = forward.getInport();
                if (inport != null) {
                    addifneed(inport, forward);
                } else {
                    MapleTopology.NodeId fn = findnode(forward);
                    if (fn == null) { //maybe punt
                        addaccordingtomatch(forward);
                    } else {
                        addifneed(fn, forward);
                    }
                }
            }
        }
    }

    private void addaccordingtomatch(Forward f) {
        MapleMatchInPort inportMatch = (MapleMatchInPort) matches.get(MapleMatchField.INPORT);
        if (inportMatch == null) {
            this.route.addRule(null, null, f);
        } else {
            for (MapleTopology.PortId port : inportMatch.getPorts()) {
                this.route.addRule(port.getNodeId(), port, f);
            }
            for (MapleTopology.NodeId node : inportMatch.getNodes()) {
                this.route.addRule(node, null, f);
            }
        }
    }

    private void addifneed(MapleTopology.NodeId node, Forward f) {
        MapleMatchInPort inportMatch = (MapleMatchInPort) matches.get(MapleMatchField.INPORT);
        if (inportMatch == null) {
            this.route.addRule(node, null, f);
        } else {
            if (inportMatch.getNodes().contains(node)) {
                this.route.addRule(node, null, f);
            }
            Set<MapleTopology.PortId> ports = inportMatch.getPorts();
            for (MapleTopology.PortId port : ports) {
                if (port.getNodeId().equals(node)) {
                    this.route.addRule(node, port, f);
                }
            }
        }
    }

    private void addifneed(MapleTopology.PortId inport, Forward f) {
        MapleMatchInPort inportMatch = (MapleMatchInPort) matches.get(MapleMatchField.INPORT);
        if (inportMatch == null) {
            this.route.addRule(inport.getNodeId(), inport, f);//TODO check
        } else {
            if (inportMatch.getPorts().contains(inport) ||
                    inportMatch.getNodes().contains(inport.getNodeId())) {
                this.route.addRule(inport.getNodeId(), inport, f);
            }
        }
    }

    private MapleTopology.NodeId findnode(Forward f) {
        for (ForwardAction.Action act : f.getActions()) {
            if (act instanceof ForwardAction.OutPut) {
                ForwardAction.OutPut act1 = (ForwardAction.OutPut) act;
                return act1.getPortId().getNodeId();
            }
        }
        return null;
    }

    private boolean isdrop(Forward f) {
        List<ForwardAction.Action> actions = f.getActions();
        if (actions.size() == 1 && actions.get(0) instanceof ForwardAction.Drop) {
            return true;
        } else {
            return false;
        }
    }


    public Map<MapleMatchField, MapleMatch> getMatches() {
        return matches;
    }

    public Route getRoute() {
        return route;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MapleRule{" +
                "status=" + status +
                ", matches=" + matches +
                ", route=" + route +
                ", priority=" + priority +
                '}';
    }
}
