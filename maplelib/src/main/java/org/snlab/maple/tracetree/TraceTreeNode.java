/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.tracetree;

/**
 * abstract static class TraceTreeNode.
 */
public abstract class TraceTreeNode {
    protected int priority;

    public boolean isConsistentWith(Trace.TraceItem item){
        throw new UnsupportedOperationException();
    }
}
