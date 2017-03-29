/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api.route;

import org.snlab.maple.api.network.MapleTopology;

public class MapleAction {

    public static class OutPut{

    }

    public static MapleAction.OutPut output(MapleTopology.Port port){
        return new MapleAction.OutPut();
    }

    public static class ToController{

    }

    public static MapleAction.ToController toController(){
        return new MapleAction.ToController();
    }

    public static class SetField{

    }

    public static MapleAction.SetField setField(){
        return new MapleAction.SetField();
    }

    public static class PushVlan{

    }

    public static MapleAction.PushVlan pushVlan(){
        return new MapleAction.PushVlan();
    }

    public static class PopVlan{

    }

    public static MapleAction.PopVlan popVlan(){
        return new MapleAction.PopVlan();
    }

}
