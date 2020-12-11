package com.github.exabrial.jca.udp.ra;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

@Data
public class UDPWorker implements Work {
	private static final Logger log = LoggerFactory.getLogger(UDPWorker.class);
	private final WorkManager workManager;
	private final MessageEndpointFactory messageEndpointFactory;
	private final Integer maxPacketSize;
	private final Integer port;
	private final InetAddress inetAddress;
	private final DatagramSocket datagramSocket;

	private volatile boolean done;

	public UDPWorker(final WorkManager workManager, final MessageEndpointFactory messageEndpointFactory, final Integer maxPacketSize,
			final Integer port, final String address) throws UnknownHostException, SocketException {
		this.workManager = workManager;
		this.messageEndpointFactory = messageEndpointFactory;
		this.maxPacketSize = maxPacketSize;
		this.port = port;
		this.inetAddress = InetAddress.getByName(address);
		datagramSocket = new DatagramSocket(port, inetAddress);
		datagramSocket.setSoTimeout(1000);
	}

	@Override
	public void run() {
		log.info("run() UDP Listener on socket bound to:{}:{}", inetAddress, port);
		done = false;
		while (!done) {
			try {
				final UDPUtil.InboundPacket inboundPacket = UDPUtil.receive(datagramSocket, maxPacketSize);
				if (inboundPacket != null) {
					workManager.scheduleWork(new UDPClientWorker(messageEndpointFactory, datagramSocket, inboundPacket));
				}
			} catch (final Exception e) {
				log.error("run() error occured during lifecycle", e);
			}
		}
		log.info("run() UDP listener stopped");
	}

	@Override
	public void release() {
		log.info("release() signaling for UDP Listener stop");
		done = true;
		datagramSocket.close();
	}
}
