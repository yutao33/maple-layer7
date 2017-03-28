/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api.network;

import java.util.List;

public class MapleTopology {
    private List<Node> nodes;
    private List<MapleTopology>links;

    public List<Node> getNodes() {
        return nodes;
    }

    public List<MapleTopology> getLinks() {
        return links;
    }

    public static class Link {
        private Port p1;
        private Port p2;

        public Port getP1() {
            return p1;
        }

        public void setP1(Port p1) {
            this.p1 = p1;
        }

        public Port getP2() {
            return p2;
        }

        public void setP2(Port p2) {
            this.p2 = p2;
        }
    }

    public static class Node {
        private String id;
        private List<Port> ports;

        public List<Port> getPorts() {
            return ports;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class Port {
        private Node owner;
        private String id;//  1 2 3 4 internel
        private Port end;
        private Link link;

        public Node getOwner() {
            return owner;
        }

        public void setOwner(Node owner) {
            this.owner = owner;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Port getEnd() {
            return end;
        }

        public void setEnd(Port end) {
            this.end = end;
        }

        public Link getLink() {
            return link;
        }

        public void setLink(Link link) {
            this.link = link;
        }
    }
}
