/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.route;

import com.google.common.base.Preconditions;
import org.snlab.maple.env.MapleTopology.PortId;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.ByteArray;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ForwardAction {

    private ForwardAction() {
        throw new RuntimeException("shouldn't construct this instance");
    }

    private final static Drop finalDrop = new Drop();
    private final static PopVlan finalPopVlan = new PopVlan();
    private final static Punt finalPunt = new Punt();

    //-------------------static Action function-----------------------

    public static OutPut output(@Nonnull PortId port) {
        return new OutPut(port);
    }

    public static Drop drop() {
        return finalDrop;
    }

    public static Punt punt() {
        return finalPunt;
    }

    public static SetField setField(MapleMatchField field, ByteArray value, ByteArray mask) {
        Preconditions.checkArgument(field.getByteLength() == value.length());
        return new SetField(field, value, mask);
    }
    public static SetField setField(MapleMatchField field, ByteArray value) {
        Preconditions.checkArgument(field.getByteLength() == value.length());
        return new SetField(field, value, null);
    }

    public static PushVlan pushVlan(short vlanid) {
        Preconditions.checkArgument(vlanid >= 0 && vlanid <= 4095);
        return new PushVlan(vlanid);
    }

    public static PopVlan popVlan() {
        return finalPopVlan;
    }

    //-------------------static Action class------------------------

    public static abstract class Action {

        @Override
        public boolean equals(Object obj) {
            return this.getClass().isInstance(obj);
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    public static class OutPut extends Action {
        private PortId portid;

        public OutPut(@Nonnull PortId port) {
            this.portid = port;
        }

        public PortId getPortId() {
            return portid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OutPut outPut = (OutPut) o;

            return portid.equals(outPut.portid);
        }

        @Override
        public int hashCode() {
            return portid.hashCode();
        }

        @Override
        public String toString() {
            return "OutPut{" +
                    "portid=" + portid +
                    '}';
        }
    }


    public static class Punt extends Action {

    }

    public static class Drop extends Action {

    }

    public static class SetField extends Action {
        private final MapleMatchField field;
        private final ByteArray value;
        private final ByteArray mask;

        public SetField(MapleMatchField field,@Nonnull ByteArray value,@Nullable ByteArray mask) {
            this.field = field;
            this.value = value;
            Preconditions.checkArgument(field.getByteLength()==value.length());
            this.mask = mask;
            if(mask!=null){
                Preconditions.checkArgument(field.getByteLength()==mask.length());
            }
        }

        public MapleMatchField getField() {
            return field;
        }

        public ByteArray getValue() {
            return value;
        }

        public ByteArray getMask() {
            return mask;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            SetField setField = (SetField) o;

            if (field != setField.field) return false;
            return value.equals(setField.value);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + field.hashCode();
            result = 31 * result + value.hashCode();
            return result;
        }
    }

    public static class PushVlan extends Action {
        private short vlanId;

        public PushVlan(short vlanId) {
            this.vlanId = vlanId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            PushVlan pushVlan = (PushVlan) o;

            return vlanId == pushVlan.vlanId;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (int) vlanId;
            return result;
        }
    }

    public static class PopVlan extends Action {

    }

}
