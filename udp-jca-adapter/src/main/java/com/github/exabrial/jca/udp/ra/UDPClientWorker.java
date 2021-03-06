package com.github.exabrial.jca.udp.ra;

import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.exabrial.jca.udp.UDPMessageListener;
import com.github.exabrial.jca.udp.ra.UDPUtil.InboundPacket;

public class UDPClientWorker implements Work {
	private static final String ON_PACKET = "onPacket";
	private static final Logger log = LoggerFactory.getLogger(UDPClientWorker.class);
	private final MessageEndpointFactory messageEndpointFactory;
	private final DatagramSocket datagramSocket;
	private final UDPUtil.InboundPacket inboundPacket;
	private final Method onPacketMethod;

	public UDPClientWorker(final MessageEndpointFactory messageEndpointFactory, final DatagramSocket datagramSocket,
			final InboundPacket inboundPacket) {
		this.messageEndpointFactory = messageEndpointFactory;
		this.datagramSocket = datagramSocket;
		this.inboundPacket = inboundPacket;
		try {
			onPacketMethod = UDPMessageListener.class.getMethod(ON_PACKET, byte[].class, InetAddress.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void run() {
		MessageEndpoint messageEndpoint = null;
		try {
			log.trace("run() inboundPacket:{}", inboundPacket);
			messageEndpoint = messageEndpointFactory.createEndpoint(null);
			messageEndpoint.beforeDelivery(onPacketMethod);
			final UDPMessageListener messageListener = (UDPMessageListener) messageEndpoint;
			final byte[] reply = messageListener.onPacket(inboundPacket.getData(), inboundPacket.getSourceAddress());
			if (reply != null) {
				log.trace("run() replying:{}", reply);
				UDPUtil.send(datagramSocket,
						new UDPUtil.InboundPacket(reply, inboundPacket.getSourceAddress(), inboundPacket.getSourcePort()));
			}
			messageEndpoint.afterDelivery();
			log.trace("run() UDPClientWorker complete");
		} catch (final Exception e) {
			log.error("run() error occured while handling packet", e);
			log.error("run() inboundPacket:{}", inboundPacket);
		} finally {
			if (messageEndpoint != null) {
				messageEndpoint.release();
			}
		}
	}

	@Override
	public void release() {
	}
}
