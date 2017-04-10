/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule;

import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.MapleMatch;
import org.snlab.maple.rule.route.Forward;

import java.util.List;
import java.util.Map;

public class MapleRule {
    private Map<MapleMatchField, MapleMatch> matches;
    private List<Forward> route;

    private int flags;
    private static int ISDELETED_MASK = 0x1;
    private static int ISNEW_MASK = 0x2;

    public MapleRule(Map<MapleMatchField, MapleMatch> matches, List<Forward> route) {
        this.matches = matches;
        this.route = route;
        this.flags = 0;
    }

    public Map<MapleMatchField, MapleMatch> getMatches() {
        return matches;
    }

    public List<Forward> getRoute() {
        return route;
    }

    public boolean isDeleted() {
        return (flags & ISDELETED_MASK) != 0;
    }

    public boolean isNew() {
        return (flags & ISNEW_MASK) != 0;
    }

    public void setIsDeleted(boolean isdeleted) {
        if (isdeleted) {
            flags |= ISDELETED_MASK;
        } else {
            flags &= ~ISDELETED_MASK;
        }
    }

    public void setIsNew(boolean isnew) {
        if (isnew) {
            flags |= ISNEW_MASK;
        } else {
            flags &= ~ISNEW_MASK;
        }
    }

}
