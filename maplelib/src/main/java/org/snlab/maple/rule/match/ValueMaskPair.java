/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.match;


import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ValueMaskPair {
    private final ByteArray mask;
    private final ByteArray value;

    public ValueMaskPair(@Nonnull ByteArray value, ByteArray mask) {
        this.mask = mask;
        this.value = value;
    }

    public ByteArray getMask() {
        return mask;
    }

    public ByteArray getValue() {
        return value;
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
}
