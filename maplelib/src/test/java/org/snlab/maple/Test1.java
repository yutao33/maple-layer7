/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.snlab.maple.rule.match.ByteArray;
import org.snlab.maple.rule.match.ValueMaskPair;

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

        for(int i=0;i<0xff;i++) {
            for(int j=0;j<0xff;j++) {
                ByteArray byteArray = new ByteArray(new byte[]{(byte)i, (byte)j});
                short value = byteArray.toShort();
                Assert.assertEquals(i*0x100+j,value&0xffff);
            }
        }
    }

}
