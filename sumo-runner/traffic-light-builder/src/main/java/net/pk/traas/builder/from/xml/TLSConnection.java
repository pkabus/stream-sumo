package net.pk.traas.builder.from.xml;

public class TLSConnection {

	private String fromId;
	private String toId;
	private String dir;
	private String tlsId;
	private int linkIndex;

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
