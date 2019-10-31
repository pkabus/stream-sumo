package net.pk.data.type;

/**
 * This class represents a key or id of a certain TLS in Sumo. Also a static set
 * exists, that is used to store the keys when a certain scenario is running.
 * 
 * @author peter
 *
 */
public class TLSKey {

	private String id;

	/**
	 * Constructor.
	 * 
	 * @param id of tls
	 */
	public TLSKey(final String id) {
		this.id = id;
	}

	/**
	 * Getter.
	 * 
	 * @return tls id
	 */
	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof TLSKey) ? ((TLSKey) o).id.equals(this.id) : false;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
}
