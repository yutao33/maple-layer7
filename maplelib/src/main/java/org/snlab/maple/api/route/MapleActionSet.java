/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api.route;

import org.snlab.maple.api.network.MapleTopology;

public class MapleActionSet{

    public static class OutPut{

    }

    public static MapleActionSet.OutPut output(MapleTopology.Port port){
        return new MapleActionSet.OutPut();
    }

    public static class ToController{

    }

    public static MapleActionSet.ToController toController(){
        return new MapleActionSet.ToController();
    }

    public static class SetField{

    }

    public static MapleActionSet.SetField setField(){
        return new MapleActionSet.SetField();
    }

    public static class PushVlan{

    }

    public static MapleActionSet.PushVlan pushVlan(){
        return new MapleActionSet.PushVlan();
    }

    public static class PopVlan{

    }

    public static MapleActionSet.PopVlan popVlan(){
        return new MapleActionSet.PopVlan();
    }

}
