/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.app;

import org.snlab.maple.api.IMapleDataBroker;
import org.snlab.maple.api.IMaplePacket;
import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.rule.match.ByteArray;
import org.snlab.maple.rule.route.Forward;
import org.snlab.maple.rule.route.ForwardAction;

import static org.snlab.maple.rule.field.FieldWritable.ETH_DST;
import static org.snlab.maple.rule.field.FieldWritable.ETH_SRC;


/**
 * topology single1.sh
 */
public class SetFieldTest extends MapleAppBase {

    @Override
    public boolean onPacket(IMaplePacket pkt, IMapleDataBroker db) {
        if(pkt.inport().is("openflow:1:1")){
            ForwardAction.SetField setField = ForwardAction.setField(ETH_SRC, new ByteArray(new byte[]{0, 0, 0, 0, 0, 5}));
            ForwardAction.OutPut outPut = ForwardAction.output(new MapleTopology.PortId("openflow:1:2"));
            pkt.setRoute(new Forward(null, setField),new Forward(null,outPut));
        } else if(pkt.inport().is("openflow:1:2")){
            ForwardAction.SetField setField = ForwardAction.setField(ETH_DST, new ByteArray(new byte[]{0, 0, 0, 0, 0, 1}));
            ForwardAction.OutPut outPut = ForwardAction.output(new MapleTopology.PortId("openflow:1:1"));
            pkt.setRoute(new Forward(null, setField),new Forward(null,outPut));
        }
        return true;
    }
}
