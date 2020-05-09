/****************************************************************************/
// Eclipse SUMO, Simulation of Urban MObility; see https://eclipse.org/sumo
// Copyright (C) 2017-2018 German Aerospace Center (DLR) and others.
// TraCI4J module
// Copyright (C) 2011 ApPeAL Group, Politecnico di Torino
// This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v2.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v20.html
// SPDX-License-Identifier: EPL-2.0
/****************************************************************************/
/// @file    Command.java
/// @author  Enrico Gueli
/// @author  Mario Krumnow
/// @date    2011
/// @version $Id$
///
//
/****************************************************************************/
package it.polito.appeal.traci.protocol;

import java.io.Serializable;

import de.uniluebeck.itm.tcpip.Storage;

/**
 * Represents a single TraCI command, with its identifier and content. It can
 * be used either as a request (i.e. from client to server) and as a response
 * (i.e. from server to client). The "command" term reflects the description
 * in the <a href="https://sumo.dlr.de/wiki/TraCI/Protocol#Messages">wiki</a>.
 * Each command is characterized by a type identifier and a variable-sized
 * content. The command can be constructed either from a data block (a
 * {@link Storage}) or from scratch; its content can be read or written to a
 * {@link Storage}.
 * 
 * @author Enrico Gueli &lt;enrico.gueli@polito.it&gt;
 * @see <a href="https://sumo.dlr.de/wiki/TraCI/Protocol#Messages">Messages</a>
 */
public class Command implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4953668995318598414L;

	private static final int HEADER_SIZE = 
		Byte.SIZE/8     // length 0
	  + Integer.SIZE/8  // integer length
	  + Byte.SIZE/8;     // command id
	
	private final int id;
	private final Storage content;
	
	/**
	 * Builds a command from a {@link Storage} received from the other endpoint.
	 * <p>
	 * Note that this will advance the storage's internal pointer to the next
	 * data.
	 * @param rawStorage raw storage
	 */
	public Command(Storage rawStorage) {
		int contentLen = rawStorage.readUnsignedByte();
		if (contentLen == 0)
			contentLen = rawStorage.readInt() - 6;
		else
			contentLen = contentLen - 2;

		id = rawStorage.readUnsignedByte();
		
		short[] buf = new short[contentLen];
		for (int i=0; i<contentLen; i++) {
			buf[i] = (byte)rawStorage.readUnsignedByte();
		}
		
		content = new Storage(buf);
	}
	
	/**
	 * Creates a command with a given identifier and an empty content.
	 * @param id id
	 */
	public Command(int id) {
		if (id > 255)
			throw new IllegalArgumentException("id should fit in a byte");
		content = new Storage();
		this.id = id;
	}

	/**
	 * Returns the type identifier.
	 * @return the id
	 */
	public int id() {
		return id;
	}

	/**
	 * Returns the content.
	 * @return the content
	 */
	public Storage content() {
		return content;
	}

	/**
	 * Writes the serialized form of this command to the given {@link Storage}
	 * object.
	 * <p>
	 * Note: this will advance the internal pointer of the given storage. 
	 * @param out output
	 */
	public void writeRawTo(Storage out) {
		/*
		 * use only the long form (length 0 + length as integer)
		 */
		out.writeByte(0);
		out.writeInt(HEADER_SIZE + content.size());
		
		out.writeUnsignedByte(id);
		
		for (Byte b : content.getStorageList()) {
			out.writeByte(b);
		}
	}

	/**
	 * Returns the expected size of the serialized form of this command.
	 * @return raw size
	 */
	public int rawSize() {
		return HEADER_SIZE + content.size();
	}
}
