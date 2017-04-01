package org.snlab.maple.packet.protocol;

import io.netty.buffer.ByteBuf;

public interface Writeable {
    void writeTo(ByteBuf bb);
}
