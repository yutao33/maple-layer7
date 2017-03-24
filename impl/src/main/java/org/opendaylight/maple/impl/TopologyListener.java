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
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class TopologyListener implements DataTreeChangeListener<NetworkTopology> {

    private final static Logger LOG = LoggerFactory.getLogger(TopologyListener.class);

    @Override
    public void onDataTreeChanged(@Nonnull Collection<DataTreeModification<NetworkTopology>> changes) {

        int i=0;
        for (DataTreeModification<NetworkTopology> change : changes) {
            DataObjectModification<NetworkTopology> rootNode = change.getRootNode();
            DataObjectModification.ModificationType type = rootNode.getModificationType();

            System.out.println(i+":"+type);


            if(type.equals(DataObjectModification.ModificationType.SUBTREE_MODIFIED)){
                TopologyKey topologyKey = new TopologyKey(new TopologyId("flow:1"));
                DataObjectModification<Topology> topo = rootNode.getModifiedChildListItem(Topology.class, topologyKey);

                if(topo!=null){
                    DataObjectModification.ModificationType type1 = topo.getModificationType();
                    System.out.println("type1:"+type1);
                    switch(type1){
                        case WRITE:
                            break;
                        case SUBTREE_MODIFIED:

                            Collection<DataObjectModification<? extends DataObject>> topochildren = topo.getModifiedChildren();
                            for (DataObjectModification<? extends DataObject> mc : topochildren) {
                                Class<? extends DataObject> mctype = mc.getIdentifier().getType();
                                if(mctype.equals(Node.class)){
                                    System.out.println("Node: type=" +mc.getModificationType());
                                } else if(mctype.equals(Link.class)){
                                    System.out.println("Link: type="+mc.getModificationType());
                                }
                            }

                            break;
                        case DELETE:
                            break;
                    }
                } else {
                    LOG.error("Topology changed but not flow:1");
                }
            }
            LOG.warn(rootNode.getDataAfter().toString());
        }
        System.out.println(new Date().toString()+"\n");

    }

    private void nodeModified(DataObjectModification<Node> node) {
        DataObjectModification.ModificationType type = node.getModificationType();
        System.out.println("node modified type:"+type);
        switch(type){
            case DELETE:
                break;
            case SUBTREE_MODIFIED:
                Collection<DataObjectModification<? extends DataObject>> modifiednodes = node.getModifiedChildren();
                for (DataObjectModification<? extends DataObject> mn : modifiednodes) {

                }
                break;
            case WRITE:
                break;
        }
    }

}
