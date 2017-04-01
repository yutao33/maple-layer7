/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.flowrule;

import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.api.route.MapleAction;

public class MapleRule {
    MapleTopology.Node node;
    MapleMatch match;
    MapleAction action;

    private boolean deleted;
    private boolean newrule;

}