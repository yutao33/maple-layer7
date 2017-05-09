/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.env;

import java.util.Collection;

public class TopologyAlgorithm<T> {
    private int flag;
    private T value;

    public TopologyAlgorithm(T value, int flag){
        this.value = value;
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public static <T1> TopologyAlgorithm<T1>[] constructArray(Collection<T1> collection, int flag){
        TopologyAlgorithm<T1>[] ret = new TopologyAlgorithm[collection.size()];
        int i = 0;
        for (T1 t1 : collection) {
            ret[i]=new TopologyAlgorithm<>(t1,flag);
            ++i;
        }
        return ret;
    }
}
