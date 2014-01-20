package example.hello;

import java.io.File;
import java.net.InetSocketAddress;

import com.google.common.io.Files;


//import de.htwg_konstanz.in.uce.dht.dht_access.UceDht;
import de.htwg_konstanz.in.uce.dht.dht_access.UceDhtVuzeAdapter;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.RmiClassLoaderSpiSimpleImpl;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.CodebaseLocalImpl;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.IRemoteCodebaseService;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.RmiCodebaseService;
import de.htwg_konstanz.in.uce.rmi.registry.p2p.peer.P2pRmiPeer;
//import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.dhtcb.Handler;

/**
 * Solution via java.rmi.server.RMIClassLoaderSpi without CustomClassLoader
 * but with URLStreamHandler
 * 
 * @author Marco Krammer
 * 
 * TODO 
 *
 */
public class StartServerPeer {

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
		long start, duration;

		start = System.currentTimeMillis();
		// create the peer
		P2pRmiPeer peer = P2pRmiPeer.newInstance(new UceDhtVuzeAdapter(0));
		duration = System.currentTimeMillis() - start;
		System.out.printf("[PeerServer.main] bootstrap duration %6d ms\n", duration);
    	

		String jarKey = "WelcomeService.jar";
		
		// use super-peer service
		String serviceName = /*PEER_NAME + */ RmiCodebaseService.CODEBASE_SERVICE_NAME;
		System.out.println("[PeerServer.main] Checking Super-Peer: Looking up service '" + serviceName + "' ...");
		start = System.currentTimeMillis();
		IRemoteCodebaseService remoteCbService = (IRemoteCodebaseService) peer.registry().lookup(serviceName);
		remoteCbService.put(jarKey, Files.toByteArray(new File("../de.htwg_konstanz.in.uce.rmi.codebase.demo.server/src/resources/WelcomeService.jar")));
		duration = System.currentTimeMillis() - start;
		System.out.println("[PeerServer.main] exported " + jarKey + " duration " + duration + " ms\n");
		System.out.println("[PeerServer.main File exported!");
    	
    	// codebase source
		ICodebase<String[]> localCb = new CodebaseLocalImpl(true);
		if (args.length == 0) {
			localCb.setSource(new String[]{ "../de.htwg_konstanz.in.uce.rmi.codebase.demo.server/src/resources" });
		} else {
			localCb.setSource(args);
		}
		
//		ICodebase<UceDht> cbDht = new CodebaseDhtImpl(true);
//		cbDht.setSource(peer.dht());
//		cbDht.put(jarKey, toByteArray(new File("../RmiServer/res/WelcomeService.jar")));
		
		// provide own class service
//		System.out.println("[PeerServer.main] Exporting class service ...");
//		start = System.currentTimeMillis();
//		RmiClassService<String[]> cbService = new RmiClassService<>(localCb, false);
//		peer.server().addService(RmiClassService.CLASS_FILE_SERVICE_NAME, cbService, mediator);
//		System.out.println("[PeerServer.main] addService " + RmiClassService.CLASS_FILE_SERVICE_NAME + " duration " + duration + " ms\n" ); //TODO stub content
		
		String codebase = "rmicb://" + jarKey; //"http://192.168.0.101:2001/", "dhtcb://" + jarKey;
		System.setProperty("java.rmi.server.codebase", codebase);

		// create remote stuff and add service
		String welcomeServiceName = "WelcomeService";	
		IWelcomeService instance = new WelcomeServiceImpl();
		start = System.currentTimeMillis();
		IWelcomeService stub = peer.server().addService(welcomeServiceName, instance, mediator);
		duration = System.currentTimeMillis() - start;
		System.out.println("[PeerServer.main] addService " + welcomeServiceName + " duration " + duration + " ms\n" + stub);

		// export the stub using the super node's codebase service
		serviceName = /*PEER_NAME + */ RmiCodebaseService.CODEBASE_SERVICE_NAME + ":STUB";
		System.out.println("[PeerServer.main] Checking Super-Peer: Looking up service '" + serviceName + "' ...");
		start = System.currentTimeMillis();
		IRemoteCodebaseService remoteCbStubService = (IRemoteCodebaseService) peer.registry().lookup(serviceName);
		remoteCbStubService.put(welcomeServiceName, peer.registry().getStubMap().get(welcomeServiceName));
		duration = System.currentTimeMillis() - start;
		System.out.println("[PeerServer.main] exported stub for " + welcomeServiceName + " duration " + duration + " ms\n");
		System.out.println("[PeerServer.main Ready!");
		
		// serve
		Object lock = new Object();
		synchronized (lock) {
			lock.wait();
		}	
	}
	
}
