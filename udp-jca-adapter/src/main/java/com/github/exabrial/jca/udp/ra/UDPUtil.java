package com.github.exabrial.jca.udp.ra;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import lombok.AllArgsConstructor;
import lombok.Data;

public final class UDPUtil {
	public static InboundPacket receive(final DatagramSocket datagramSocket, final int maxPacketSize) throws IOException {
		final byte[] buffer = new byte[maxPacketSize];
		final DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
		InboundPacket inboundPacket;
		try {
			datagramSocket.receive(datagramPacket);
			inboundPacket = new InboundPacket(datagramPacket.getData(), datagramPacket.getAddress(), datagramPacket.getPort());
		} catch (final SocketTimeoutException e) {
			inboundPacket = null;
		}
		return inboundPacket;
	}

	public static void send(final DatagramSocket datagramSocket, final InboundPacket inboundPacket) throws IOException {
		final byte[] payload = inboundPacket.getData();
		final DatagramPacket datagramPacket = new DatagramPacket(payload, payload.length);
		datagramPacket.setAddress(inboundPacket.getSourceAddress());
		datagramPacket.setPort(inboundPacket.getSourcePort());
		datagramSocket.send(datagramPacket);
	}

	@Data
	@AllArgsConstructor
	public static class InboundPacket {
		private final byte[] data;
		private final InetAddress sourceAddress;
		private final int sourcePort;
	}
}
