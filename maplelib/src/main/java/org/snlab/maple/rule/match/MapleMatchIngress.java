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

public class MapleMatchIngress extends MapleMatch{
    private final Set<MapleTopology.Port> ports;
    private final Set<MapleTopology.Node> nodes;

    public MapleMatchIngress(Set<MapleTopology.Port> ports, Set<MapleTopology.Node> nodes) {
        super(MapleMatchField.INGRESS, null);
        this.nodes = nodes==null?Collections.<MapleTopology.Node>emptySet():new HashSet<>(nodes);
        this.ports = ports==null?Collections.<MapleTopology.Port>emptySet():new HashSet<>(ports);
        Preconditions.checkState(this.nodes.size()+this.ports.size()>0);
    }

    @Nonnull
    public Set<MapleTopology.Port> getPorts() {
        return Collections.unmodifiableSet(ports);
    }

    @Nonnull
    public Set<MapleTopology.Node> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    @Nullable
    public MapleMatchIngress getSubMatchIngress(Set<MapleTopology.Port> ports, Set<MapleTopology.Node> nodes){
        Set<MapleTopology.Port> subMatchPorts=new HashSet<>();
        Set<MapleTopology.Node> subMatchNodes=new HashSet<>();
        if(ports!=null){
            for (MapleTopology.Port port : ports) {
                if(this.ports.contains(port)||this.nodes.contains(port.getOwner())){
                    subMatchPorts.add(port);
                }
            }
        }
        if(nodes!=null){
            for (MapleTopology.Node node : nodes) {
                if(this.nodes.contains(node)){
                    subMatchNodes.add(node);
                }
            }
            for (MapleTopology.Port port : this.ports) {
                if(nodes.contains(port.getOwner())){
                    subMatchPorts.add(port);
                }
            }
        }
        if(subMatchNodes.size()+subMatchPorts.size()>0){
            return new MapleMatchIngress(subMatchPorts,subMatchNodes);
        }
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
