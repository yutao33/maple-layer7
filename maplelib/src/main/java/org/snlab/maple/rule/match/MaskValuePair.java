/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.match;

public class MaskValuePair {
    private ByteArray mask;
    private ByteArray value;

    public MaskValuePair(ByteArray mask, ByteArray value) {
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
        //TODO
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MaskValuePair that = (MaskValuePair) o;

        if (mask != null ? !mask.equals(that.mask) : that.mask != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = mask != null ? mask.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
