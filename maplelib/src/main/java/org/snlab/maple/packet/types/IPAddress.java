package org.snlab.maple.packet.types;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

public abstract class IPAddress<F extends IPAddress<F>> implements OFValueType<F> {

    /**
     * Returns the Internet Protocol (IP) version of this object
     *
     * @return  the Internet Protocol (IP) version of this object
     */
    public abstract IPVersion getIpVersion();

    /**
     * Checks if this IPAddress represents a valid CIDR style netmask, i.e.,
     * it has a set of leading "1" bits followed by only "0" bits
     * @return true if this represents a valid CIDR style netmask, false
     * otherwise
     */
    public abstract boolean isCidrMask();

    /**
     * If this IPAddress represents a valid CIDR style netmask (see
     * isCidrMask()) returns the length of the prefix (the number of "1" bits).
     * @return length of CIDR mask if this represents a valid CIDR mask
     * @throws IllegalStateException if isCidrMask() == false
     */
    public abstract int asCidrMaskLength();

    /**
     * Returns {@code true} if the IPAddress is unspecified.
     *
     * <p>The <em>unspecified</em> addresses, also known as the
     * <em>wildcard</em> addresses, refer to:
     * <ul>
     * <li>the {@link IPv4Address} of {@code 0.0.0.0}
     * <li>the {@link IPv6Address} of {@code ::}
     * </ul>
     * @return {@code true} if the IPAddress is unspecified, false otherwise
     */
    public abstract boolean isUnspecified();

    /**
     * Returns {@code true} if the IPAddress is a loopback address.
     *
     * <p><em>Loopback</em> addresses refer to:
     * <ul>
     * <li>any {@link IPv4Address} within {@code 127.0.0.0/8}
     * <li>the {@link IPv6Address} of {@code ::1}
     * </ul>
     * @return {@code true} if the IPAddress is a loopback address, false otherwise
     */
    public abstract boolean isLoopback();

    /**
     * Returns {@code true} if the IPAddress is a link local address.
     *
     * <p><em>Link local</em> addresses refer to:
     * <ul>
     * <li>any {@link IPv4Address} within {@code 169.254.0.0/16}
     * <li>any {@link IPv6Address} within {@code fe80::/10}
     * </ul>
     * @return {@code true} if the IPAddress is a link local address, false otherwise
     */
    public abstract boolean isLinkLocal();

    /**
     * Checks if the IPAddress is the global broadcast address
     * 255.255.255.255 in case of IPv4
     * @return boolean true or false
     */
    public abstract boolean isBroadcast();

    /**
     * Checks if the IPAddress is the multicast address
     * @return boolean true or false
     */
    public abstract boolean isMulticast();

    /**
     * Perform a low level AND operation on the bits of two IPAddress objects
     * @param   other IPAddress
     * @return  new IPAddress object after the AND oper
     */
    public abstract F and(F other);

    /**
     * Perform a low level OR operation on the bits of two IPAddress objects
     * @param   other IPAddress
     * @return  new IPAddress object after the AND oper
     */
    public abstract F or(F other);

    /**
     * Returns a new IPAddress object with the bits inverted
     * @return  IPAddress
     */
    public abstract F not();

    /**
     * Returns an {@code IPAddressWithMask<F>} object that represents this
     * IP address masked by the given IP address mask.
     *
     * @param mask  the {@code F} object that represents the mask
     * @return      an {@code IPAddressWithMask<F>} object that represents this
     *              IP address masked by the given mask
     * @throws NullPointerException  if the given mask was {@code null}
     */
    @Nonnull
    public abstract IPAddressWithMask<F> withMask(@Nonnull final F mask);

    /**
     * Returns an {@code IPAddressWithMask<F>} object that represents this
     * IP address masked by the CIDR subnet mask of the given prefix length.
     *
     * @param cidrMaskLength  the prefix length of the CIDR subnet mask
     *                        (i.e. the number of leading one-bits),
     *                        where <code>
     *                        0 {@literal <=} cidrMaskLength {@literal <=} (F.getLength() * 8)
     *                        </code>
     * @return                an {@code IPAddressWithMask<F>} object that
     *                        represents this IP address masked by the CIDR
     *                        subnet mask of the given prefix length
     * @throws IllegalArgumentException  if the given prefix length was invalid
     * @see org.snlab.maple.packet.types.IPv4Address#ofCidrMaskLength(int)
     * @see org.snlab.maple.packet.types.IPv6Address#ofCidrMaskLength(int)
     */
    @Nonnull
    public abstract IPAddressWithMask<F> withMaskOfLength(
            final int cidrMaskLength);

    /**
     * Returns the raw IP address of this {@code IPAddress} object. The result
     * is in network byte order: the highest order byte of the address is in
     * {@code getBytes()[0]}.
     * <p>
     * Similar to {@link InetAddress#getAddress()}
     *
     * @return  the raw IP address of this object
     * @see InetAddress#getAddress()
     */
    public abstract byte[] getBytes();

    /**
     * Returns an {@link InetAddress} object representing this IP address.
     *
     * <p>The resulting {@link InetAddress} object:
     * <ul>
     * <li>will not carry a hostname
     * <li>will not carry a non-zero scope ID or scoped interface,
     *     in the case of {@link Inet6Address}
     * </ul>
     * @return an {@link InetAddress} object representing this IP address
     */
    @Nonnull
    public abstract InetAddress toInetAddress();

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

    /** parse an IPv4Address or IPv6Address from their conventional string representation.
     *  For details on supported representations,  refer to {@link IPv4Address#of(String)}
     *  and {@link IPv6Address#of(String)}
     *
     * @param ip a string representation of an IP address
     * @return the parsed IP address
     * @throws NullPointerException if ip is null
     * @throws IllegalArgumentException if string is not a valid IP address
     */
    @Nonnull
    public static IPAddress<?> of(@Nonnull String ip) {
        Preconditions.checkNotNull(ip, "ip must not be null");
        if (ip.indexOf('.') != -1)
            return IPv4Address.of(ip);
        else if (ip.indexOf(':') != -1)
            return IPv6Address.of(ip);
        else
            throw new IllegalArgumentException("IP Address not well formed: " + ip);
    }

    /**
     * Factory function for InetAddress values.
     * @param address the InetAddress you wish to parse into an IPAddress object.
     * @return the IPAddress object.
     * @throws NullPointerException if address is null
     */
    @Nonnull
    public static IPAddress<?> of(@Nonnull InetAddress address) {
        Preconditions.checkNotNull(address, "address must not be null");
        if(address instanceof Inet4Address)
            return IPv4Address.of((Inet4Address) address);
        else if (address instanceof Inet6Address)
            return IPv6Address.of((Inet6Address) address);
        else
            return IPAddress.of(address.getHostAddress());
    }

    /**
     * Factory function for InetAddress values.
     * @param address the InetAddress you wish to parse into an IPAddress object.
     * @return the IPAddress object.
     * @throws NullPointerException if address is null
     * @deprecated  replaced by {@link #of(InetAddress)}
     */
    @Deprecated
    @Nonnull
    public static IPAddress<?> fromInetAddress(@Nonnull InetAddress address) {
        return of(address);
    }
}
