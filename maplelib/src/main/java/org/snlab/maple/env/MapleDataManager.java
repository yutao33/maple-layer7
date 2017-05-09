/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import org.snlab.maple.api.IMapleDataBroker;
import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.packet.types.IPv4Address;
import org.snlab.maple.packet.types.MacAddress;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class MapleDataManager {

    private static final Logger LOG = Logger.getLogger(MapleDataManager.class.toString());

    private final IReExecHandler handler;

    private final MapleTopology topology;

    private final TrackedMap<MacAddress,MapleTopology.Port> macHostTable;

    private final TrackedMap<IPv4Address,MapleTopology.Port> ipv4HostTable;

    private final MapleDataBroker[] dbs ;

    public MapleDataManager(int dbsize, IReExecHandler handler) {
        dbs = new MapleDataBroker[dbsize];
        for(int i=0;i<dbsize;i++){
            dbs[i]=new MapleDataBroker();
        }

        this.handler = handler;
        topology = new MapleTopology();
        macHostTable = new TrackedMap<>(handler);
        ipv4HostTable = new TrackedMap<>(handler);
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


    public MapleDataBroker allocBroker(){
        if(dbs!=null){
            for (MapleDataBroker db : dbs) {
                if(!db.isused.getAndSet(true)){
                    return db;
                }
            }
        }
        return new MapleDataBroker(); //just in case
    }

    public void freeBroker(MapleDataBroker db){
        db.isused.set(false);
    }

    public class MapleDataBroker implements IMapleDataBroker{

        private AtomicBoolean isused=new AtomicBoolean(false);

        private MaplePacket pkt;

        private MapleDataBroker(){

        }

        @Override
        public MapleTopology getTopology() {
            return topology;
        }

        public TrackedMap<MacAddress, MapleTopology.Port> getMacHostTable(){
            return new TrackedMap<>(pkt,macHostTable);
        }

        public TrackedMap<IPv4Address, MapleTopology.Port> getIPv4HostTable(){
            return new TrackedMap<>(pkt,ipv4HostTable);
        }

        @Override
        public Object readData(String url) {
            return null;
        }
    }

}
