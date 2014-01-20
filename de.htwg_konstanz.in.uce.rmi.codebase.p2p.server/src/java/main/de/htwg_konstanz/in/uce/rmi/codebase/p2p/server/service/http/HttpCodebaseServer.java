package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase;

/**
 * Implementation of a simple HTTP service.
 * Serves files from the given codebase.
 * 
 * TODO: allow put/post to save resources
 *    
 * @author Marco Krammer
 *
 * @param <T>
 */
public final class HttpCodebaseServer<T> implements Runnable {
	
	private static final Logger logger = Logger.getLogger(HttpCodebaseServer.class.getName());
	public static final int DefaultServerPort = 2001;
	private static final int MAX_THREADS = 10;
	
	private ServerSocket serverSocket;
	private ExecutorService executor;
	private ICodebase<T> codebase;

	/**
	 * Constructor for HttpCodebaseServer,
	 * sets the port and the codebase
	 * 
	 * @param port to listen for incoming connections
	 * @param codebase to use as container
	 * @throws IOException if e.g. port is already in use
	 */
	public HttpCodebaseServer(int port, ICodebase<T> codebase) throws IOException {
		this.codebase = codebase;
		
		serverSocket = new ServerSocket(port);		
		
		executor = Executors.newFixedThreadPool(MAX_THREADS);
				
		newListener();
		
		logger.info("HttpClassServer listening on " + port + " using " + codebase);
	}
	
	/**
	 * Constructor for HttpCodebaseServer,
	 * sets only the codebase and uses the default port
	 * 
	 * @param codebase to use as container
	 * @throws IOException
	 */
	public HttpCodebaseServer(ICodebase<T> codebase) throws IOException {
		this(DefaultServerPort, codebase);
	}

	/**
	 * Starts the server socket and spawns new threads
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Socket socket;
		
		// accept a connection
		try {
			socket = serverSocket.accept();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "server socket died: " + e.getMessage(), e);
			return;
		}
		logger.fine("accept: " + socket);
		
		 // create a new thread to accept the next connection
		newListener();

		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String path = extractPath(in);
				byte[] bytes = codebase.get(path);
				if (null == bytes) {
					sendError(out, "404", "Resource not found or removed.");
					return;
				}

				try { 
					//write header
					out.writeBytes("HTTP/1.0 200 OK\r\n");
					out.writeBytes("Content-Length: " + bytes.length + "\r\n");
					out.writeBytes("Content-Type: application/java\r\n\r\n");
					//write class bytes
					out.write(bytes);
					out.flush();
					logger.fine("served from codebase " + bytes.length + " bytes");
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
					return;
				}
			} catch (Exception e) {
				sendError(out, "400", e.getMessage());
				
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	private void newListener() {
		executor.execute(this);
	}

	private String extractPath(BufferedReader in) throws IOException {
		String line = in.readLine();
		logger.fine(line);
		
		String path = "";
		if (line.startsWith("GET ")) {
			int idx = line.lastIndexOf(" HTTP/");
			if (idx > 0) {
				path = line.substring("GET ".length(), idx).trim();
			} else {	//TODO remove, not needed
				path = line.substring("GET ".length(), line.length()).trim();
			}
		}
		
		// eat the rest of the header
		do {
			line = in.readLine();
		} while (line.length() > 0);
		
		if (path.length() > 0) {
			return path;
		}
		throw new IOException("Malformed request header: " + path);
	}

	private void sendError(OutputStream out, String statusCode, String reasonPhrase) throws IOException {
		StringBuffer response = new StringBuffer("HTTP/1.0 ")
									.append(statusCode)
									.append(" ")
									.append(reasonPhrase)
									.append("\r\n");
		
		logger.fine("sendError: " + response.toString());
		
		response.append("Content-Type: text/html\r\n\r\n");
		
		out.write(response.toString().getBytes());
		out.flush();
		out.close();
	}
	
	/**
	 * Closes the server socket and shuts the
	 * executor service down
	 */
	public void shutdown() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			//ignore
		}
		
		executor.shutdown();
		while (!executor.isTerminated()) {
			logger.fine("Waiting for executer to terminate");
		}
		logger.fine("shutdown");
	}
	
	/**
	 * Simple getter for the server socket port in use
	 * 
	 * @return the port of the server socket
	 */
	public int getPort() {
		return serverSocket.getLocalPort();
	}

}