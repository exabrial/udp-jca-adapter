package com.github.exabrial.jca.udp.ra;

import java.net.DatagramSocket;
import java.net.SocketException;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UDPResourceAdapter implements ResourceAdapter {
	private Integer port;
	private Integer maxPacketSize;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private WorkManager workManager;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Work work;

	@Override
	public void start(final BootstrapContext bootstrapContext) {
		workManager = bootstrapContext.getWorkManager();
	}

	@Override
	public void endpointActivation(final MessageEndpointFactory messageEndpointFactory, final ActivationSpec activationSpec)
			throws ResourceException {
		try {
			final DatagramSocket datagramSocket = new DatagramSocket(port);
			datagramSocket.setSoTimeout(1000);
			work = new UDPWorker(workManager, messageEndpointFactory, datagramSocket, maxPacketSize);
			workManager.scheduleWork(work);
		} catch (final SocketException e) {
			throw new ResourceException(e);
		}
	}

	@Override
	public void endpointDeactivation(final MessageEndpointFactory mepf, final ActivationSpec as) {
		work.release();
	}

	@Override
	public void stop() {
		work.release();
	}

	@Override
	public XAResource[] getXAResources(final ActivationSpec[] as) throws ResourceException {
		throw new ResourceException("no XA support");
	}
}
