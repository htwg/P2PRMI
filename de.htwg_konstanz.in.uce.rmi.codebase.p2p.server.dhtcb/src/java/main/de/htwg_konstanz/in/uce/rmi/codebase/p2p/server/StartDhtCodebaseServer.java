package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server;

import java.io.IOException;
import java.rmi.AlreadyBoundException;

import com.aelitis.azureus.core.dht.transport.DHTTransportException;

import de.htwg_konstanz.in.uce.dht.dht_access.UceDht;
import de.htwg_konstanz.in.uce.dht.dht_access.UceDhtVuzeAdapter;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.CodebaseDhtImpl;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.http.HttpCodebaseServer;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.RmiCodebaseService;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.util.ServiceUtil;

/**
 * Starts two services for serving from a CodebaseDhtImpl. Starts a RMI registry
 * if not running to bind the codebase service.
 * 
 * @author Marco Krammer
 * 
 */
public class StartDhtCodebaseServer {

	public static void main(String[] args) throws IOException,
			DHTTransportException, AlreadyBoundException {
		UceDht dht = new UceDhtVuzeAdapter(0);
		dht.bootstrap();

		int port = HttpCodebaseServer.DefaultServerPort;

		if (args.length == 1) {
			port = new Integer(args[0]).intValue();
		}

		// DHT Codebase
		ICodebase<UceDht> codebase = new CodebaseDhtImpl(true);
		codebase.setSource(dht);

		// HttpCodebaseServer Thread
		new Thread(new HttpCodebaseServer<UceDht>(port, codebase)).start();

		// RmiCodebaseService object
		RmiCodebaseService<UceDht> cbService = new RmiCodebaseService<UceDht>(
				codebase, true);
		ServiceUtil.rebindService(
		/* PEER_NAME + */RmiCodebaseService.CODEBASE_SERVICE_NAME, cbService);
	}
}
