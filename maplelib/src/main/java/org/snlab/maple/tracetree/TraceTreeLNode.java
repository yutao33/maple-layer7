/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;

import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.rule.route.Forward;

import java.util.List;

public class TraceTreeLNode extends TraceTreeNode {
    private List<Forward> route;
    private MapleRule rule;

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
}
