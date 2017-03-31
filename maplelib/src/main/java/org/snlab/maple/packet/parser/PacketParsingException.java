/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.packet.parser;

public class PacketParsingException extends Exception {

    private static final long serialVersionUID = -1177841297678875573L;

    public PacketParsingException(String msg) {
        super(msg);
    }

}
