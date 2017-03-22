/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;

import java.util.ArrayList;
import java.util.List;

public class MultiPath extends RouteAction {

    List<Path> paths = new ArrayList<Path>();

    public void addPath(Path path) {
        paths.add(path);
    }

    public List<Path> getPaths() {
        return paths;
    }

    @Override
    public String toString() {
        String result = "";
        for (Path path : paths) {
            result += "-" + path.toString();
        }
        return result.substring(1);
    }
}
