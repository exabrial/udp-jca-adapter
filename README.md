# udp-jca-adapter

## Description

A JCA 1.7 compliant Resource Adapter RAR to accept UDP packets with full modern Java EE facilities in a fast memory safe language.


## Motivation / Use Case

Do you want to accept UDP connections in Java, but have full access to CDI, JAX-RS, JPA, SQL, JTA, etc?

Simply implement the `UDPMessageListener` interface:

```
import java.math.BigInteger;
import java.net.InetAddress;

import javax.ejb.MessageDriven;

@MessageDriven
public class UDPDumper implements UDPMessageListener {
   @Inject
   private Logger log;

	@Override
	public byte[] onPacket(final byte[] payload, final InetAddress sourceAddress) {
		log.info("onPacket() UDP packet from:{}", sourceAddress.getHostAddress());
		return null;
	}
}
```

## Maven Coordinates

The API jar contains the interface you need to implement.

```
		<dependency>
			<groupId>com.github.exabrial</groupId>
			<artifactId>udp-jca-adapter-api</artifactId>
			<version>1.1.0</version>
			<scope>compile</scope>
		</dependency>
```

## Installation 

RAR installation by app server differs greatly by server. Most of the time you install a JCA adapter at the EAR or server level.

### Easiset deployment: Apache TomEE 8.0.4+ embedded war deployment

To deploy at the Application level, add the following to your app's `pom.xml`, then copy the `ra.xml` from the into your WEB-INF directory.

```
		<dependency>
			<groupId>com.github.exabrial</groupId>
			<artifactId>udp-jca-adapter</artifactId>
			<version>1.1.0</version>
			<scope>runtime</scope>
		</dependency>
```

## Configuration

| config option 	| type 	| default 	|
|---------------	|------	|---------	|
| port          	| int  	| 5553    	|
| maxPacketSize 	| int  	| 512     	|
| address         | String| localhost |

