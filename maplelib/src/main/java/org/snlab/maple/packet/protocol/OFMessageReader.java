package org.snlab.maple.packet.protocol;

import io.netty.buffer.ByteBuf;
import org.snlab.maple.packet.exceptions.OFParseError;


public interface OFMessageReader<T> {
    T readFrom(ByteBuf bb) throws OFParseError;
}
