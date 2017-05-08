/*
 * Copyright © 2017 SNLab and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple;


import org.snlab.maple.api.IMapleDataBroker;
import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.app.InPortTest;
import org.snlab.maple.env.MapleDataManager;
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.packet.types.EthType;
import org.snlab.maple.rule.MaplePacketInReason;
import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.tracetree.TraceTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MapleSystem {

    private final static Logger LOG = Logger.getLogger(MapleSystem.class.toString());

    private final static int THREADPOOLSIZE = Runtime.getRuntime().availableProcessors();

    private final IMapleAdaptor mapleAdaptor;
    private TraceTree traceTree;
    private IMapleHandler handler;
    private List<MapleAppBase> mapleAppList;
    private BlockingQueue<Runnable> pktBlockingQueue;
    private ThreadPoolExecutor pktThreadPool;
    private MapleDataManager dataManager;


    public MapleSystem(IMapleAdaptor mapleAdaptor) {
        this.mapleAdaptor = mapleAdaptor;
        this.traceTree = new TraceTree();
        this.dataManager = new MapleDataManager(THREADPOOLSIZE);
        this.mapleAppList = new ArrayList<>();
        this.pktBlockingQueue = new LinkedBlockingQueue<>();
        this.pktThreadPool = new ThreadPoolExecutor(THREADPOOLSIZE, THREADPOOLSIZE, 1, TimeUnit.MINUTES, pktBlockingQueue);
        this.mapleAppList.add(new InPortTest());
    }

    public IMapleHandler getHandler() {
        if (handler == null) {
            handler = new MapleSystemHandlerImpl();
        }
        return handler;
    }

    private void onPacket(MaplePacket pkt) {
        if (pkt._getFrame().getEtherType().equals(EthType.LLDP)) {
            LOG.info("get LLDP");
            return;
        }

        pkt.getTraceList().clear();
        IMapleDataBroker db = dataManager.allocBroker();

        for (MapleAppBase app : mapleAppList) {
            if (app.onPacket(pkt,db)) {
                break;
            }
        }

        traceTree.update(pkt.getTraceList(), pkt);

        List<MapleRule> rules = traceTree.generateRules();

        mapleAdaptor.updateRules(rules);

        LOG.info("packet=" + pkt + "\nrules=\n" + rules);
    }

    private void addPktRunnable(final MaplePacket pkt){
        pktBlockingQueue.add(new Runnable() {
            @Override
            public void run() {
                MapleSystem.this.onPacket(pkt);
            }
        });
    }


    private boolean setupMapleApp(Class<? extends MapleAppBase> appclass, MapleAppSetup opt) {

        switch (opt) {
            case INSTALL:
                try {
                    MapleAppBase app = appclass.newInstance();
                    app.init(dataManager.allocBroker());
                    mapleAppList.add(0, app);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            case UNINSTALL:
                Iterator<MapleAppBase> iter = mapleAppList.iterator();
                while (iter.hasNext()) {
                    MapleAppBase app = iter.next();
                    if (app.getClass().equals(appclass)) {
                        iter.remove();
                        break;//  'break' is safe
                    }
                }
                break;
        }
        return true;
    }

    private Object command(Class<? extends MapleAppBase> appclass, Object parm) {
        for (MapleAppBase app : mapleAppList) {
            if (app.getClass().equals(appclass)) {
                return app.onCommand(parm, dataManager.allocBroker());  // 'return' is safe
            }
        }
        return null;
    }

    public enum MapleAppSetup {
        INSTALL,
        UNINSTALL
    }

    private class MapleSystemHandlerImpl implements IMapleHandler {

        @Override
        public void onPacket(String inportId, byte[] payload, MaplePacketInReason reason) {
            MaplePacket pkt = new MaplePacket(payload, new MapleTopology.PortId(inportId));
            MapleSystem.this.addPktRunnable(pkt);
        }


        /**
         * Topology change.
         * PUT DELETE
         * Node Port Link
         */
        @Override
        public void onTopologyChanged(List<MapleTopology.Element> putList,
                                      List<MapleTopology.Element> deleteList) {
            MapleSystem.this.dataManager.updateTopology(putList, deleteList);
        }
    }
}

