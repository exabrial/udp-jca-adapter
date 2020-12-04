package com.github.exabrial.jca.udp;

import java.net.InetAddress;

public interface UDPMessageListener {
	byte[] onPacket(byte[] payload, InetAddress sourceAddress);
}
