package net.pk.traas.builder.from.xml;

/**
 * Java representation of a Sumo connection
 * (https://sumo.dlr.de/docs/Networks/SUMO_Road_Networks.html#plain_connections).
 * 
 * @author peter
 *
 */
public class TLSConnection {

	private String fromId;
	private String toId;
	private String dir;
	private String tlsId;
	private int linkIndex;

	/**
	 * Constructor.
	 * 
	 * @param from      edge
	 * @param to        edge
	 * @param dir       direction
	 * @param tls       id of corresponding tls
	 * @param linkIndex in the tls
	 */
	public TLSConnection(String from, String to, String dir, String tls, int linkIndex) {
		this.fromId = from;
		this.toId = to;
		this.dir = dir;
		this.tlsId = tls;
		this.linkIndex = linkIndex;
	}

	/**
	 * Getter.
	 * 
	 * @return the fromId
	 */
	public String getFromId() {
		return fromId;
	}

	/**
	 * Getter.
	 * 
	 * @return the toId
	 */
	public String getToId() {
		return toId;
	}

	/**
	 * Getter.
	 * 
	 * @return the dir
	 */
	public String getDir() {
		return dir;
	}

	/**
	 * Getter.
	 * 
	 * @return the tlsId
	 */
	public String getTlsId() {
		return tlsId;
	}

	public int getLinkIndex() {
		return linkIndex;
	}

	@Override
	public String toString() {
		return "TLS: " + tlsId + ", From: " + fromId + ", To: " + toId + ", Dir: " + dir;
	}
}
