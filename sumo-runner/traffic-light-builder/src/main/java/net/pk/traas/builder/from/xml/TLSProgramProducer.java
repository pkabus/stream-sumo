package net.pk.traas.builder.from.xml;

/**
 * A program producer implements a tls program producer strategy.
 * 
 * @author peter
 *
 */
public interface TLSProgramProducer {

	/**
	 * Produces a string representation of a TLS phase.
	 * 
	 * @return TLS phase
	 */
	public String produceTLSPhase();

}
