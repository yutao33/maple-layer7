/*
 * Copyright © 2017 SNLab and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple;


import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.app.ArpHandler;
import org.snlab.maple.app.ArpHandler2;
import org.snlab.maple.app.IPv4Switch;
import org.snlab.maple.app.L2Switch;
import org.snlab.maple.app.L7Test;
import org.snlab.maple.env.IReExecHandler;
import org.snlab.maple.env.MapleDataManager;
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.packet.MaplePacketType;
import org.snlab.maple.packet.OutPutPacket;
import org.snlab.maple.packet.types.EthType;
import org.snlab.maple.rule.MaplePacketInReason;
import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.tracetree.TraceTree;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MapleSystem{

    private final static Logger LOG = Logger.getLogger(MapleSystem.class.toString());

    private final static int THREADPOOLSIZE = 1;//Runtime.getRuntime().availableProcessors();

    private final IMapleAdaptor mapleAdaptor;
    private final TraceTree traceTree;
    private IMapleHandler handler;
    private List<MapleAppBase> mapleAppList;
    //private BlockingQueue<Runnable> pktBlockingQueue;
    private ThreadPoolExecutor pktThreadPool;
    private MapleDataManager dataManager;


    public MapleSystem(IMapleAdaptor mapleAdaptor) {
        this.mapleAdaptor = mapleAdaptor;
        this.traceTree = new TraceTree();
        this.mapleAppList = new ArrayList<>();
        BlockingQueue<Runnable> pktBlockingQueue = new LinkedBlockingQueue<>();
        this.pktThreadPool = new ThreadPoolExecutor(THREADPOOLSIZE, THREADPOOLSIZE, 1, TimeUnit.MINUTES, pktBlockingQueue);

        this.dataManager = new MapleDataManager(THREADPOOLSIZE,new IReExecHandler(){
            @Override
            public void onReExec(MaplePacket pkt) {
                LOG.info("reexec");
                MaplePacket pkt1 = new MaplePacket(pkt);
                pkt1.setType(MaplePacketType.REEXEC);
                addPktRunnable(pkt1);
            }
        });

        //test
        //this.mapleAppList.add(new ArpHandler());
        //this.mapleAppList.add(new L2Switch());
        //this.mapleAppList.add(new SetFieldTest());
        this.mapleAppList.add(new ArpHandler2());
        //this.mapleAppList.add(new IPv4Switch());
        this.mapleAppList.add(new L7Test());
    }

    public IMapleHandler getHandler() {
        if (handler == null) {
            handler = new MapleSystemHandlerImpl();
        }
        return handler;
    }

    volatile int pktcount=0;

    private void onPacket(MaplePacket pkt) {
        synchronized (traceTree) {  //TODO fix it
            pkt.getTraceList().clear();
            MapleDataManager.MapleDataBroker db = dataManager.allocBroker(pkt);

            for (MapleAppBase app : mapleAppList) {
                if (app.onPacket(pkt, db)) {
                    break;
                }
            }

            dataManager.freeBroker(db);

            List<MapleRule> rules = null;

            traceTree.update(pkt.getTraceList(), pkt);

            if(!pkt.getType().equals(MaplePacketType.REEXEC)) {
                MapleTopology topo = dataManager.allocBroker(null).getTopology();//FIXME
                Object[] objs = traceTree.derivePackets(topo, pkt);
                List<OutPutPacket> outPutPackets = (List<OutPutPacket>) objs[0];
                List<MaplePacket> genpkts = (List<MaplePacket>) objs[1];
                for (MaplePacket genpkt : genpkts) {
                    addPktRunnable(genpkt);
                }
                mapleAdaptor.sendPacket(outPutPackets);
                LOG.info("sendpacket="+outPutPackets);
            }
            int k=this.pktThreadPool.getQueue().size();
            if(k<=1) {
                rules = traceTree.generateRules();
                LOG.warning("queuesize="+k+" rules="+rules.size());
                if (rules.size() > 0) {
                    mapleAdaptor.updateRules(rules);
                    LOG.warning("updateRules="+rules);
//                    for (MapleRule rule : rules) {
//                        rule.setStatus(MapleRule.Status.INSTALLED);
//                    }
                    mapleAdaptor.outPutTraceTree(traceTree, pkt);
                }
            }

            LOG.info("pktcount="+(++pktcount)+" packet=" + pkt);
        }
    }

    private synchronized void addPktRunnable(final MaplePacket pkt){
        pktThreadPool.execute(new Runnable() {
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
                    MapleDataManager.MapleDataBroker db = dataManager.allocBroker(null);
                    if(app.init(db)){
                        mapleAppList.add(0, app);
                    };
                    dataManager.freeBroker(db);
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
                MapleDataManager.MapleDataBroker db = dataManager.allocBroker(null);
                Object ret = app.onCommand(parm, db);  // 'return' is safe
                dataManager.freeBroker(db);
                return ret;
            }
        }
        return null;
    }

    public enum MapleAppSetup {
        INSTALL,
        UNINSTALL
    }

    private class MapleSystemHandlerImpl implements IMapleHandler {

        private int pktcount =0;
        private int lldpcount = 0;

        private final List<MapleTopology.Element> cachePutList =  new ArrayList<>();
        private volatile boolean cacheThread = false;
        private volatile long cacheThreadEndTime = 0;

        @Override
        public void onPacket(String inportId, byte[] payload, MaplePacketInReason reason) {
            MaplePacket pkt = new MaplePacket(payload, new MapleTopology.PortId(inportId));
            int queueSize = pktThreadPool.getQueue().size();
            String str="getpkt queueSize="+ queueSize +" count="+ pktcount++;
            LOG.info(str);
            if(pkt._getFrame().getEtherType().equals(EthType.LLDP)){
                lldpcount++;
                LOG.info("lldpcount="+lldpcount);
                return;
            }
            if(queueSize>100*THREADPOOLSIZE){
                LOG.severe("got too much packets");
                return ;
            }
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
            if(deleteList.size()>0){
                MapleSystem.this.dataManager.updateTopology(Collections.EMPTY_LIST, deleteList);
            }
            if(putList.size()>0) {
                synchronized (cachePutList) {
                    cachePutList.addAll(putList);
                    cacheThreadEndTime = Calendar.getInstance().getTimeInMillis()+1000;
                }
                if(!cacheThread){
                    cacheThread = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(true) {
                                long deta = cacheThreadEndTime - Calendar.getInstance().getTimeInMillis();
                                if (deta > 0) {
                                    try {
                                        Thread.sleep(deta);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    break;
                                }
                            }
                            synchronized (cachePutList){
                                MapleSystem.this.dataManager.updateTopology(cachePutList,Collections.EMPTY_LIST);
                                cachePutList.clear();
                                cacheThread = false;
                            }
                        }
                    }).start();
                }
            }
            LOG.info("putList="+putList.toString()+" deleteList="+deleteList.toString()+" pktthreadpool size"+pktThreadPool.getQueue().size());
        }
    }
}

