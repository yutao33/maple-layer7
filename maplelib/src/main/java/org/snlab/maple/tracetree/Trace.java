/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;


import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.ByteArray;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class Trace {

    public static abstract class TraceItem {
        protected MapleMatchField field;
        protected ByteArray mask;

        public MapleMatchField getField() {
            return field;
        }

        public ByteArray getMask() {
            return mask;
        }
    }

    static abstract class TestItem extends TraceItem {
        protected boolean result;

        public boolean getresult() {
            return result;
        }
    }

    public static class TraceGet extends TraceItem {
        private ByteArray value;

        public TraceGet(MapleMatchField field, byte[] mask, @Nonnull byte[] value) {
            super.field = field;
            if (mask != null) {
                super.mask = new ByteArray(mask);
            }
            this.value = new ByteArray(value);
        }

        public ByteArray getValue() {
            return value;
        }
    }

    public static class TraceIs extends TestItem {
        private ByteArray value;

        public TraceIs(MapleMatchField field, byte[] mask, @Nonnull byte[] value, boolean ret) {
            super.field = field;
            if (mask != null) {
                super.mask = new ByteArray(mask);
            }
            this.value = new ByteArray(value);
            this.result = ret;
        }

        public ByteArray getValue() {
            return value;
        }
    }

    public static class TraceIn extends TestItem {
        private Set<ByteArray> values;

        public TraceIn(MapleMatchField field, byte[] mask, @Nonnull List<byte[]> values, boolean ret) {
            super.field = field;
            if (mask != null) {
                super.mask = new ByteArray(mask);
            }
            for (byte[] value : values) {
                this.values.add(new ByteArray(value));
            }
            this.result = ret;
        }

        public Set<ByteArray> getValues() {
            return values;
        }
    }

    public static class TraceRange extends TestItem {
        private ByteArray value1;
        private ByteArray value2;

        public TraceRange(MapleMatchField field, byte[] mask, byte[] value1, byte[] value2, boolean ret) {
            super.field = field;
            if (mask != null) {
                super.mask = new ByteArray(mask);
            }
            this.value1 = new ByteArray(value1);
            this.value2 = new ByteArray(value2);
            this.result = ret;
        }

        public ByteArray getValue1() {
            return value1;
        }

        public ByteArray getValue2() {
            return value2;
        }
    }

}
