/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.snlab.maple.MapleAdaptor;

public class ODLMapleSystemAdaptor implements MapleAdaptor{

    private final DataBroker dataBroker;

    public ODLMapleSystemAdaptor(DataBroker dataBroker){
        this.dataBroker = dataBroker;
    }

    @Override
    public void sendPacket() {

    }

    @Override
    public void installPath() {

    }

    @Override
    public void deletePath() {

    }

    @Override
    public void installRule() {

    }

    @Override
    public void deleteRule() {

    }

    @Override
    public void resetWriteTransaction() {

    }

    @Override
    public void submitTransaction() {

    }

    @Override
    public void outputtracetree() {

    }
}
