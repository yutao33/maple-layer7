/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import com.google.common.base.Preconditions;
import org.snlab.maple.rule.route.Forward;
import org.snlab.maple.rule.route.ForwardAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;


public class MapleTopology {

    private static final Logger LOG = Logger.getLogger(MapleTopology.class.getName());

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
    public Node findNode(@Nonnull NodeId nodeId) {
        return nodes.get(nodeId);
    }

    @Nullable
    public Port findPort(@Nonnull PortId portId) {
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

    public synchronized Forward[] shortestPath(PortId src, PortId dst) {
        if(src==null||dst==null){
            return new Forward[0];
        }

        Port srcPort = this.findPort(src);
        Port dstPort = this.findPort(dst);
        if(srcPort==null||dstPort==null){
            return new Forward[0];
        }

        Node srcNode = srcPort.getOwner();
        Node dstNode = dstPort.getOwner();
        if(srcNode.equals(dstNode)){
            return new Forward[]{
                    new Forward(src,ForwardAction.output(dst))
            };
        }

        Collection<Node> nodes = this.nodes.values();
        for (Node node : nodes) {
            node.flag = -1;
        }
        Map<NodeId,Link> path=new HashMap<>();
        Queue<Node> queue= new ArrayDeque<>();

        srcNode.flag=0;
        queue.add(srcNode);
        while(!queue.isEmpty()){
            Node a = queue.poll();
            for (Port port : a.getPorts()) {
                Link link = port.getLink();
                if(isBidirectional(link)){
                    Node endNode = link.getEnd().getOwner();
                    if(endNode.flag==-1){

                        if(endNode.equals(dstNode)){
                            List<Forward> list = new ArrayList<>();
                            list.add(new Forward(link.getEnd().getId(),ForwardAction.output(dst)));
                            Link ll=link;
                            while(true){
                                NodeId id1 = ll.getStart().getOwner().getId();
                                if(id1.equals(srcNode.getId())){
                                    list.add(new Forward(src,ForwardAction.output(ll.getStart().getId())));
                                    break;
                                }
                                Link ll2 = path.get(id1);
                                Preconditions.checkState(ll2!=null);
                                list.add(new Forward(ll2.getEnd().getId(),ForwardAction.output(ll.getStart().getId())));
                                ll=ll2;
                            }
                            Forward[] ret = new Forward[list.size()];
                            list.toArray(ret);
                            return ret;

                        } else {
                           endNode.flag=a.flag+1;
                           queue.add(endNode);
                           path.put(endNode.getId(),link);
                        }
                    }
                }
            }
        }

        return new Forward[0];
    }

    private boolean isBidirectional(Link link){
        return link!=null&&
                link.getEnd().getLink()!=null&&
                link.getEnd().getLink().getEnd().equals(link.getStart());
    }

    public synchronized Forward[] spanningTree() {
        List<Forward> list = new ArrayList<>();
        Collection<Node> nodes = this.nodes.values();
        for (Node node : nodes) {
            node.flag = 0;
        }
        for (Node node : nodes) {
            recurse(node, list);
        }
        Forward[] ret = new Forward[list.size()];
        list.toArray(ret);
        return ret;
    }

    private void recurse(Node node, List<Forward> list) {
        if (node.flag == 1) {
            return;
        }
        node.flag = 1;
        Set<Port> ports = node.getPorts();
        for (Port port : ports) {
            Link link = port.getLink();
            if (link == null || link.getEnd().getLink() == null) {
                list.add(new Forward(null, ForwardAction.output(port.getId())));
            } else {
                Port end = link.getEnd();
                Node endNode = end.getOwner();
                if (endNode.flag == 0) {
                    list.add(new Forward(null, ForwardAction.output(port.getId())));
                    list.add(new Forward(null, ForwardAction.output(end.getId())));
                    recurse(endNode, list);
                }
            }
        }
    }

    public synchronized Forward[] spanningTreeToHost(PortId dst) {
        if(dst==null){
            return new Forward[0];
        }
        Port dstport = findPort(dst);
        if(dstport==null){
            return new Forward[0];
        }
        for (Node node : this.nodes.values()) {
            node.flag = 0;
        }
        List<Forward> list = new ArrayList<>();
        recurse1(dstport,list);
        Forward[] ret = new Forward[list.size()];
        list.toArray(ret);
        return ret;
    }

    private void recurse1(Port dstport, List<Forward> list) {
        Node node = dstport.getOwner();
        if(node.flag==1){
            return;
        }
        list.add(new Forward(null,ForwardAction.output(dstport.getId())));
        node.flag=1;
        Set<Port> ports = node.getPorts();
        for (Port port : ports) {
            if(!port.equals(dstport)){
                Link link = port.getLink();
                if(isBidirectional(link)){
                    Port end = link.getEnd();
                    recurse1(end,list);
                }
            }
        }
    }

    public synchronized String[] getBorderPorts() {
        List<String> list = new ArrayList<>();
        for (Node node : nodes.values()) {
            for (Port port : node.getPorts()) {
                if (port.getLink() == null) {
                    list.add(port.getId().toString());
                }
            }
        }
        String[] ret = new String[list.size()];
        list.toArray(ret);
        return ret;
    }

    /**
     * for MapleEnv to updateAndreturnTrack topology.
     *
     * @param putList    add elements.
     * @param deleteList delete elements.
     * @return return true if topology is changed.
     */
    synchronized boolean update(List<MapleTopology.Element> putList, List<MapleTopology.Element> deleteList) {
        boolean ischanged = false;
        if (deleteList != null && updateDeleteList(deleteList)) {
            ischanged = true;
        }
        if (putList != null && updatePutList(putList)) {
            ischanged = true;
        }
        verify();
        return ischanged;
    }

    private boolean updateDeleteList(@Nonnull List<MapleTopology.Element> deleteList) {
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

    private boolean updatePutList(@Nonnull List<MapleTopology.Element> putList) {
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
            port.owner = mynode;
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

    public void verify() {
        for (Node node : nodes.values()) {
            for (Port port : node.ports) {
                assert port.getId().equals(node.id);
                if (port.link != null) {
                    assert links.get(port.link) == port.link;
                    assert port.link.start.getOwner() == node;
                    assert node.ports.contains(port.link.start);
                    assert port.link.end != null;
                    assert nodes.get(port.link.end.id.getNodeId()) == port.link.end.getOwner();
                }
            }
        }
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
        private int flag;

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
