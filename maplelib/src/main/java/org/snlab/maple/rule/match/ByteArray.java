/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.match;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;

@Immutable
public class ByteArray {
    private final byte[] value;

    public ByteArray(@Nonnull byte[] value) {
        this.value = value.clone();
    }

    public byte get(int index) {
        return value[index];
    }

    public int length() {
        return value.length;
    }

    public ByteArray bitAnd(@Nullable ByteArray a2){
        if(a2==null){
            return this;
        }
        assert value.length==a2.value.length; //TODO
        byte[] v = value.clone();
        for(int i=0;i<v.length;i++){
            v[i]&=a2.value[i];
        }
        return new ByteArray(v);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteArray value1 = (ByteArray) o;

        return Arrays.equals(value, value1.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public String toString() {
        return Arrays.toString(value);
    }
}
