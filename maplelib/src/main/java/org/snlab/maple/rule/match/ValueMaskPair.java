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

@Immutable
public class ValueMaskPair {
    private final ByteArray mask;
    private final ByteArray value;

    public ValueMaskPair(@Nonnull ByteArray value, @Nullable ByteArray mask) {
        if(mask!=null&&!value.bitAnd(mask.not()).isFullZero()){
            throw new RuntimeException("bad ValueMaskPair");
        }
        this.mask = mask;
        this.value = value;
    }

    @Nullable
    public ByteArray getMask() {
        return mask;
    }

    @Nonnull
    public ByteArray getValue() {
        return value;
    }


    private static ByteArray getfullone(int l) {
        byte[] bytes = new byte[l];
        for (int i = 0; i < l; i++) {
            bytes[i] = (byte) 0xff;
        }
        return new ByteArray(bytes);
    }

    private static boolean allone(ByteArray a) {
        for (int i = 0; i < a.length(); i++) {
            if (a.get(i) != (byte) 0xff) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public static ValueMaskPair getSubSet(ValueMaskPair a, ValueMaskPair b) {
        //        1xxx0   xxx10   1xx10
        //mask    10001   00011   10011
        //value   10000   00010   10010

        //        xxxx_1111  xxxx_x111
        //mask    0000_1111  0000_0111
        //value   0000_1111  0000_0111

        assert a.value.length() == b.value.length();
        ByteArray maska = a.mask;
        if (maska == null) {
            maska = getfullone(a.value.length());
        }
        ByteArray maskb = b.mask;
        if (maskb == null) {
            maskb = getfullone(b.value.length());
        }
        if(a.value.bitAnd(maskb).equals(b.value.bitAnd(maska))){
            ByteArray m = maska.bitOr(maskb);
            ByteArray v = a.value.bitOr(b.value);
            if (allone(m)) {
                m = null;
            }
            return new ValueMaskPair(v, m);
        } else {
            return null;
        }

//        ByteArray j1 = a.value.bitOr(maska.not().bitAnd(b.value)).bitAnd(b.mask);
//        if (j1.equals(b.value)) {
//            ByteArray j2 = b.value.bitOr(maskb.not().bitAnd(a.value)).bitAnd(a.mask);
//            if (j2.equals(b.value)) {
//                ByteArray m = maska.bitOr(maskb);
//                ByteArray v = a.value.bitOr(b.value);
//                if (allone(m)) {
//                    m = null;
//                }
//                return new ValueMaskPair(v, m);
//            } else {
//                return null;
//            }
//        } else {
//            return null;
//        }
    }

    public boolean testMatch(@Nonnull ByteArray value){
        if(mask!=null){
            value=value.bitAnd(mask);
        }
        return value.equals(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueMaskPair that = (ValueMaskPair) o;

        if (mask != null ? !mask.equals(that.mask) : that.mask != null) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = mask != null ? mask.hashCode() : 0;
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ValueMaskPair{" +
                "mask=" + mask +
                ", value=" + value +
                '}';
    }
}
