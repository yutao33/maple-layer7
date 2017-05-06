/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.route;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.snlab.maple.env.MapleTopology.PortId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Forward {  //TODO actions sequence and only for one node

    private final PortId inport;
    private final List<ForwardAction.Action> actions;
    private int bandwidthlimit;
    //private int timeout;

    
    public static final Forward DROP =
            new Forward(null, ForwardAction.drop());
    public static final Forward PUNT =
            new Forward(null, ForwardAction.punt());
    public static final List<Forward> DEFAULT_PuntForwards =
            Collections.singletonList(PUNT);



    public Forward(PortId inport, List<ForwardAction.Action> actions, int bandwidthlimit, int timeout) {
        this.inport = inport;
        this.actions = new ArrayList<>(actions);
        this.bandwidthlimit = bandwidthlimit;
        //this.timeout = timeout;
    }

    public Forward(PortId inport, ForwardAction.Action action, int bandwidthlimit, int timeout) {
        this(inport, Collections.singletonList(action), bandwidthlimit, timeout);
    }

    public Forward(PortId inport, ForwardAction.Action action) {
        this(inport, Collections.singletonList(action), 0, 0);
    }

    public Forward(@Nullable String inport, @Nonnull String output) {
        if (inport != null) {
            this.inport = new PortId(inport);
        } else {
            this.inport = null;
        }
        this.actions = new ArrayList<>();
        this.actions.add(ForwardAction.output(new PortId(output)));
        this.bandwidthlimit = 0;
    }

    public void concat(@Nonnull Forward n){
        Preconditions.checkArgument(Objects.equal(n.inport,this.inport));
        this.actions.addAll(n.actions);
        this.bandwidthlimit=n.bandwidthlimit;
    }

    @Nullable
    public PortId getInport() {
        return inport;
    }

    public List<ForwardAction.Action> getActions() {
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
        if (inport != null ? !inport.equals(forward.inport) : forward.inport != null) return false;
        return actions.equals(forward.actions);
    }

    @Override
    public int hashCode() {
        int result = inport != null ? inport.hashCode() : 0;
        result = 31 * result + actions.hashCode();
        result = 31 * result + bandwidthlimit;
        return result;
    }

    @Override
    public String toString() {
        return "Forward{" +
                "inport=" + inport +
                ", actions=" + actions +
                '}';
    }


    /**
     * static method
     * @param path
     * @return
     */
    public static String[] extractInPort(String... path) { //TODO
        Preconditions.checkArgument(path.length%2==0);
        List<String> l = new ArrayList<>(path.length/2);
        for(int i=0;i<path.length/2;i++){
            if(path[i*2]!=null){
                l.add(path[i*2]);
            }
        }
        String[] ret = new String[l.size()];
        l.toArray(ret);
        return ret;
    }
}



