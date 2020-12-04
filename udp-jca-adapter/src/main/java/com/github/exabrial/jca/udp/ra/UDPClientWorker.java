package com.github.exabrial.jca.udp.ra;

import java.net.DatagramSocket;

import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.exabrial.jca.udp.UDPMessageListener;
import com.github.exabrial.jca.udp.ra.UDPUtil.InboundPacket;

public class UDPClientWorker implements Work {
	private static final Logger log = LoggerFactory.getLogger(UDPClientWorker.class);
	private final MessageEndpointFactory messageEndpointFactory;
	private final DatagramSocket datagramSocket;
	private final UDPUtil.InboundPacket inboundPacket;

	public UDPClientWorker(final MessageEndpointFactory messageEndpointFactory, final DatagramSocket datagramSocket,
			final InboundPacket inboundPacket) {
		this.messageEndpointFactory = messageEndpointFactory;
		this.datagramSocket = datagramSocket;
		this.inboundPacket = inboundPacket;
	}

	@Override
	public void run() {
		try {
			log.trace("run() starting handler");
			final UDPMessageListener messageListener = (UDPMessageListener) messageEndpointFactory.createEndpoint(null);
			final byte[] reply = messageListener.onPacket(inboundPacket.getData(), inboundPacket.getSourceAddress());
			if (reply != null) {
				UDPUtil.send(datagramSocket,
						new UDPUtil.InboundPacket(reply, inboundPacket.getSourceAddress(), inboundPacket.getSourcePort()));
			}
			log.trace("run() client handler complete");
		} catch (final Exception e) {
			log.error("run() error occured while handling packet", e);
			log.error("run() inboundPacket:{}", inboundPacket);
		}
	}

	@Override
	public void release() {
	}
}
