/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.route;

import org.snlab.maple.env.MapleTopology.Port;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.packet.parser.Ethernet;

public class ForwardAction {

    private ForwardAction() {
        throw new RuntimeException("shouldn't construct this instance");
    }

    public static abstract class Action {

    }

    public static class OutPut extends Action {
        private Port port;

        public OutPut(Port port) {
            this.port = port;
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
