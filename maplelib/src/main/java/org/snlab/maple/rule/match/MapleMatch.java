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
    private final Set<ValueMaskPair> matchSet; //NOTE matchSet is not intersectant

    public MapleMatch(MapleMatchField field, Set<ValueMaskPair> matchset) {
        this.field = field;
        this.matchSet = new HashSet<>(matchset);
    }

    public MapleMatchField getField() {
        return field;
    }

    public Set<ValueMaskPair> getMatchSet() {
        return Collections.unmodifiableSet(matchSet);
    }

    public boolean getMatchProperSubSetOrfalse(@Nonnull Set<ValueMaskPair> subset,Set<ValueMaskPair> newset){
        int size = matchSet.size();
        ValueMaskPair[] pairs =(ValueMaskPair[]) matchSet.toArray();
        boolean [] allcontain=new boolean[size];
        for(int i=0;i<size;i++){
            allcontain[i]=false;
        }
        newset.clear();
        for (ValueMaskPair a : subset) {
            for(int i=0;i<size;i++){
                ValueMaskPair b=ValueMaskPair.getSubSet(pairs[i],a);
                if(b!=null){
                    if(b.equals(pairs[i])){
                        allcontain[i]=true;
                    }
                    newset.add(b);
                }
            }
        }
        for(int i=0;i<size;i++){
            if(!allcontain[i]){
                return true;
            }
        }
        return false;
    }


}