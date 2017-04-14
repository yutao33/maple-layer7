/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.api;

import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.rule.route.Forward;

public interface IMaplePacket {

    MaplePacket.PktFieldMaskable ethSrc();

    MaplePacket.PktFieldMaskable ethDst();

    MaplePacket.PktField ethType();

    MaplePacket.PktFieldMaskable ipSrc();

    MaplePacket.PktFieldMaskable ipDst();

    MaplePacket.Ingress ingress();

    void setRoute(String... path);

    void addRoute(String... path);

    void setRoute(Forward... path);

    void addRoute(Forward... path);
}
