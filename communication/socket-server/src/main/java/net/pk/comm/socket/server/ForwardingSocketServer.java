package net.pk.comm.socket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * This socket server expects two client sockets. If they have bound to the
 * server, the server simply listens on the first client and forwards its input
 * to the second socket.
 * 
 * @author peter
 *
 */
public class ForwardingSocketServer {

	private int portNumber;
	private boolean status;

	/**
	 * Construct new server.
	 */
	public ForwardingSocketServer(final int portNumber) {
		this.portNumber = portNumber;
	}

	/**
	 * Waiting for two clients and forwarding from client-1 to client-2.
	 * 
	 * @throws IOException
	 */
	public void run() throws SocketTimeoutException {
		try (ServerSocket serverSocket = ForwardingSocketServer.createServerSocket(portNumber);
				Socket emitterSocket = serverSocket.accept();
				Socket receiverSocket = serverSocket.accept();
				PrintWriter out = new PrintWriter(receiverSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(emitterSocket.getInputStream()));) {
			this.status = true;
			String inputLine;
			while (true) {
				inputLine = in.readLine();
				if (inputLine != null)
					out.println(inputLine);
			}
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static ServerSocket createServerSocket(final int port) throws IOException {
		ServerSocket s = new ServerSocket(port);
		s.setSoTimeout(30000);
		return s;
	}

	/**
	 * Returns true if and only if the two clients have connected to the socket.
	 * 
	 * @return if server is ready to run
	 */
	public boolean isReady() {
		return status;
	}
}
