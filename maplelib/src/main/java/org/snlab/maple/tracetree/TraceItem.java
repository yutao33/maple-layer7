/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;



public class TraceItem{

    private MapleMatchField field;

    private byte[] mask;

    private byte[] value;

    private TraceItem.Type type;

    private boolean testresult;

    public TraceItem(MapleMatchField field, byte[] mask, byte[] value,Type type,boolean testresult) {
        assert value!=null;//TODO:
        this.field = field;
        if(mask!=null){
            this.mask = mask.clone();
        }
        this.value = value.clone();
        this.type=type;
        this.testresult=testresult;
    }

    public MapleMatchField getField() {
        return field;
    }

    public byte[] getMask() {
        return mask;
    }

    public byte[] getValue() {
        return value;
    }

    public enum Type{
        TEST,
        VALUE;
    }
}
