/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api;

import com.sun.istack.internal.Nullable;

public abstract class MapleAppBase {


    public @Nullable Object oncommand(Object parm){
        return null;
    }

    public boolean onPacket(MaplePacket pkt) {
        return false;
    }


}
