package example.hello;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import example.hello.IGreeting;
import example.hello.IWelcomeService;

public class WelcomeServiceImpl implements IWelcomeService {

	public final String serviceName = "WelcomeService";
	public final String codebase = "http://localhost:2001";
	
	public WelcomeServiceImpl() {
		super();
	}
	
	@Override
	public List<IGreeting> getAllGreetings() throws RemoteException {
		List<IGreeting> list = new ArrayList<>();
		list.add(new GreetingImplEN());
		list.add(new GreetingImplDE());
		list.add(new GreetingImplFR());
		return list;
	}

}
