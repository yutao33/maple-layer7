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
import org.snlab.maple.packet.types.U32;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UtilTest {

    @Test
    public void test(){
        Random random = new Random();
        for(int i=0;i<10000;i++){
            int rand = random.nextInt();
            byte[] bytes = U32.ofRaw(rand).getBytes();
            //System.out.println(HexString.toHexString(bytes));
            int ret = U32.bytesToInt(bytes);
            //System.out.println(HexString.toHexString(U32.ofRaw(ret).getBytes()));
            //System.out.println(bytes[0]);
            Assert.assertEquals(rand,ret);
        }

        Map<String,String> test=new HashMap<>();
        test.put("2","1");
        test.put("1","2");
        Map<String,String> test1=new HashMap<>();
        test1.put("1","2");
        test1.put("2","1");
        System.out.println(test1.equals(test));
    }
}
