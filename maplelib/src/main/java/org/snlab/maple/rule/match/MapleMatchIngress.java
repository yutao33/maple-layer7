/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.match;

import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.rule.field.MapleMatchField;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MapleMatchIngress extends MapleMatch{
    private final Set<MapleTopology.Port> ports;
    private final Set<MapleTopology.Node> nodes;

    public MapleMatchIngress(Set<MapleTopology.Port> ports, Set<MapleTopology.Node> nodes) {
        super(MapleMatchField.INGRESS, null);
        this.nodes = new HashSet<>(nodes);
        this.ports = new HashSet<>(ports);
    }

    public Set<MapleTopology.Port> getPorts() {
        return Collections.unmodifiableSet(ports);
    }

    public Set<MapleTopology.Node> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    public MapleMatchIngress getSubMatch(){
        
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MapleMatchIngress that = (MapleMatchIngress) o;

        if (!ports.equals(that.ports)) return false;
        return nodes.equals(that.nodes);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ports.hashCode();
        result = 31 * result + nodes.hashCode();
        return result;
    }
}
