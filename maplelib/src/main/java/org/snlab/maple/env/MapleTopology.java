/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import java.util.Collections;
import java.util.Set;

public class MapleTopology {
    private Set<Node> nodes;
    private Set<Link> links;

    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    public Set<Link> getLinks() {
        return Collections.unmodifiableSet(links);
    }




    //-----------------------inner class-----------------------

    public static class Link {
        private Port p1;
        private Port p2;

        public Link(){

        }

        public Port getPort1() {
            return p1;
        }

        public Port getPort2() {
            return p2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Link link = (Link) o;
            if(p1.equals(link.p1)){
                return p2.equals(link.p2);
            } else if(p1.equals(link.p2)){
                return p2.equals(link.p2);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return p1.hashCode()+p2.hashCode();
        }
    }

    public static class Node {
        private String id; //openflow:1 openflow:233334443
        private Set<Port> ports;

        public Node(){

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

    public static class Port {
        private Node owner;
        private String id;//  openflow:1:1 2 3 4 internel
        private Port end;
        private Link link;

        public Port(){

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
}
