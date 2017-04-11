/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.match;


import org.snlab.maple.rule.field.MapleMatchField;

import java.util.Collections;
import java.util.Set;

public class MapleMatch {
    private MapleMatchField field;
    private Set<ValueMaskPair> matchset;

    public MapleMatch(MapleMatchField field, Set<ValueMaskPair> matchset) {
        this.field = field;
        this.matchset = matchset;
    }

    public MapleMatchField getField() {
        return field;
    }

    public Set<ValueMaskPair> getMatchset() {
        return Collections.unmodifiableSet(matchset);
    }
}