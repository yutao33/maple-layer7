/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import java.util.*;

public class MapleTopology {
    private Map<String,Node> nodes;
    private Map<String,Link> links;

    public MapleTopology() {
        nodes=new HashMap<>();
        links=new HashMap<>();
    }

    public Set<Node> getNodes() {
        //return Collections.unmodifiableSet(nodes.values());
        throw new UnsupportedOperationException();
    }

    public Set<Link> getLinks() {
        //return Collections.unmodifiableSet(links);
        throw new UnsupportedOperationException();
    }

    /**
     * for MapleEnv to update topology
     * @param putList
     * @param deleteList
     */
    void update(List<MapleTopology.Element> putList,
                               List<MapleTopology.Element> deleteList){
        if(!putList.isEmpty()){
            for (Element ele : putList) {
                if(ele instanceof Node){
                    Node node = (Node) ele;

                } else if(ele instanceof Port) {
                    Port port = (Port) ele;
                } else if(ele instanceof Link){
                    Link link = (Link) ele;
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
        if(!deleteList.isEmpty()){
            for (Element ele : putList) {
                if(ele instanceof Node){
                    Node node = (Node) ele;
                } else if(ele instanceof Port) {
                    Port port = (Port) ele;
                } else if(ele instanceof Link){
                    Link link = (Link) ele;
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder("MapleTopology:\nNodes:\n");
        for (Node node : nodes) {
            sb.append(node.getId());
            sb.append(" : ");
            for (Port port : node.getPorts()) {
                sb.append(port.getId());
                sb.append(" -> ");
                if(port.getEnd()!=null){
                    sb.append(port.getEnd().getId());
                } else {
                    sb.append("null");
                }
                sb.append(" ; ");
            }
        }
        sb.append("Links:\n");
        for (Link link : links) {
            sb.append(link.getStart().getId());
            sb.append(" -> ");
            sb.append(link.getEnd().getId());
            sb.append("\n");
        }
        return sb.toString();
    }

    //-----------------------inner class-----------------------

    public static abstract class Element{

    }

    public static class Node extends Element{
        private String id; //openflow:1 openflow:233334443
        private Set<Port> ports;

        public Node(String id, List<String> ports){

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
    }

    public static class Port extends Element{
        private Node owner;// openflow:1
        private String id;//  openflow:1:1 openflow:1:2 openflow:1:internal
        private Port end;
        private Link link;

        public Port(String id){

        }

        public Node getOwner() {
            return owner;
        }

        public String getId() {
            return id;
        }

        public Port getEnd() {
            return end;
        }

        public Link getLink() {
            return link;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Port port = (Port) o;

            if (!owner.equals(port.owner)) return false;
            return id.equals(port.id);
        }

        @Override
        public int hashCode() {
            int result = owner.hashCode();
            result = 31 * result + id.hashCode();
            return result;
        }
    }

    public static class Link extends Element{
        private Port start;
        private Port end;

        public Link(String start,String end){

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
    }

}
