/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.field;

import java.util.HashSet;
import java.util.Set;

public class FieldWritable {
    public static MapleMatchField ETH_SRC=MapleMatchField.ETH_SRC;

    public static Set<MapleMatchField> writableFields=new HashSet<>();

    static{
        writableFields.add(ETH_SRC);
    }

    public static boolean isWriteAble(MapleMatchField field){
        return writableFields.contains(field);
    }

    private FieldWritable(){

    }

}
