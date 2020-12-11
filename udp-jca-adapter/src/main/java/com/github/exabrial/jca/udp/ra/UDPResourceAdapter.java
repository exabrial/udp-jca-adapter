package com.github.exabrial.jca.udp.ra;

import java.net.SocketException;
import java.net.UnknownHostException;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UDPResourceAdapter implements ResourceAdapter {
	private String address;
	private Integer port;
	private Integer maxPacketSize;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private WorkManager workManager;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private UDPWorker udpWorker;

	@Override
	public void start(final BootstrapContext bootstrapContext) {
		workManager = bootstrapContext.getWorkManager();
	}

	@Override
	public void endpointActivation(final MessageEndpointFactory messageEndpointFactory, final ActivationSpec activationSpec)
			throws ResourceException {
		try {
			udpWorker = new UDPWorker(workManager, messageEndpointFactory, maxPacketSize, port, address);
			workManager.scheduleWork(udpWorker);
		} catch (final UnknownHostException | SocketException e) {
			throw new ResourceException(e);
		}
	}

	@Override
	public void endpointDeactivation(final MessageEndpointFactory mepf, final ActivationSpec as) {
		udpWorker.release();
	}

	@Override
	public void stop() {
		udpWorker.release();
	}

	@Override
	public XAResource[] getXAResources(final ActivationSpec[] as) throws ResourceException {
		throw new ResourceException("no XA support");
	}
}
