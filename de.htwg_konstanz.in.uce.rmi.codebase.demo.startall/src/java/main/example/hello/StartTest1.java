package example.hello;

import java.io.IOException;
import java.rmi.AlreadyBoundException;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.StartLocalCodebaseServer;

/**
 * This class starts all components (codebase, RMI server and client) for
 * standard RMI demo
 * 
 * @author Marco Krammer
 * 
 */
public class StartTest1 {

	public static void main(String[] args) throws AlreadyBoundException,
			InterruptedException, IOException {

		StartLocalCodebaseServer.main(new String[0]);

		StartServerNoWait.main(new String[0]);

		StartClient.main(new String[0]);

		System.exit(0);

	}

}