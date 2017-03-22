/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.maple.tracetree;

public class RouteAction extends Action {
    private static Punt punt = new Punt();
    private static Drop drop = new Drop();
    private static Flood flood = new Flood();

    public static RouteAction Punt() {
        return punt;
    }

    public static RouteAction Drop() {
        return drop;
    }

    public static RouteAction Flood() {
        return flood;
    }
}
