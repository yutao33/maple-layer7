/*
 * Copyright © 2017 SNLab and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple;


import org.snlab.maple.api.MapleAppBase;
import org.snlab.maple.api.MaplePacket;
import org.snlab.maple.tracetree.TraceTree;

import java.util.Iterator;
import java.util.List;

public class MapleSystem {


    private final MapleAdaptor mapleAdaptor;
    private TraceTree traceTree = new TraceTree();
    private MapleHandler handler;
    private List<MapleAppBase> mapleAppList;

    public MapleSystem(MapleAdaptor mapleAdaptor) {
        this.mapleAdaptor = mapleAdaptor;
    }

    public MapleHandler getHandler() {
        if (handler == null) {
            handler = new MapleSystemHandlerImpl();
        }
        return handler;
    }

    private void updateTrace() {

    }

    private void updateFlowRules() {

    }


    private void onPacket(MaplePacket pkt) {

        for (MapleAppBase app : mapleAppList) {
            if (app.onPacket(pkt)) {
                break;
            }
        }

        traceTree.update(pkt.getTraceList());

        //TODO: flow rules
    }

    private boolean setupMapleApp(Class<? extends MapleAppBase> appclass,MapleAppSetup opt) {

        switch(opt){
            case INSTALL:
                try {
                    MapleAppBase app = appclass.newInstance();
                    mapleAppList.add(0,app);
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
                    if(app.getClass().equals(appclass)){
                        iter.remove();
                        break;//  'break' is safe
                    }
                }
                break;
        }
        return true;
    }

    private Object command(Class<? extends MapleAppBase> appclass, Object parm){
        for (MapleAppBase app : mapleAppList) {
            if(app.getClass().equals(appclass)){
                return app.oncommand(parm);  // 'return' is safe
            }
        }
        return null;
    }

    public enum MapleAppSetup {
        INSTALL,
        UNINSTALL
    }

    private class MapleSystemHandlerImpl implements MapleHandler {

        public void onPacket() {

        }
    }
}

