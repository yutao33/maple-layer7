/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.match;


import org.snlab.maple.rule.field.MapleMatchField;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Immutable
public class MapleMatch {
    private final MapleMatchField field;
    private final ValueMaskPair match; //NOTE matchSet is not intersectant

    public MapleMatch(MapleMatchField field, ValueMaskPair match) {
        this.field = field;
        this.match = match;
    }

//    public MapleMatchField getField() {
//        return field;
//    }

    public ValueMaskPair getMatch() {
        return match;
    }

//    public boolean getMatchProperSubSetOrfalse(@Nonnull Set<ValueMaskPair> subset, Set<ValueMaskPair> newset) {
//        int size = matchSet.size();
//        Object[] pairs = matchSet.toArray();
//        boolean[] allcontain = new boolean[size];
//        for (int i = 0; i < size; i++) {
//            allcontain[i] = false;
//        }
//        newset.clear();
//        for (ValueMaskPair a : subset) {
//            for (int i = 0; i < size; i++) {
//                ValueMaskPair b = ValueMaskPair.getSubSet((ValueMaskPair) pairs[i], a);
//                if (b != null) {
//                    if (b.equals(pairs[i])) {
//                        allcontain[i] = true;
//                    }
//                    newset.add(b);
//                }
//            }
//        }
//        for (int i = 0; i < size; i++) {
//            if (!allcontain[i]) {
//                return true;
//            }
//        }
//        return false;
//    }

    @Override
    public String toString() {
        return "MapleMatch{" +
                "field=" + field +
                ", match=" + match +
                '}';
    }
}