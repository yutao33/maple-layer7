/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.route;

import org.snlab.maple.env.MapleTopology.Port;

import java.util.ArrayList;
import java.util.Arrays;
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
        this(ingress, Arrays.asList(action),bandwidthlimit,timeout);
    }

    public Forward(Port ingress, ForwardAction.Action action){
        this(ingress, Arrays.asList(action),0,0);
    }


    public static String[] extractIngress(String... path) {
        return new String[]{};
    }

}



