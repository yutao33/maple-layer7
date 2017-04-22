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
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Immutable
public class Forward {

    private final Port ingress;
    private final List<? extends ForwardAction.Action> actions;
    private final int bandwidthlimit;
    //private int timeout;

    public static final List<? extends ForwardAction.Action> DEFAULT_PuntActions =
            Collections.singletonList(new ForwardAction.Punt());
    public static final List<Forward> DEFAULT_PuntForwards =
            Collections.singletonList(new Forward(null, DEFAULT_PuntActions, 0, 0));
    public static final Forward DROP =
            new Forward(null, ForwardAction.drop());


    public Forward(Port ingress, List<? extends ForwardAction.Action> actions, int bandwidthlimit, int timeout) {
        this.ingress = ingress;
        this.actions = new ArrayList<>(actions);
        this.bandwidthlimit = bandwidthlimit;
        //this.timeout = timeout;
    }

    public Forward(Port ingress, ForwardAction.Action action, int bandwidthlimit, int timeout) {
        this(ingress, Collections.singletonList(action), bandwidthlimit, timeout);
    }

    public Forward(Port ingress, ForwardAction.Action action) {
        this(ingress, Collections.singletonList(action), 0, 0);
    }

    public Forward(@Nullable String ingress, @Nonnull String output) {
        if (ingress != null) {
            this.ingress = new Port(ingress);
        } else {
            this.ingress = null;
        }
        this.actions = Collections.singletonList(ForwardAction.output(new Port(output)));
        this.bandwidthlimit = 0;
    }

    @Nullable
    public Port getIngress() {
        return ingress;
    }

    public List<? extends ForwardAction.Action> getActions() {
        return actions;
    }

    public int getBandwidthLimit() {
        return bandwidthlimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Forward forward = (Forward) o;

        if (bandwidthlimit != forward.bandwidthlimit) return false;
        if (ingress != null ? !ingress.equals(forward.ingress) : forward.ingress != null) return false;
        return actions.equals(forward.actions);
    }

    @Override
    public int hashCode() {
        int result = ingress != null ? ingress.hashCode() : 0;
        result = 31 * result + actions.hashCode();
        result = 31 * result + bandwidthlimit;
        return result;
    }

    @Override
    public String toString() {
        return "Forward{" +
                "ingress=" + ingress +
                ", actions=" + actions +
                '}';
    }


    /**
     * static method
     * @param path
     * @return
     */
    public static String[] extractIngress(String... path) {
        throw new UnsupportedOperationException();
    }
}



