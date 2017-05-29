/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.match;


import com.google.common.base.Preconditions;
import org.snlab.maple.packet.types.IPv4Address;
import org.snlab.maple.packet.types.MacAddress;
import org.snlab.maple.packet.util.HexString;

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

    @Nonnull
    public ByteArray bitAnd(@Nullable ByteArray a2) {
        if (a2 == null) {
            return this;
        }
        Preconditions.checkArgument(value.length == a2.value.length);
        byte[] v = value.clone();
        for (int i = 0; i < v.length; i++) {
            v[i] &= a2.value[i];
        }
        return new ByteArray(v);
    }

    @Nonnull
    public ByteArray bitOr(@Nonnull ByteArray a2) {
        assert value.length == a2.value.length;
        byte[] v = value.clone();
        for (int i = 0; i < v.length; i++) {
            v[i] |= a2.value[i];
        }
        return new ByteArray(v);
    }

    @Nonnull
    public ByteArray not() {
        byte[] v = value.clone();
        for (int i = 0; i < v.length; i++) {
            v[i] = (byte) ~v[i];
        }
        return new ByteArray(v);
    }

    public boolean isFullZero() {
        for (int i = 0; i < value.length; i++) {
            if (value[i] != 0)
                return false;
        }
        return true;
    }

    public String toMacAddressString() {
        if (value.length != 6) {
            throw new Error("toMacAddressString value.length!=6");
        }
        return MacAddress.of(value).toString();
    }

    public String toIpv4AddressString() {
        Preconditions.checkState(value.length == 4);
        return IPv4Address.of(value).toString();
    }

    private boolean bittest(int i) {
        int k = value[i / 8];
        return ((k >> (i % 8)) & 0x1) > 0;
    }

    public int toPrefixMaskNum(int bitlength) {
        Preconditions.checkArgument(bitlength <= value.length * 8 && bitlength > 0);
        int c = 0;
        int i = bitlength - 1;
        for (; i >= 0; i--) {
            if (bittest(i)) {
                c++;
            } else {
                break;
            }
        }
        for (; i >= 0; i--) {
            if (bittest(i)) {
                throw new RuntimeException("error prefix");
            }
        }
        return c;
    }

    public byte toByte(){
        Preconditions.checkState(value.length==1);
        return value[0];
    }

    public short toShort() {
        Preconditions.checkState(value.length == 2);
        return (short) ((value[0] << 8) | (value[1] & 0xff));
    }

    public byte[] getBytes() {
        return value.clone();
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
        return HexString.toHexString(value);
    }
}
