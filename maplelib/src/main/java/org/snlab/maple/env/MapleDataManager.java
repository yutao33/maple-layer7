/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import com.google.common.base.Preconditions;
import org.snlab.maple.api.IMapleDataBroker;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class MapleDataManager {

    private static final Logger LOG = Logger.getLogger(MapleDataManager.class.toString());

    private MapleTopology topology = new MapleTopology();

    private MapleDataBroker[] dbs ;

    public MapleDataManager(int dbsize) {
        dbs = new MapleDataBroker[dbsize];
    }

    public void updateTopology(List<MapleTopology.Element> putList,
                               List<MapleTopology.Element> deleteList) {
        boolean ret = topology.update(putList, deleteList);
        String info = "updateTopology:\nputList="
                + putList.toString()
                + "\ndeleteList=" + deleteList.toString()
                + "\nreturn=" + ret;
        if (ret) {
            info += "\nTopology=\n" + topology.toString();
        }
        LOG.info(info + "\n");
    }


    public IMapleDataBroker allocBroker(){
        if(dbs!=null){
            for (MapleDataBroker db : dbs) {
                if(!db.isused.getAndSet(true)){
                    return db;
                }
            }
        }
        return new MapleDataBroker();
    }

    public void freeBroker(IMapleDataBroker db){
        Preconditions.checkArgument(db instanceof MapleDataBroker);
        MapleDataBroker db1 = (MapleDataBroker) db;
        db1.isused.set(false);
    }

    private class MapleDataBroker implements IMapleDataBroker{

        private AtomicBoolean isused=new AtomicBoolean(false);

        @Override
        public MapleTopology getTopo() {
            return topology;
        }

        @Override
        public Object readData(String url) {
            return null;
        }
    }

}
