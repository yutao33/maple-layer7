/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import org.snlab.maple.api.IMapleEnv;

import java.util.List;
import java.util.logging.Logger;

public class MapleEnv implements IMapleEnv {

    private static final Logger LOG = Logger.getLogger(MapleEnv.class.toString());

    private MapleTopology topology = new MapleTopology();

    public void updateTopology(List<MapleTopology.Element> putList,
                               List<MapleTopology.Element> deleteList) {
//        boolean ret = topology.update(putList, deleteList);
//        String info = "updateTopology:\nputList="
//                + putList.toString()
//                + "\ndeleteList=" + deleteList.toString()
//                + "\nreturn=" + ret;
//        if (ret) {
//            info += "\nTopology=\n" + topology.toString();
//        }
//        LOG.info(info + "\n");
    }


    //------------------env function---------------------

    @Override
    public MapleTopology getTopo() {
        return topology;
    }

    @Override
    public boolean isReExec() {
        return false;
    }
}
