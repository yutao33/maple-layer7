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
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Trace {

    private Trace() {
        throw new UnsupportedOperationException();
    }

    public static abstract class TraceItem {
        protected MapleMatchField field;
        protected ByteArray mask;

        public MapleMatchField getField() {
            return field;
        }

        @Nullable
        public ByteArray getMask() {
            return mask;
        }
    }

    public static abstract class TestItem extends TraceItem {
        protected boolean result;
        protected ByteArray value;

        public boolean getresult() {
            return result;
        }

        @Nullable
        public ByteArray getValue(){
            return value;
        }
    }

    public static class TraceGet extends TraceItem {
        private ByteArray value;

        public TraceGet(MapleMatchField field,@Nullable byte[] mask, @Nonnull byte[] value) {
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

        public TraceIs(MapleMatchField field,@Nullable byte[] mask, @Nonnull byte[] value, boolean ret) {
            this.field = field;
            if (mask != null) {
                this.mask = new ByteArray(mask);
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
        private ByteArray pktvalue;

        public TraceIn(MapleMatchField field,@Nullable byte[] mask, @Nonnull List<byte[]> values,@Nullable byte[] pktvalue, boolean ret) {
            this.field = field;
            if (mask != null) {
                this.mask = new ByteArray(mask);
            }
            this.values = new HashSet<>();
            for (byte[] value : values) {
                this.values.add(new ByteArray(value));
            }
            if(pktvalue!=null){
                this.pktvalue=new ByteArray(pktvalue);
            }
            this.result = ret;
        }

        public Set<ByteArray> getValues() {
            return values;
        }

        public ByteArray getPktvalue() {
            return pktvalue;
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
            if (value1 != null) {
                this.value1 = new ByteArray(value1);
            }
            if (value2 != null) {
                this.value2 = new ByteArray(value2);
            }
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
