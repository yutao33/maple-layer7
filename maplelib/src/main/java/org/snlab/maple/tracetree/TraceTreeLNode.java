/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;

import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.MapleMatch;
import org.snlab.maple.rule.route.Forward;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TraceTreeLNode extends TraceTreeNode {
    private List<Forward> route;
    private MapleRule rule;
    private MaplePacket pkt;

    public TraceTreeLNode(List<Forward> route) {
        this.route = route;
    }

    @Override
    public boolean isConsistentWith(Trace.TraceItem item) {
        return false;
    }

    public List<Forward> getRoute() {
        return route;
    }

    public MapleRule getRule() {
        return rule;
    }

    public void removePktTrack(){
        pkt.removeTrack();
    }

    public static TraceTreeLNode build(@Nonnull List<Forward> route,@Nonnull MaplePacket pkt, @Nonnull Map<MapleMatchField, MapleMatch> matchMapBefore) {
        //route:
        //      inport=null, actions=output:openflow:2:3
        //      inport=null, actions=output:openflow:1:2
        //      inport=null, actions=output:openflow:3:3
        //      inport=null, actions=output:openflow:1:1
        Map<MapleMatchField, MapleMatch> match = new EnumMap<>(matchMapBefore);
        TraceTreeLNode l = new TraceTreeLNode(route);
        l.rule = new MapleRule(match, route);
        l.pkt = pkt;
        return l;
    }
}
