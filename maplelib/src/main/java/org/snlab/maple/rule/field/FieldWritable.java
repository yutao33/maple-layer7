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
    public static final MapleMatchField ETH_SRC = MapleMatchField.ETH_SRC;
    public static final MapleMatchField ETH_DST = MapleMatchField.ETH_DST;
    public static final MapleMatchField IPv4_SRC = MapleMatchField.IPv4_SRC;
    public static final MapleMatchField IPv4_DST = MapleMatchField.IPv4_DST;

    public static Set<MapleMatchField> writableFields = new HashSet<>();

    static {
        writableFields.add(ETH_SRC);
        writableFields.add(ETH_DST);
        writableFields.add(IPv4_SRC);
        writableFields.add(IPv4_DST);
    }

    public static boolean isWriteAble(MapleMatchField field) {
        return writableFields.contains(field);
    }

    private FieldWritable() {
        throw new UnsupportedOperationException();
    }

}
