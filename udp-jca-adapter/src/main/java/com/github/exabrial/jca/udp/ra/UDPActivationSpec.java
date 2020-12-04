package com.github.exabrial.jca.udp.ra;

import javax.resource.spi.ActivationSpec;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;

import lombok.Data;

@Data
public class UDPActivationSpec implements ActivationSpec {
	private ResourceAdapter resourceAdapter;

	@Override
	public void validate() throws InvalidPropertyException {
	}
}
