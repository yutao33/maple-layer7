/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.flow.http.rev170512.HttpFlowMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.flow.http.rev170512.baseflow.flow.metadata.HttpMetadata;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.flow.rev170512.Baseflow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.flow.rev170512.baseflow.FlowMetadata;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.IMapleHandler;
import org.snlab.maple.flow.IPFiveTuple;
import org.snlab.maple.flow.flowinfo.FlowType;
import org.snlab.maple.flow.flowinfo.HTTPFlowInfo;
import org.snlab.maple.packet.types.IPv4Address;

import javax.annotation.Nonnull;
import java.util.Collection;

public class BaseflowListener implements DataTreeChangeListener<Baseflow> {

    private final static Logger LOG = LoggerFactory.getLogger(BaseflowListener.class);

    private final IMapleHandler mapleHandler;

    public BaseflowListener(IMapleHandler mapleHandler){
        this.mapleHandler = mapleHandler;
    }

    @Override
    public void onDataTreeChanged(@Nonnull Collection<DataTreeModification<Baseflow>> changes) {
        for (DataTreeModification<Baseflow> change : changes) {
            DataObjectModification<Baseflow> rootNode = change.getRootNode();
            DataObjectModification.ModificationType mctype = rootNode.getModificationType();
            if(mctype.equals(DataObjectModification.ModificationType.SUBTREE_MODIFIED)) {
                Collection<DataObjectModification<? extends DataObject>> mc = rootNode.getModifiedChildren();
                for (DataObjectModification<? extends DataObject> mcc : mc) {
                    DataObjectModification<FlowMetadata> mcc1 = (DataObjectModification<FlowMetadata>) mcc;
                    FlowMetadata dataAfter = mcc1.getDataAfter();
                    if(dataAfter!=null){
                        String flowType = dataAfter.getFlowType();
                        switch(flowType){
                            case "HTTP":
                                //HttpFlowMetadata http = dataAfter.getAugmentation(HttpFlowMetadata.class);
                                updateHTTP(dataAfter);
                                break;
                            case "SSH":
                                break;
                            default:
                                throw new UnsupportedOperationException();
                        }
                    }
                }
            }
        }
    }

    private void updateHTTP(FlowMetadata data) {
        HttpFlowMetadata http = data.getAugmentation(HttpFlowMetadata.class);
        HttpMetadata httpMetadata = http.getHttpMetadata();
        byte ipproto = data.getIpProto().byteValue();
        int srcip = IPv4Address.of(data.getSrcIp().getValue()).getInt();
        int dstip = IPv4Address.of(data.getDstIp().getValue()).getInt();
        short sport = data.getSrcPort().getValue().shortValue();
        short dport = data.getDstPort().getValue().shortValue();
        IPFiveTuple key = new IPFiveTuple(ipproto, srcip, dstip, sport, dport);
        HTTPFlowInfo flowInfo = new HTTPFlowInfo(httpMetadata.getMethod(), httpMetadata.getRequestURL());
        mapleHandler.onFlowChanged(key,flowInfo);
    }
}
