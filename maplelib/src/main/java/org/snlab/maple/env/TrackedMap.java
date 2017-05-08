/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TrackedMap<K,V> {

    private Map<K,V> data = new HashMap<>();

    @Nullable
    public V get(K key){
        return data.get(key);
    }

    public void put(K key, V value){
        data.put(key,value);
    }
}
