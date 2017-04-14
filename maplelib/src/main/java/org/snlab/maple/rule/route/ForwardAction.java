/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.route;

import org.snlab.maple.env.MapleTopology.Port;
import org.snlab.maple.packet.parser.Ethernet;
import org.snlab.maple.rule.field.MapleMatchField;

import javax.annotation.Nonnull;

public final class ForwardAction {

    private ForwardAction() {
        throw new RuntimeException("shouldn't construct this instance");
    }


    //-------------------static Action function-----------------------

    public static OutPut output(@Nonnull Port port){
        return new OutPut(port);
    }

    public static Drop drop(){
        return new Drop();
    }

    //-------------------static Action class------------------------

    public static abstract class Action {

    }

    public static class OutPut extends Action {
        private Port port;

        public OutPut(@Nonnull Port port) {
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OutPut outPut = (OutPut) o;

            return port.equals(outPut.port);
        }

        @Override
        public int hashCode() {
            return port.hashCode();
        }
    }

    public interface PuntPktListener {
        void onPunt(Ethernet originPkt);
    }

    public static class Punt extends Action {
        private PuntPktListener listener;

        public Punt(PuntPktListener listener) {
            this.listener = listener;
        }

    }

    public static class Drop extends Action{

    }

    public static class SetField extends Action {
        private MapleMatchField field;

        public SetField(MapleMatchField field) {
            this.field = field;
        }
    }

    public static class PushVlan extends Action {

    }

    public static class PopVlan extends Action {

    }

}
