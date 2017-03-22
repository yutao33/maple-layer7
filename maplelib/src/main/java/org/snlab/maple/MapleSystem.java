/*
 * Copyright © 2017 SNLab and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple;


import org.snlab.maple.tracetree.TraceTree;

public class MapleSystem {


    private final MapleAdaptor mapleAdaptor;
    private TraceTree traceTree = new TraceTree();
    private MapleHandler handler;

    public MapleSystem(MapleAdaptor mapleAdaptor) {
        this.mapleAdaptor = mapleAdaptor;
    }

    public MapleHandler getHandler() {
        if( handler==null ){
            handler=new MapleSystemHandlerImpl();
        }
        return handler;
    }

    private void updateTrace() {

    }

    private void updateFlowRules() {

    }

    private class MapleSystemHandlerImpl implements MapleHandler {

        public void onPacket() {

        }
    }
}

