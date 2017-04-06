/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule;


import java.util.Set;

public class MapleMatch{
    MapleMatchField field;
    Set<MapleMatchValue> values;
    MapleMatchValue mask;
}
