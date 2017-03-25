/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api;

public interface TraceMaplePacket {

    long ethSrc();

    long ethDst();

    int ethType();

    boolean ethSrcIs(long exp);

    boolean ethDstIs(long exp);

    boolean ethTypeIs(int exp);

    /**
     * path can be an endpoint-endpoint path or a tree without loop
     * @param path
     */
    void setRoute(String[] path);
}
