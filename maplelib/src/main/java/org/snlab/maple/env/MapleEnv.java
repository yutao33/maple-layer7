/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import org.snlab.maple.api.IMapleEnv;

public class MapleEnv implements IMapleEnv {

    private MapleTopology topology=new MapleTopology();

    @Override
    public MapleTopology getTopo() {
        return topology;
    }

    @Override
    public boolean isReExec() {
        return false;
    }
}
