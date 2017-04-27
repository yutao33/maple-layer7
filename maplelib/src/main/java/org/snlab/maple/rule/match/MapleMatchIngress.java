/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.rule.match;

import org.snlab.maple.rule.field.MapleMatchField;

public class MapleMatchIngress extends MapleMatch{

    public MapleMatchIngress(MapleMatchField field, ValueMaskPair match) {
        super(field, match);
    }


}
