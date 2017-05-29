/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.flow.rev170512.Baseflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;

public class BaseflowListener implements DataTreeChangeListener<Baseflow> {

    private final static Logger LOG = LoggerFactory.getLogger(BaseflowListener.class);

    public BaseflowListener(){

    }

    @Override
    public void onDataTreeChanged(@Nonnull Collection<DataTreeModification<Baseflow>> changes) {
        LOG.info(changes.toString());
    }
}
