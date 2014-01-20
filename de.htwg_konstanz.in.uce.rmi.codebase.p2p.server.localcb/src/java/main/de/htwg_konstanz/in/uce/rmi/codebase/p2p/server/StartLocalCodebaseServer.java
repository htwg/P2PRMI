package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.util.Arrays;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.CodebaseLocalImpl;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.http.HttpCodebaseServer;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.RmiCodebaseService;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.util.ServiceUtil;

/**
 * Starts two services for serving from a CodebaseLocalImpl. Starts a rmi
 * registry if not running to bind the codebase service.
 * 
 * @author Marco Krammer
 * 
 */
public class StartLocalCodebaseServer {

	public static void main(String[] args) throws IOException,
			AlreadyBoundException {
		String[] dirPaths = null;
		if (args.length == 0) {
			dirPaths = new String[] { "../de.htwg_konstanz.in.uce.rmi.codebase.demo.server/src/resources" };
		}

		if (args.length == 1) {
			dirPaths = new String[] { args[0] };
		}

		int port = HttpCodebaseServer.DefaultServerPort;
		if (args.length >= 2) {
			port = new Integer(args[1]).intValue();
			dirPaths = Arrays.copyOfRange(args, 1, args.length);
		}

		final ICodebase<String[]> codebase = new CodebaseLocalImpl(true);
		codebase.setSource(dirPaths);

		// HttpCodebaseServer Thread
		new Thread(new HttpCodebaseServer<String[]>(port, codebase)).start();

		// RmiCodebaseService
		RmiCodebaseService<String[]> cbService = new RmiCodebaseService<>(
				codebase, true);
		ServiceUtil.rebindService(
		/* PEER_NAME + */RmiCodebaseService.CODEBASE_SERVICE_NAME, cbService);
	}
}
