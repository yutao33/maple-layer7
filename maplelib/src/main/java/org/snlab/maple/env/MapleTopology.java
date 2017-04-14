/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


public class MapleTopology {
    public static final Logger LOG = Logger.getLogger("MapleTopology");

    private Map<Node, Node> nodes;
    private Map<Link, Link> links;

    public MapleTopology() {
        nodes = new HashMap<>();
        links = new HashMap<>();
    }

    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    public Set<Link> getLinks() {
        return Collections.unmodifiableSet(links.keySet());
    }

    /**
     * for MapleEnv to update topology
     *
     * @param putList
     * @param deleteList
     * @return ischanged
     */
    boolean update(List<MapleTopology.Element> putList,
                   List<MapleTopology.Element> deleteList) {
        boolean ischanged = false;
        List<Node> nodeList = new ArrayList<>();
        List<Port> portList = new ArrayList<>();
        List<Link> linkList = new ArrayList<>();
        if (deleteList != null) {
            for (Element ele : deleteList) {
                if (ele instanceof Node) {
                    nodeList.add((Node) ele);
                } else if (ele instanceof Port) {
                    portList.add((Port) ele);
                } else if (ele instanceof Link) {
                    linkList.add((Link) ele);
                } else {
                    throw new UnsupportedOperationException();
                }
            }
            for (Link link : linkList) {
                Link mylink = links.get(link);
                if (mylink != null) {
                    removeLink(mylink);
                    ischanged = true;
                }
            }
            for (Port port : portList) {
                Node mynode = nodes.get(port.getOwner());
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
            for (Node node : nodeList) {
                Node mynode = nodes.get(node);
                if (mynode != null) {
                    removeNode(mynode);
                    ischanged = true;
                }
            }
            nodeList.clear();
            portList.clear();
            linkList.clear();
        }
        if (putList != null) {
            for (Element ele : putList) {
                if (ele instanceof Node) {
                    nodeList.add((Node) ele);
                } else if (ele instanceof Port) {
                    portList.add((Port) ele);
                } else if (ele instanceof Link) {
                    linkList.add((Link) ele);
                } else {
                    throw new UnsupportedOperationException();
                }
            }
            for (Node node : nodeList) {
                Node mynode = nodes.get(node);
                if (mynode != null) {
                    if (!mynode.ports.equals(node.ports)) {
                        removeNode(mynode);
                        nodes.put(node, node);
                        ischanged = true;
                    }
                } else {
                    nodes.put(node, node);
                    ischanged = true;
                }
            }
            for (Port port : portList) {
                if (addPortifnotexisted(port)) {
                    ischanged = true;
                }
            }
            for (Link link : linkList) {
                Link mylink = links.get(link);
                if (mylink == null) {
                    Port start = link.getStart();
                    Node startowner = start.getOwner();
                    Node mystartowner = nodes.get(startowner);
                    if (mystartowner == null) {
                        nodes.put(startowner, startowner);
                        mystartowner = startowner;
                    }
                    Iterator<Port> iter = mystartowner.ports.iterator();
                    Port mystart = null;
                    while (iter.hasNext()) {
                        Port next = iter.next();
                        if (next.equals(start)) {
                            mystart = next;
                        }
                    }
                    if (mystart == null) {
                        mystartowner.ports.add(start);
                        mystart = start;
                    } else {
                        if (mystart.link != null) {
                            removeLink(mystart.link);
                        }
                    }

                    Port end = link.getEnd();
                    Node endowner = end.getOwner();
                    Node myendowner = nodes.get(endowner);
                    if (myendowner == null) {
                        nodes.put(endowner, endowner);
                        myendowner = endowner;
                    }
                    iter = myendowner.ports.iterator();
                    Port myend = null;
                    while (iter.hasNext()) {
                        Port next = iter.next();
                        if (next.equals(end)) {
                            myend = next;
                        }
                    }
                    if (myend == null) {
                        myendowner.ports.add(end);
                        myend = end;
                    } else {
                        if (myend.link != null) {
                            if (!myend.link.end.equals(mystart)) {
                                removeLink(myend.link);
                            }
                        }
                    }
                    mylink = new Link(mystart, myend);
                    mystart.link = mylink;
                    links.put(mylink, mylink);
                    ischanged = true;
                }
            }
        }
        return ischanged;
    }

    //return isadded
    private boolean addPortifnotexisted(Port port) {
        boolean isadded = false;
        Node owner = port.getOwner();
        Node mynode = nodes.get(owner);
        if (mynode != null) {
            isadded = mynode.ports.add(port);
        } else {
            nodes.put(owner, owner);
            isadded = true;
        }
        return isadded;
    }

    //remove a node in nodes
    private void removeNode(Node mynode) {
        for (Port myport : mynode.getPorts()) {
            removeLinkofaPort(myport);
        }
        nodes.remove(mynode);
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
        for (Node node : nodes.keySet()) {
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
        for (Link link : links.keySet()) {
            sb.append(link.getStart().getId());
            sb.append(" -> ");
            sb.append(link.getEnd().getId());
            sb.append("\n");
        }
        return sb.toString();
    }

    //-----------------------inner class-----------------------

    public static abstract class Element {

    }

    public static class Node extends Element {
        private final String id; //openflow:1 openflow:233334443
        private Set<Port> ports;

        public Node(String id, List<String> ports) {
            this.id = id;
            this.ports = new HashSet<>();
            if (ports != null) {
                for (String port : ports) {
                    this.ports.add(new Port(port));
                }
            }
        }

        public Set<Port> getPorts() {
            return Collections.unmodifiableSet(ports);  //TODO
        }

        public String getId() {
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
                    "id='" + id + '\'' +
                    ", ports=" + ports +
                    '}';
        }
    }

    public static class Port extends Element {
        private Node owner;// openflow:1
        private final String id;//  openflow:1:1 openflow:1:2 openflow:1:internal
        private Link link;

        public Port(String id) {
            assert id.matches("openflow:\\d+:\\w+");//TODO
            this.id = id;
        }

        public Node getOwner() {
            if (this.owner == null) {
                this.owner = new Node(id.substring(0, id.lastIndexOf(':')), Arrays.asList(id));
            }
            return owner;
        }

        public String getId() {
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

            return id != null ? id.equals(port.id) : port.id == null;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Port{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }

    public static class Link extends Element {
        private final Port start;
        private final Port end;

        public Link(String start, String end) {
            this.start = new Port(start);
            this.end = new Port(end);
        }

        public Link(Port start, Port end) {
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
