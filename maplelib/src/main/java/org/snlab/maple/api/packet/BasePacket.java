/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

// package net.floodlightcontroller.packet;
package org.snlab.maple.api.packet;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;


/**
*
* @author David Erickson (daviderickson@cs.stanford.edu)
*/
public abstract class BasePacket implements IPacket {
  // public static final Logger log = LoggerFactory.getLogger(BasePacket.class);
    protected IPacket parent;
    protected IPacket payload;

    /**
     * @return the parent
     */
    public IPacket getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public IPacket setParent(IPacket parent) {
        this.parent = parent;
        return this;
    }

    /**
     * @return the payload
     */
    public IPacket getPayload() {
        return payload;
    }

    /**
     * @param payload the payload to set
     */
    public IPacket setPayload(IPacket payload) {
        this.payload = payload;
        return this;
    }

    public void resetChecksum() {
        if (this.parent != null)
            this.parent.resetChecksum();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 6733;
        int result = 1;
        result = prime * result + ((payload == null) ? 0 : payload.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof BasePacket))
            return false;
        BasePacket other = (BasePacket) obj;
        if (payload == null) {
            if (other.payload != null)
                return false;
        } else if (!payload.equals(other.payload))
            return false;
        return true;
    }

    @Override
    public Object clone() {
        IPacket pkt;
        try {
            pkt = this.getClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not clone packet");
        }
        // TODO: we are using serialize()/deserialize() to perform the
        // cloning. Not the most efficient way but simple. We can revisit
        // if we hit performance problems.
        byte[] data = this.serialize();
        try {
            pkt.deserialize(this.serialize(), 0, data.length);
        } catch (PacketParsingException e) {
            // This shouldn't happen here, since we already deserialized it once
            return new Data(data);
        }
        pkt.setParent(this.parent);
        return pkt;
    }
}
