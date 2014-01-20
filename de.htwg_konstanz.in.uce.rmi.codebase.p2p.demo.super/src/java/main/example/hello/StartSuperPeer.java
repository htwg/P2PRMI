package example.hello;

import java.net.InetSocketAddress;
import java.util.Map;

import de.htwg_konstanz.in.uce.dht.dht_access.UceDhtVuzeAdapter;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.RmiClassLoaderSpiSimpleImpl;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.HandlerUtil;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.dhtcb.Handler;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.CodebaseLocalImpl;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.CodebaseStubServeImpl;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.http.HttpCodebaseServer;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.RmiCodebaseService;
import de.htwg_konstanz.in.uce.rmi.registry.p2p.peer.P2pRmiPeer;

/**
 * Solution via java.rmi.server.RMIClassLoaderSpi WITHOUT CustomClassLoader,
 * but with URLStreamHandler
 * 
 * @author Marco Krammer
 *
 */
public class StartSuperPeer {
	
	private static final String DEFAULT_MEDIATOR_IP = "192.168.0.10";
	private static final int DEFAULT_MEDIATOR_PORT = 9090;
	
	public static void main(String args[]) throws Exception {
    	System.setProperty("java.rmi.server.RMIClassLoaderSpi", RmiClassLoaderSpiSimpleImpl.class.getName());
		
//		System.setProperty("java.rmi.server.logCalls", "true");
//		System.setProperty("sun.rmi.server.exceptionTrace", "true");
//		
//		System.setProperty("sun.rmi.server.logLevel", "VERBOSE");
//		System.setProperty("sun.rmi.transport.logLevel", "VERBOSE");
//		System.setProperty("sun.rmi.transport.proxy.logLevel", "VERBOSE");
//		System.setProperty("sun.rmi.loader.logLevel", "VERBOSE");
//		System.setProperty("sun.rmi.dgc.logLevel", "VERBOSE");
//		
//		System.setProperty("sun.rmi.server.suppressStackTraces", "false");
//		System.setProperty("sun.rmi.server.exceptionTrace", "true");
		
		InetSocketAddress mediator = new InetSocketAddress(DEFAULT_MEDIATOR_IP, DEFAULT_MEDIATOR_PORT);
		
		boolean allow_upload = true;
		

		long start, duration;

		start = System.currentTimeMillis();
		// create the peer
		P2pRmiPeer peer = P2pRmiPeer.newInstance(new UceDhtVuzeAdapter(0));
		duration = System.currentTimeMillis() - start;
		System.out.printf("[SuperPeer.main] bootstrap duration %6d ms\n", duration);
		
    	Handler.setDht(peer.dht());
    	HandlerUtil.register(Handler.class);

		// DHT codebase
//		ICodebase<UceDht> codebaseDht = new CodebaseDhtImpl(true);
//		codebaseDht.setSource(peer.dht());
		ICodebase<String[]> localCb = new CodebaseLocalImpl(false);
		if (args.length == 0) {
			localCb.setSource(new String[]{ "../de.htwg_konstanz.in.uce.rmi.codebase.demo.server/src/resources" });
		} else {
			localCb.setSource(args);
		}
		
		// Stub cache codebase
//		Map<String, byte[]> stubMap = new HashMap<>(); TODO different map for extern stubs?
		ICodebase<Map<String,byte[]>> codebaseStubs = new CodebaseStubServeImpl(true);
		codebaseStubs.setSource(peer.registry().getStubMap());
		
		// HttpCodebaseServer with stub codebase
		new Thread(new HttpCodebaseServer<Map<String,byte[]>>(HttpCodebaseServer.DefaultServerPort, codebaseStubs)).start();
		
		// HttpCodebaseServer with class dht codebase
//		new Thread(new HttpCodebaseServer<UceDht>(HttpCodebaseServer.DefaultServerPort, codebaseDht)).start();
						
		// RMI server object serving class files from dht
		String serviceName = RmiCodebaseService.CODEBASE_SERVICE_NAME;
		start = System.currentTimeMillis();
		RmiCodebaseService<String[]> cbServiceObj = new RmiCodebaseService<>(/*codebaseDht*/ localCb, allow_upload);
		peer.server().addService(serviceName, cbServiceObj, mediator);
		duration = System.currentTimeMillis() - start;
		System.out.println("[SuperPeer.main] addService " + serviceName+ " duration " + duration + " ms\n" );
	
		// RMI server object serving stubs from a map
		serviceName = RmiCodebaseService.CODEBASE_SERVICE_NAME + ":STUB";
		start = System.currentTimeMillis();
		RmiCodebaseService<Map<String,byte[]>> cbServiceForStubsObj = new RmiCodebaseService<>(codebaseStubs, true);
		peer.server().addService(serviceName, cbServiceForStubsObj, mediator);
		duration = System.currentTimeMillis() - start;
		System.out.println("[SuperPeer.main] addService " + serviceName+ " duration " + duration + " ms\n" );
		
		// serve
		Object lock = new Object();
		synchronized (lock) {
			lock.wait();
		}	
	}

}
