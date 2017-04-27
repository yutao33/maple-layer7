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

import java.util.HashSet;
import java.util.Set;

public class MapleMatchIngress extends MapleMatch{
    private final Set<MapleTopology.Port> ports;
    private final Set<MapleTopology.Node> nodes;

    public MapleMatchIngress(Set<MapleTopology.Port> ports, Set<MapleTopology.Node> nodes) {
        super(MapleMatchField.INGRESS, null);
        this.ports = new HashSet<>(ports);
        this.nodes = new HashSet<>(nodes);
    }



}
