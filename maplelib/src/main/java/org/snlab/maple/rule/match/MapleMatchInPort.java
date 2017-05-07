/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.match;

import com.google.common.base.Preconditions;
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.rule.field.MapleMatchField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MapleMatchInPort extends MapleMatch {
    private final Set<MapleTopology.PortId> ports;
    private final Set<MapleTopology.NodeId> nodes;

    public MapleMatchInPort(Set<MapleTopology.PortId> ports, Set<MapleTopology.NodeId> nodes) {
        super(MapleMatchField.INPORT, null);  //NOTE match=null
        this.nodes = nodes == null ? Collections.<MapleTopology.NodeId>emptySet() : new HashSet<>(nodes);
        this.ports = ports == null ? Collections.<MapleTopology.PortId>emptySet() : new HashSet<>(ports);
        Preconditions.checkState(this.nodes.size() + this.ports.size() > 0);
    }

    @Nonnull
    public Set<MapleTopology.PortId> getPorts() {
        return Collections.unmodifiableSet(ports);
    }

    @Nonnull
    public Set<MapleTopology.NodeId> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    @Nullable
    public MapleMatchInPort getSubMatchInPort(Set<MapleTopology.PortId> ports, Set<MapleTopology.NodeId> nodes) {
        Set<MapleTopology.PortId> subMatchPorts = new HashSet<>();
        Set<MapleTopology.NodeId> subMatchNodes = new HashSet<>();
        if (ports != null) {
            for (MapleTopology.PortId port : ports) {
                if (this.ports.contains(port) || this.nodes.contains(port.getNodeId())) {
                    subMatchPorts.add(port);
                }
            }
        }
        if (nodes != null) {
            for (MapleTopology.NodeId node : nodes) {
                if (this.nodes.contains(node)) {
                    subMatchNodes.add(node);
                }
            }
            for (MapleTopology.PortId port : this.ports) {
                if (nodes.contains(port.getNodeId())) {
                    subMatchPorts.add(port);
                }
            }
        }
        if (subMatchNodes.size() + subMatchPorts.size() > 0) {
            return new MapleMatchInPort(subMatchPorts, subMatchNodes);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        //if (!super.equals(o)) return false;

        MapleMatchInPort that = (MapleMatchInPort) o;

        if (!ports.equals(that.ports)) return false;
        return nodes.equals(that.nodes);
    }

    @Override
    public int hashCode() {
        int result = 0;// super.hashCode();
        result = 31 * result + ports.hashCode();
        result = 31 * result + nodes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MapleMatchInPort{" +
                "ports=" + ports +
                ", nodes=" + nodes +
                '}';
    }
}
