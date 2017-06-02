/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import com.google.common.base.Objects;
import org.snlab.maple.packet.MaplePacket;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TrackedMap<K,V> {

    private Map<K,TrackedUnit<V>> data = new HashMap<>();

    private IReExecHandler handler;

    private MaplePacket pkt;

    TrackedMap(IReExecHandler handler){ //packet private
        this.handler = handler;
    }

    TrackedMap(MaplePacket pkt, TrackedMap<K,V> m1){ // packet private
        this.data = m1.data;
        this.handler = m1.handler;
        this.pkt = pkt;
    }

    @Nullable
    public V get(K key){
        TrackedUnit<V> unit = data.get(key);
        if(unit==null){
            unit=new TrackedUnit<>(null);
            data.put(key,unit);
        }
        if(pkt!=null){
            unit.trackSet.track(pkt);
        }
        return unit.value;
    }

    public void put(K key, V value){
        TrackedUnit<V> unit = data.get(key);
        if(unit==null){
            unit=new TrackedUnit<>(value);
            data.put(key,unit);
        } else {
            if(!Objects.equal(value,unit.value)){
                //if(unit.value!=null){
                    unit.trackSet.reexec(handler);
                //}
            }
            unit.value = value;
        }
    }


    private class TrackedUnit<TYPE>{
        TrackSet trackSet = new TrackSet();
        TYPE value;
        TrackedUnit(TYPE v){
            value=v;
        }
    }
}
