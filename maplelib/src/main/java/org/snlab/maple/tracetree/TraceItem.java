/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;


import org.snlab.maple.flowrule.MapleMatchField;

import java.util.ArrayList;
import java.util.List;

public class TraceItem{

    private MapleMatchField field;

    private byte[] mask;

    private List<byte[]> values;

    private TraceItem.Type type;

    private boolean testresult;

    public TraceItem(MapleMatchField field, byte[] mask, List<byte[]> values, Type type, boolean testresult) {
        assert values!=null;//TODO:
        this.field = field;
        if(mask!=null){
            this.mask = mask.clone();
        }
        this.values = new ArrayList<>(values);
        this.type=type;
        this.testresult=testresult;
    }

    public TraceItem(MapleMatchField field, byte[] mask, byte[] value, Type type, boolean testresult) {
        assert value!=null;//TODO:
        this.field = field;
        if(mask!=null){
            this.mask = mask.clone();
        }
        this.values = new ArrayList<>();
        this.values.add(value);
        this.type=type;
        this.testresult=testresult;
    }

    public MapleMatchField getField() {
        return field;
    }

    public byte[] getMask() {
        return mask;
    }

    public List<byte[]> getValues() {
        return values;
    }

    public Type getType() {
        return type;
    }

    public boolean getTestresult() {
        return testresult;
    }

    public enum Type{
        TEST,
        VALUE;
    }
}
