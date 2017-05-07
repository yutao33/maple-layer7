/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MapleTopology {

    private Map<NodeId, Node> nodes;
    private Map<Link, Link> links;

    public MapleTopology() {
        nodes = new HashMap<>();
        links = new HashMap<>();
    }

    public static boolean isValidNodeId(String id) {
        return id.matches("^openflow:\\d+$");
    }

    public static boolean isValidPortId(String id) {
        return id.matches("^openflow:\\d+:\\w+$");
    }

    public static String truncateNodeId(String portid) {
        return portid.substring(0, portid.lastIndexOf(':'));
    }

    public Collection<Node> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    public Collection<Link> getLinks() {
        return Collections.unmodifiableCollection(links.values());
    }

    @Nullable
    public Node getNode(@Nonnull NodeId nodeId) {
        return nodes.get(nodeId);
    }

    @Nullable
    public Port getPort(@Nonnull PortId portId) {
        Node node = nodes.get(portId.getNodeId());
        if (node != null) {
            for (Port port : node.getPorts()) {
                if (port.getId().equals(portId)) {
                    return port;
                }
            }
        }
        return null;
    }

    /**
     * for MapleEnv to update topology.
     *
     * @param putList    add elements.
     * @param deleteList delete elements.
     * @return return true if topology is changed.
     */
    boolean update(List<MapleTopology.Element> putList, List<MapleTopology.Element> deleteList) {
        boolean ischanged = false;
        if (updateDeleteList(deleteList)) {
            ischanged = true;
        }
        if (updatePutList(putList)) {
            ischanged = true;
        }
        return ischanged;
    }

    private boolean updateDeleteList(List<MapleTopology.Element> deleteList) {
        boolean ischanged = false;
        for (Element ele : deleteList) {
            if (ele instanceof Link) {
                Link link = (Link) ele;
                Link mylink = links.get(link);
                if (mylink != null) {
                    removeLink(mylink);
                    ischanged = true;
                }
            }
        }
        for (Element ele : deleteList) {
            if (ele instanceof Port) {
                Port port = (Port) ele;
                NodeId nodeid = port.getId().getNodeId();
                Node mynode = nodes.get(nodeid);
                Iterator<Port> iter = mynode.ports.iterator();
                while (iter.hasNext()) {
                    Port next = iter.next();
                    if (next.equals(port)) {
                        removeLinkofaPort(next);
                        iter.remove();
                        ischanged = true;
                        break;
                    }
                }
            }
        }
        for (Element ele : deleteList) {
            if (ele instanceof Node) {
                Node node = (Node) ele;
                Node mynode = nodes.get(node.getId());
                if (mynode != null) {
                    removeNode(mynode);
                    ischanged = true;
                }
            }
        }
        return ischanged;
    }

    private boolean updatePutList(List<MapleTopology.Element> putList) {
        boolean ischanged = false;

        for (Element ele : putList) {
            if (ele instanceof Node) {
                Node node = (Node) ele;
                Node mynode = nodes.get(node.getId());
                if (mynode != null) {
                    if (!mynode.ports.equals(node.ports)) {
                        removeNode(mynode);
                        nodes.put(node.getId(), node);
                        ischanged = true;
                    }
                } else {
                    nodes.put(node.getId(), node);
                    ischanged = true;
                }
            }
        }
        for (Element ele : putList) {
            if (ele instanceof Port) {
                Port port = (Port) ele;
                if (addPortifnotexisted(port)) {
                    ischanged = true;
                }
            }
        }
        for (Element ele : putList) {
            if (ele instanceof Link) {
                Link link = (Link) ele;
                if (links.get(link) == null) {
                    putLink(link);
                    ischanged = true;
                }
            }
        }

        return ischanged;
    }

    private void putLink(Link link) {
        Port startport = link.getStart();
        NodeId startnodeid = startport.getId().getNodeId();
        Node mystartowner = nodes.get(startnodeid);
        if (mystartowner == null) {
            mystartowner = new Node(startnodeid, Collections.singletonList(startport.getId()));
            nodes.put(startnodeid, mystartowner);
        }
        Iterator<Port> iter = mystartowner.ports.iterator();
        Port mystartport = null;
        while (iter.hasNext()) {
            Port next = iter.next();
            if (next.equals(startport)) {
                mystartport = next;
            }
        }
        if (mystartport == null) {
            startport.owner = mystartowner;
            mystartowner.ports.add(startport);
            mystartport = startport;
        } else {
            if (mystartport.link != null) {
                removeLink(mystartport.link);
            }
        }

        Port endport = link.getEnd();
        NodeId endownerid = endport.getId().getNodeId();
        Node myendowner = nodes.get(endownerid);
        if (myendowner == null) {
            myendowner = new Node(endownerid, Collections.singletonList(endport.getId()));
            nodes.put(endownerid, myendowner);
        }
        iter = myendowner.ports.iterator();
        Port myendport = null;
        while (iter.hasNext()) {
            Port next = iter.next();
            if (next.equals(endport)) {
                myendport = next;
            }
        }
        if (myendport == null) {
            endport.owner = myendowner;
            myendowner.ports.add(endport);
            myendport = endport;
        } else {
            if (myendport.link != null) {
                if (!myendport.link.end.equals(mystartport)) {
                    removeLink(myendport.link);
                }
            }
        }
        Link mylink = new Link(mystartport, myendport);
        mystartport.link = mylink;
        links.put(mylink, mylink);
    }

    //return isadded
    private boolean addPortifnotexisted(Port port) {
        boolean isadded = false;
        NodeId nodeid = port.getId().getNodeId();
        Node mynode = nodes.get(nodeid);
        if (mynode != null) {
            isadded = mynode.ports.add(port);
        } else {
            nodes.put(nodeid, new Node(nodeid, Collections.singletonList(port.getId())));
            isadded = true;
        }
        return isadded;
    }

    //remove a node in nodes
    private void removeNode(Node mynode) {
        for (Port myport : mynode.getPorts()) {
            removeLinkofaPort(myport);
        }
        nodes.remove(mynode.getId());
    }

    //remove related link of one port
    private void removeLinkofaPort(Port myport) {
        if (myport.link != null) {
            removeLink(myport.link.end.link);
            removeLink(myport.link);
        }
    }

    //remove a link from links and set the start to null
    private void removeLink(Link mylink) {
        if (mylink != null) {
            links.remove(mylink);
            mylink.start.link = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MapleTopology:\nNodes:\n");
        for (Node node : nodes.values()) {
            sb.append(node.getId());
            sb.append(" : ");
            for (Port port : node.getPorts()) {
                sb.append(port.getId());
                sb.append(" -> ");
                if (port.getLink() != null) {
                    sb.append(port.getLink().getEnd().getId());
                } else {
                    sb.append("null");
                }
                sb.append(" ; ");
            }
            sb.append("\n");
        }
        sb.append("Links:\n");
        for (Link link : links.values()) {
            sb.append(link.getStart().getId());
            sb.append(" -> ");
            sb.append(link.getEnd().getId());
            sb.append("\n");
        }
        return sb.toString();
    }

    //-----------------------static inner class-----------------------

    @Immutable
    public static class NodeId {
        private final String id; //openflow:1 openflow:233334443

        public NodeId(@Nonnull String id) {
            Preconditions.checkArgument(isValidNodeId(id));
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NodeId nodeId = (NodeId) o;

            return id.equals(nodeId.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return id;
        }
    }

    @Immutable
    public static class PortId {
        private final String id;//  openflow:1:1 openflow:1:2 openflow:1:local
        private NodeId nodeId;

        public PortId(@Nonnull String id) {
            Preconditions.checkArgument(isValidPortId(id));
            this.id = id;
        }

        @Nonnull
        public NodeId getNodeId() {
            if (this.nodeId == null) {
                this.nodeId = new NodeId(truncateNodeId(id));
            }
            return nodeId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PortId portId = (PortId) o;

            return id.equals(portId.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return id;
        }
    }


    public static abstract class Element {

    }

    public static class Node extends Element {
        private final NodeId id;
        private Set<Port> ports;

        public Node(@Nonnull NodeId nodeid, List<PortId> ports) {
            this.id = nodeid;
            this.ports = new HashSet<>();
            if (ports != null) {
                for (PortId portid : ports) {
                    Preconditions.checkArgument(portid.getNodeId().equals(id));
                    this.ports.add(new Port(portid, this));
                }
            }
        }

        public Set<Port> getPorts() {
            return Collections.unmodifiableSet(ports);
        }

        public NodeId getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            return id.equals(node.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return "Node{" +
                    "id=" + id +
                    '}';
        }
    }

    public static class Port extends Element {
        private final PortId id;
        private Node owner;
        private Link link;

        public Port(@Nonnull PortId id, Node owner) {
            this.owner = owner;
            this.id = id;
        }

        public Node getOwner() {
            return owner;
        }

        public PortId getId() {
            return id;
        }

        public Link getLink() {
            return link;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Port port = (Port) o;

            return id.equals(port.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return "Port{" +
                    "id=" + id +
                    '}';
        }
    }

    public static class Link extends Element {
        private final Port start;
        private final Port end;

        public Link(@Nonnull Port start, @Nonnull Port end) {
            this.start = start;
            this.end = end;
        }

        public Port getStart() {
            return start;
        }

        public Port getEnd() {
            return end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Link link = (Link) o;

            if (!start.equals(link.start)) return false;
            return end.equals(link.end);
        }

        @Override
        public int hashCode() {
            int result = start.hashCode();
            result = 31 * result + end.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Link{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

}
