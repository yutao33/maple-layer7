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

    private final TrackSet topoTrackSet = new TrackSet();

    private final TrackedMap<MacAddress,MapleTopology.PortId> macHostTable;

    private final TrackedMap<IPv4Address,MapleTopology.PortId> ipv4HostTable;

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

    public boolean updateTopology(List<MapleTopology.Element> putList,
                               List<MapleTopology.Element> deleteList) {
        boolean ret = topology.update(putList, deleteList);
        String info = "updateTopology:\nputList="
                + putList.toString()
                + "\ndeleteList=" + deleteList.toString()
                + "\nreturn=" + ret;
        if (true) {
            info += "\nTopology=\n" + topology.toString();
        }
        LOG.info(info + "\n");
        if(ret){
            topoTrackSet.reexec(handler);
        }
        return ret;
    }


    public MapleDataBroker allocBroker(MaplePacket pkt){
        if(dbs!=null){
            for (MapleDataBroker db : dbs) {
                if(!db.isused.getAndSet(true)){
                    db.pkt=pkt;
                    return db;
                }
            }
        }
        MapleDataBroker db = new MapleDataBroker();
        db.pkt=pkt;
        return db;
    }

    public void freeBroker(MapleDataBroker db){
        db.isused.set(false);
    }

    public class MapleDataBroker implements IMapleDataBroker{

        private AtomicBoolean isused=new AtomicBoolean(false);

        private MaplePacket pkt;

        @Override
        public MapleTopology getTopology() {
            topoTrackSet.track(pkt);
            return topology;
        }

        @Override
        public TrackedMap<MacAddress, MapleTopology.PortId> getMacHostTable(){
            return new TrackedMap<>(pkt,macHostTable);
        }

        @Override
        public TrackedMap<IPv4Address, MapleTopology.PortId> getIPv4HostTable(){
            return new TrackedMap<>(pkt,ipv4HostTable);
        }

        @Override
        public Object readData(String url) {
            return null;
        }
    }

}
