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
    private final ByteArray valueMasked;

    public ValueMaskPair(@Nonnull ByteArray value,@Nullable ByteArray mask) {
        this.mask = mask;
        this.valueMasked = value.bitAnd(mask);
    }

    public ByteArray getMask() {
        return mask;
    }

    public ByteArray getValueMasked() {
        return valueMasked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueMaskPair that = (ValueMaskPair) o;

        if (mask != null ? !mask.equals(that.mask) : that.mask != null) return false;
        return valueMasked.equals(that.valueMasked);
    }

    @Override
    public int hashCode() {
        int result = mask != null ? mask.hashCode() : 0;
        result = 31 * result + valueMasked.hashCode();
        return result;
    }
}
