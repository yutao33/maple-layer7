/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.packet.parser;
//package net.floodlightcontroller.parser;

/**
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
public interface IPacket {
    /**
     * @return
     */
    public IPacket getPayload();

    /**
     * @param packet
     * @return
     */
    public IPacket setPayload(IPacket packet);

    /**
     * @return
     */
    public IPacket getParent();

    /**
     * @param packet
     * @return
     */
    public IPacket setParent(IPacket packet);

    /**
     * Reset any checksums as needed, and call resetChecksum on all parents
     */
    public void resetChecksum();

    /**
     * Sets all payloads parent parser if applicable, then serializes this
     * parser and all payloads
     *
     * @return a byte[] containing this parser and payloads
     */
    public byte[] serialize();

    /**
     * Deserializes this parser layer and all possible payloads
     *
     * @param data
     * @param offset offset to start deserializing from
     * @param length length of the data to deserialize
     * @return the deserialized data
     */
    public IPacket deserialize(byte[] data, int offset, int length)
            throws PacketParsingException;

    /**
     * Clone this parser and its payload parser but not its parent.
     *
     * @return
     */
    public Object clone();
}