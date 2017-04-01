/*
 * Copyright © 2017 SNLab and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.*;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
        List<Integer> test = new ArrayList<Integer>(Arrays.asList(1, 2, 3,2,2, 4, 5, 6, 7));
        List<Integer> unmodifiableList = Collections.unmodifiableList(test);

        Map<Integer,Integer> map=new HashMap<>();
        map.put(1,2);
        map.put(3,4);

        Map<Integer,Integer> map1=new HashMap<>();
        map.put(5,6);
        map.putAll(map1);
        System.out.println(map.toString());
    }
}
