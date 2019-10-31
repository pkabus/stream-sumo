package net.pk.data.type;

/**
 * Implementations using this interface label themselves as associated to a TLS
 * in any sense. They need to have a connection to a TLS.
 * 
 * @author peter
 *
 */
public interface TLSAssociated {

	/**
	 * Associated TLS id of this object.
	 * 
	 * @return tls id
	 */
	String getTLS();
}
