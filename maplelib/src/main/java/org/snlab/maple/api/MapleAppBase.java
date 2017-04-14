/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api;


public abstract class MapleAppBase {

    public boolean init(IMapleEnv env) {
        return true;
    }

    public Object onCommand(Object parm, IMapleEnv env) {
        return null;
    }

    public boolean onPacket(IMaplePacket pkt, IMapleEnv env) {
        return false;
    }


}
