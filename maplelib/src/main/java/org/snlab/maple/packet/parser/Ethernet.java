/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.packet.parser;



import org.snlab.maple.util.HexString;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Ethernet extends BasePacket {
    public static final short TYPE_ARP = 0x0806;
    public static final short TYPE_RARP = (short) 0x8035;
    public static final short TYPE_IPv4 = 0x0800;
    public static final short TYPE_LLDP = (short) 0x88cc;
    public static final short TYPE_VLAN = (short) 0x8100;
    public static final short VLAN_UNTAGGED = (short) 0xffff;
    public static final short DATALAYER_ADDRESS_LENGTH = 6; // bytes
    public static Map<Short, Class<? extends IPacket>> etherTypeClassMap;
    private static String HEXES = "0123456789ABCDEF";

    static {
        etherTypeClassMap = new HashMap<Short, Class<? extends IPacket>>();
        etherTypeClassMap.put(TYPE_ARP, ARP.class);
        etherTypeClassMap.put(TYPE_RARP, ARP.class);
        etherTypeClassMap.put(TYPE_IPv4, IPv4.class);
        etherTypeClassMap.put(TYPE_LLDP, LLDP.class);
    }

    protected MACAddress destinationMACAddress;
    protected MACAddress sourceMACAddress;
    protected byte priorityCode;
    protected short vlanID;
    protected short etherType;
    protected boolean pad = false;

    /**
     * By default, set Ethernet to untagged
     */
    public Ethernet() {
        super();
        this.vlanID = VLAN_UNTAGGED;
    }

    /**
     * @return the destination MAC as a byte array
     */
    public byte[] getDestinationMACAddress() {
        return destinationMACAddress.toBytes();
    }

    /**
     * @param destinationMACAddress the destination MAC to set
     */
    public Ethernet setDestinationMACAddress(String destinationMACAddress) {
        this.destinationMACAddress = MACAddress.valueOf(destinationMACAddress);
        return this;
    }

    /**
     * @return the destination MAC
     */
    public MACAddress getDestinationMAC() {
        return destinationMACAddress;
    }

    /**
     * @param destinationMACAddress the destination MAC to set
     */
    public Ethernet setDestinationMACAddress(byte[] destinationMACAddress) {
        this.destinationMACAddress = MACAddress.valueOf(destinationMACAddress);
        return this;
    }

    /**
     * @return the source MACAddress as a byte array
     */
    public byte[] getSourceMACAddress() {
        return sourceMACAddress.toBytes();
    }

    /**
     * @param sourceMACAddress the source MAC to set
     */
    public Ethernet setSourceMACAddress(String sourceMACAddress) {
        this.sourceMACAddress = MACAddress.valueOf(sourceMACAddress);
        return this;
    }

    /**
     * @return the source MACAddress
     */
    public MACAddress getSourceMAC() {
        return sourceMACAddress;
    }

    /**
     * @param sourceMACAddress the source MAC to set
     */
    public Ethernet setSourceMACAddress(byte[] sourceMACAddress) {
        this.sourceMACAddress = MACAddress.valueOf(sourceMACAddress);
        return this;
    }

    /**
     * @return the priorityCode
     */
    public byte getPriorityCode() {
        return priorityCode;
    }

    /**
     * @param priorityCode the priorityCode to set
     */
    public Ethernet setPriorityCode(byte priorityCode) {
        this.priorityCode = priorityCode;
        return this;
    }

    /**
     * @return the vlanID
     */
    public short getVlanID() {
        return vlanID;
    }

    /**
     * @param vlanID the vlanID to set
     */
    public Ethernet setVlanID(short vlanID) {
        this.vlanID = vlanID;
        return this;
    }

    /**
     * @return the etherType
     */
    public short getEtherType() {
        return etherType;
    }

    /**
     * @param etherType the etherType to set
     */
    public Ethernet setEtherType(short etherType) {
        this.etherType = etherType;
        return this;
    }


    /**
     * Pad this parser to 60 bytes minimum, filling with zeros?
     *
     * @return the pad
     */
    public boolean isPad() {
        return pad;
    }

    /**
     * Pad this parser to 60 bytes minimum, filling with zeros?
     *
     * @param pad the pad to set
     */
    public Ethernet setPad(boolean pad) {
        this.pad = pad;
        return this;
    }

    public byte[] serialize() {
        byte[] payloadData = null;
        if (payload != null) {
            payload.setParent(this);
            payloadData = payload.serialize();
        }
        int length = 14 + ((vlanID == VLAN_UNTAGGED) ? 0 : 4) +
                ((payloadData == null) ? 0 : payloadData.length);
        if (pad && length < 60) {
            length = 60;
        }
        byte[] data = new byte[length];
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.put(destinationMACAddress.toBytes());
        bb.put(sourceMACAddress.toBytes());
        if (vlanID != VLAN_UNTAGGED) {
            bb.putShort((short) 0x8100);
            bb.putShort((short) ((priorityCode << 13) | (vlanID & 0x0fff)));
        }
        bb.putShort(etherType);
        if (payloadData != null)
            bb.put(payloadData);
        if (pad) {
            Arrays.fill(data, bb.position(), data.length, (byte) 0x0);
        }
        return data;
    }

    public IPacket deserialize(byte[] data, int offset, int length) {
        if (length <= 16)  // Ethernet parser minium should be 60, this is reasonable
            return null;
        ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
        if (this.destinationMACAddress == null)
            this.destinationMACAddress = MACAddress.valueOf(new byte[6]);
        byte[] dstAddr = new byte[MACAddress.MAC_ADDRESS_LENGTH];
        bb.get(dstAddr);
        this.destinationMACAddress = MACAddress.valueOf(dstAddr);

        if (this.sourceMACAddress == null)
            this.sourceMACAddress = MACAddress.valueOf(new byte[6]);
        byte[] srcAddr = new byte[MACAddress.MAC_ADDRESS_LENGTH];
        bb.get(srcAddr);
        this.sourceMACAddress = MACAddress.valueOf(srcAddr);

        short etherType = bb.getShort();
        if (etherType == (short) 0x8100) {
            short tci = bb.getShort();
            this.priorityCode = (byte) ((tci >> 13) & 0x07);
            this.vlanID = (short) (tci & 0x0fff);
            etherType = bb.getShort();
        } else {
            this.vlanID = VLAN_UNTAGGED;
        }
        this.etherType = etherType;

        IPacket payload;
        if (Ethernet.etherTypeClassMap.containsKey(this.etherType)) {
            Class<? extends IPacket> clazz = Ethernet.etherTypeClassMap.get(this.etherType);
            try {
                payload = clazz.newInstance();
                this.payload = payload.deserialize(data, bb.position(), bb.limit() - bb.position());
            } catch (PacketParsingException e) {
                this.payload = new Data(data);
            } catch (InstantiationException e) {
                this.payload = new Data(data);
            } catch (IllegalAccessException e) {
                this.payload = new Data(data);
            } catch (RuntimeException e) {
                this.payload = new Data(data);
            }
        } else {
            this.payload = new Data(data);
        }
        this.payload.setParent(this);
        return this;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 7867;
        int result = super.hashCode();
        result = prime * result + destinationMACAddress.hashCode();
        result = prime * result + etherType;
        result = prime * result + vlanID;
        result = prime * result + priorityCode;
        result = prime * result + (pad ? 1231 : 1237);
        result = prime * result + sourceMACAddress.hashCode();
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof Ethernet))
            return false;
        Ethernet other = (Ethernet) obj;
        if (!destinationMACAddress.equals(other.destinationMACAddress))
            return false;
        if (priorityCode != other.priorityCode)
            return false;
        if (vlanID != other.vlanID)
            return false;
        if (etherType != other.etherType)
            return false;
        if (pad != other.pad)
            return false;
        if (!sourceMACAddress.equals(other.sourceMACAddress))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString(java.lang.Object)
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer("\n");

        IPacket pkt = this.getPayload();

        if (pkt instanceof ARP)
            sb.append("arp");
        else if (pkt instanceof LLDP)
            sb.append("lldp");
        else if (pkt instanceof ICMP)
            sb.append("icmp");
        else if (pkt instanceof IPv4)
            sb.append("ip");
        else if (pkt instanceof DHCP)
            sb.append("dhcp");
        else sb.append(this.getEtherType());

        sb.append("\ndl_vlan: ");
        if (this.getVlanID() == Ethernet.VLAN_UNTAGGED)
            sb.append("untagged");
        else
            sb.append(this.getVlanID());
        sb.append("\ndl_vlan_pcp: ");
        sb.append(this.getPriorityCode());
        sb.append("\ndl_src: ");
        sb.append(HexString.toHexString(this.getSourceMACAddress()));
        sb.append("\ndl_dst: ");
        sb.append(HexString.toHexString(this.getDestinationMACAddress()));


        if (pkt instanceof ARP) {
            ARP p = (ARP) pkt;
            sb.append("\nnw_src: ");
            sb.append(IPv4.fromIPv4Address(IPv4.toIPv4Address(p.getSenderProtocolAddress())));
            sb.append("\nnw_dst: ");
            sb.append(IPv4.fromIPv4Address(IPv4.toIPv4Address(p.getTargetProtocolAddress())));
        } else if (pkt instanceof LLDP) {
            sb.append("lldp parser");
        } else if (pkt instanceof ICMP) {
            ICMP icmp = (ICMP) pkt;
            sb.append("\nicmp_type: ");
            sb.append(icmp.getIcmpType());
            sb.append("\nicmp_code: ");
            sb.append(icmp.getIcmpCode());
        } else if (pkt instanceof IPv4) {
            IPv4 p = (IPv4) pkt;
            sb.append("\nnw_src: ");
            sb.append(IPv4.fromIPv4Address(p.getSourceAddress()));
            sb.append("\nnw_dst: ");
            sb.append(IPv4.fromIPv4Address(p.getDestinationAddress()));
            sb.append("\nnw_tos: ");
            sb.append(p.getDiffServ());
            sb.append("\nnw_proto: ");
            sb.append(p.getProtocol());

            if (p.getProtocol() == IPv4.PROTOCOL_TCP) {
                TCP pTCP = (TCP) p.getPayload();
                sb.append("\ntcpsrc: ");
                sb.append(pTCP.getSourcePort());
                sb.append("\ntcpdst: ");
                sb.append(pTCP.getDestinationPort());
            }
        } else if (pkt instanceof DHCP) {
            sb.append("\ndhcp parser");
        } else if (pkt instanceof Data) {
            sb.append("\ndata parser");
        } else sb.append("\nunknwon parser");

        return sb.toString();
    }
}