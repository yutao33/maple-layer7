/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import java.util.ArrayList;
import java.util.List;

public class TopologyAlgorithm {

    private List<Vertex> vertices=new ArrayList<>();
    //private List<Arc> arcs=new ArrayList<>();



    private class Arc{
        Vertex end;
    }

    private class Vertex{
        MapleTopology.Node node;
        int flag;
        List<Arc> arcs;
    }
}
