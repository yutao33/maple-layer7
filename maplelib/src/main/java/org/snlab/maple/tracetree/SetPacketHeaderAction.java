/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.maple.tracetree;

public class SetPacketHeaderAction extends Action {

    Match.Field field;

    String value;

    public SetPacketHeaderAction(Match.Field field, String value) {
        this.field = field;
        this.value = value;
    }

    public Match.Field getField() {
        return field;
    }

    public void setField(Match.Field field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
