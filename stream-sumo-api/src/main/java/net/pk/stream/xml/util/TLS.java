package net.pk.stream.xml.util;

/**
 * Implementations of this interface are supposed to be TLS objects. This means
 * their id must match a tls object in Sumo (junctions of type 'traffic_light'
 * are not TLS objects! Sometimes their ids are equal, but it is wrong to assume
 * it).
 * 
 * @author peter
 *
 */
public interface TLS {

	/**
	 * Id of the TLS.
	 * 
	 * @return id
	 */
	String getTlsId();

}
