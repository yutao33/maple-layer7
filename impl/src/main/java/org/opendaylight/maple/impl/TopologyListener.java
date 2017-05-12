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
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.node.TerminationPoint;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.IMapleHandler;
import org.snlab.maple.env.MapleTopology;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class TopologyListener implements DataTreeChangeListener<NetworkTopology> {

    private final static Logger LOG = LoggerFactory.getLogger(TopologyListener.class);

    private List<MapleTopology.Element> putList = new ArrayList<>();
    private List<MapleTopology.Element> deleteList = new ArrayList<>();

    private final IMapleHandler mapleHandler;

    public TopologyListener(IMapleHandler mapleHandler) {
        this.mapleHandler = mapleHandler;
    }

    @Override
    public synchronized void onDataTreeChanged(@Nonnull Collection<DataTreeModification<NetworkTopology>> changes) {

        putList.clear();
        deleteList.clear();

        for (DataTreeModification<NetworkTopology> change : changes) {
            DataObjectModification<NetworkTopology> rootNode = change.getRootNode();
            DataObjectModification.ModificationType type = rootNode.getModificationType();

            if (type.equals(DataObjectModification.ModificationType.SUBTREE_MODIFIED)) {
                TopologyKey topologyKey = new TopologyKey(new TopologyId("flow:1"));
                DataObjectModification<Topology> topo = rootNode.getModifiedChildListItem(Topology.class, topologyKey);

                if (topo != null) {

                    DataObjectModification.ModificationType type1 = topo.getModificationType();
                    switch (type1) {
                        case WRITE:
                            putTopology(topo.getDataAfter());
                            break;
                        case SUBTREE_MODIFIED:
                            modifyTopology(topo);
                            break;
                        case DELETE:
                            deleteTopology(topo.getDataBefore());
                            break;
                    }

                } else {
                    LOG.error("Topology changed but not flow:1");
                }
            }
        }
        if(putList.size()+deleteList.size()>0) {
            mapleHandler.onTopologyChanged(putList, deleteList);
        }
    }

    private void modifyTopology(DataObjectModification<Topology> topo) {

        Collection<DataObjectModification<? extends DataObject>> topochildren = topo.getModifiedChildren();
        for (DataObjectModification<? extends DataObject> mc : topochildren) {
            Class<? extends DataObject> mctype = mc.getIdentifier().getType();
            if (mctype.equals(Node.class)) {
                DataObjectModification<Node> nodemod = (DataObjectModification<Node>) mc;
                switch (nodemod.getModificationType()) {
                    case WRITE:
                        putNode(nodemod.getDataAfter());
                        break;
                    case SUBTREE_MODIFIED:
                        modifyNode(nodemod);
                        break;
                    case DELETE:
                        deleteNode(nodemod.getDataBefore());
                        break;
                }
            } else if (mctype.equals(Link.class)) {
                DataObjectModification<Link> linkmod = (DataObjectModification<Link>) mc;
                switch (linkmod.getModificationType()) {
                    case WRITE:
                    case SUBTREE_MODIFIED:
                        Link dataAfter = linkmod.getDataAfter();
                        Link dataBefore = linkmod.getDataBefore();
                        if(Objects.equals(dataAfter,dataBefore)){
                            LOG.info("dataAfter == dataBefore");
                        } else {
                            putLink(dataAfter);
                        }
                        break;
                    case DELETE:
                        deleteLink(linkmod.getDataBefore());
                        break;
                }
            } else {
                LOG.warn("unknown ModificationType: " + mctype);
            }
        }
    }

    private void modifyNode(DataObjectModification<Node> nodemod) {
        Collection<DataObjectModification<? extends DataObject>> mc = nodemod.getModifiedChildren();
        for (DataObjectModification<? extends DataObject> mcc : mc) {
            Class<? extends DataObject> mctype = mcc.getIdentifier().getType();
            if (mctype.equals(TerminationPoint.class)) {
                DataObjectModification<TerminationPoint> tpmod = (DataObjectModification<TerminationPoint>) mcc;
                switch (tpmod.getModificationType()) {
                    case WRITE:
                    case SUBTREE_MODIFIED:
                        putPort(tpmod.getDataAfter());
                        break;
                    case DELETE:
                        deletePort(tpmod.getDataBefore());
                        break;
                }
            } else {
                LOG.warn("unknown ModificationType1:" + mctype);
            }
        }
    }

    private void putTopology(Topology topo) {
        List<Node> nodes = topo.getNode();
        for (Node n : nodes) {
            putNode(n);
        }
        List<Link> links = topo.getLink();
        for (Link l : links) {
            putLink(l);
        }
    }

    private void putNode(Node node) {
        MapleTopology.NodeId nodeId = new MapleTopology.NodeId(node.getNodeId().getValue());
        List<MapleTopology.PortId> ports = new ArrayList<>();
        List<TerminationPoint> terminationPoint = node.getTerminationPoint();
        if(terminationPoint!=null) {
            for (TerminationPoint point : terminationPoint) {
                MapleTopology.PortId portId = new MapleTopology.PortId(point.getTpId().getValue());
                ports.add(portId);
            }
        }
        putList.add(new MapleTopology.Node(nodeId, ports));
    }

    private void putLink(Link link) {
        String src = link.getSource().getSourceTp().getValue();
        String dst = link.getDestination().getDestTp().getValue();
        MapleTopology.Port srcport = new MapleTopology.Port(new MapleTopology.PortId(src), null);
        MapleTopology.Port dstport = new MapleTopology.Port(new MapleTopology.PortId(dst), null);
        putList.add(new MapleTopology.Link(srcport, dstport));
    }

    private void deleteTopology(Topology topo) {
        List<Node> nodes = topo.getNode();
        for (Node n : nodes) {
            deleteNode(n);
        }
        List<Link> links = topo.getLink();
        for (Link l : links) {
            deleteLink(l);
        }
    }

    private void deleteNode(Node node) {
        String value = node.getNodeId().getValue();
        MapleTopology.NodeId nodeid = new MapleTopology.NodeId(value);
        deleteList.add(new MapleTopology.Node(nodeid, null));
    }

    private void deleteLink(Link link) {
        String src = link.getSource().getSourceTp().getValue();
        String dst = link.getDestination().getDestTp().getValue();
        MapleTopology.Port srcport = new MapleTopology.Port(new MapleTopology.PortId(src), null);
        MapleTopology.Port dstport = new MapleTopology.Port(new MapleTopology.PortId(dst), null);
        deleteList.add(new MapleTopology.Link(srcport, dstport));
    }

    private void putPort(TerminationPoint tp) {
        String value = tp.getTpId().getValue();
        MapleTopology.PortId portId = new MapleTopology.PortId(value);
        putList.add(new MapleTopology.Port(portId, null));
    }

    private void deletePort(TerminationPoint tp) {
        String value = tp.getTpId().getValue();
        MapleTopology.PortId portId = new MapleTopology.PortId(value);
        deleteList.add(new MapleTopology.Port(portId, null));
    }

}

