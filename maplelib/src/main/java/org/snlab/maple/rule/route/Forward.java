/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.route;

import org.snlab.maple.env.MapleTopology.Port;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Forward {
    private Port ingress;
    private List<? extends ForwardAction.Action> actions;
    private int bandwidthlimit;
    private int timeout;

    public Forward(Port ingress, List<? extends ForwardAction.Action> actions, int bandwidthlimit, int timeout) {
        this.ingress = ingress;
        this.actions = new ArrayList<>(actions);
        this.bandwidthlimit = bandwidthlimit;
        this.timeout = timeout;
    }

    public Forward(Port ingress, ForwardAction.Action action, int bandwidthlimit, int timeout){
        this(ingress, Collections.singletonList(action),bandwidthlimit,timeout);
    }

    public Forward(Port ingress, ForwardAction.Action action){
        this(ingress, Collections.singletonList(action),0,0);
    }

    public Forward(@Nullable String ingress,@Nonnull String output){
        if(ingress!=null){
            this.ingress=new Port(ingress);
        }
        this.actions=Collections.singletonList(ForwardAction.output(new Port(output)));
    }


    public static String[] extractIngress(String... path) {
        throw new UnsupportedOperationException();
    }

    public List<? extends ForwardAction.Action> getActions() {
        return actions;
    }

    public int getBandwidthlimit() {
        return bandwidthlimit;
    }
}



