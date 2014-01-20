package example.hello;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.util.List;

import com.aelitis.azureus.core.dht.transport.DHTTransportException;

import de.htwg_konstanz.in.uce.dht.dht_access.UceDhtVuzeAdapter;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.RmiClassLoaderSpiSimpleImpl;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.HandlerUtil;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.rmicb.Handler;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.util.LogHandler;
//import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.dhtcb.Handler;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.IRemoteCodebaseService;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.RmiCodebaseService;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.util.ClassUtil;
import de.htwg_konstanz.in.uce.rmi.registry.p2p.peer.P2pRmiPeer;

/**
 * Solution via java.rmi.server.RMIClassLoaderSpi WITHOUT CustomClassLoader,
 * but with URLStreamHandler
 * 
 * @author Marco Krammer
 *
 */
public class StartClientPeer {

    public static void main(String[] args) throws SecurityException, DHTTransportException, 
    	InterruptedException, ClassNotFoundException, IOException, NotBoundException {		
		
    	System.setProperty("java.security.policy", StartClientPeer.class
    			.getClassLoader().getResource("allow_all.policy").toExternalForm());
		System.setSecurityManager(new RMISecurityManager());											
  
		//if false uses class' annotation, else codebase property!
		System.setProperty("java.rmi.server.useCodebaseOnly", "false");
    	System.setProperty("java.rmi.server.RMIClassLoaderSpi", RmiClassLoaderSpiSimpleImpl.class.getName());
    	
		long start, duration;

		// create the peer
		start = System.currentTimeMillis();
		P2pRmiPeer peer = P2pRmiPeer.newInstance(new UceDhtVuzeAdapter(0));
		duration = System.currentTimeMillis() - start;
		System.out.printf("[ClientPeer.main] bootstrap duration %6d ms\n", duration);
    	    	
		// get class service
		String serviceName = RmiCodebaseService.CODEBASE_SERVICE_NAME;
		System.out.println("[ClientPeer.main]  Looking up service '" + serviceName + "' ...");
        IRemoteCodebaseService cbService = (IRemoteCodebaseService) peer.registry().lookup(serviceName);
		duration = System.currentTimeMillis() - start;
		System.out.printf("[ClientPeer.main] Received stub after %5d ms\n", duration);
		
		// Handler.setDht(peer.dht());
		Handler.setCbService(cbService);
    	HandlerUtil.register(Handler.class);
    	
    	// get the demo service
    	serviceName = "WelcomeService";
		System.out.println("[ClientPeer.main] Looking up service '" + serviceName + "' ...");
		start = System.currentTimeMillis();
		IWelcomeService service = (IWelcomeService) peer.registry().lookup(serviceName);
		duration = System.currentTimeMillis() - start;
		System.out.printf("[ClientPeer.main] Received stub after %5d ms\n", duration);
		ClassUtil.printAnnotation(service);

		// call the remote getAllGreetings method and 
		// the runtime retrieves the class definitions from the codebase
		List<? extends IGreeting> greetingImplList = service.getAllGreetings();
		for (IGreeting greetingImpl : greetingImplList) {
			ClassLoader loader = greetingImpl.getClass().getClassLoader();
			Class<?>[] interfaces = new Class<?>[]{ IGreeting.class };
			LogHandler logHandler = new LogHandler(greetingImpl);

			IGreeting greetingProxy = (IGreeting) Proxy.newProxyInstance(loader, interfaces, logHandler);
			System.out.println(greetingProxy.greet());
		}
		
		System.out.println("ClientPeer.main]: done.");
    }
    
	
}