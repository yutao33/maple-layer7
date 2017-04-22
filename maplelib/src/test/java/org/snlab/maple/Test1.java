/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.ByteArray;
import org.snlab.maple.rule.match.ValueMaskPair;
import org.snlab.maple.tracetree.TraceTree;

import java.util.Iterator;

public class Test1 {

    @Rule
    public ExpectedException thrown=ExpectedException.none();

    @Test
    public void test1(){
        ByteArray avalue = new ByteArray(new byte[]{(byte)0x10});
        ByteArray amask = new ByteArray(new byte[]{(byte)0x11});
        ValueMaskPair a = new ValueMaskPair(avalue, amask);

        ByteArray bvalue = new ByteArray(new byte[]{(byte)0x02});
        ByteArray bmask = new ByteArray(new byte[]{(byte)0x03});
        ValueMaskPair b = new ValueMaskPair(bvalue, bmask);

        System.out.println(ValueMaskPair.getSubSet(a,b));
    }

}
