/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple;

import org.junit.Assert;
import org.junit.Test;
import org.snlab.maple.env.MapleTopology;

public class MapleTopologyTest {

    @Test
    public void test(){
        Assert.assertFalse(MapleTopology.isValidNodeId("openflow:www"));
        Assert.assertFalse(MapleTopology.isValidPortId("openflow:www:333"));
        Assert.assertTrue(MapleTopology.isValidNodeId("openflow:1"));
        Assert.assertTrue(MapleTopology.isValidPortId("openflow:1:1"));
        Assert.assertEquals("openflow:11",MapleTopology.truncateNodeId("openflow:11:33"));
    }
}
