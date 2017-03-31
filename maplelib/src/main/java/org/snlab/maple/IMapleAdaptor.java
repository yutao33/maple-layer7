/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.snlab.maple;


public interface IMapleAdaptor {
    void sendPacket();

    void installPath();

    void deletePath();

    void installRule();

    void deleteRule();

    void resetWriteTransaction();

    void submitTransaction();

    void outputtracetree();

}
