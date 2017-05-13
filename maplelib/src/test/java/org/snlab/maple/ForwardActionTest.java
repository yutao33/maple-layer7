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
import org.snlab.maple.rule.route.ForwardAction;

public class ForwardActionTest {

    @Test
    public void test(){
        ForwardAction.Punt punt = new ForwardAction.Punt();
        ForwardAction.Punt punt1 = new ForwardAction.Punt();
        ForwardAction.PopVlan popVlan = new ForwardAction.PopVlan();
        Assert.assertEquals(punt,punt1);
        Assert.assertNotEquals(punt,popVlan);
        Assert.assertFalse(punt.equals(null));
    }
}
