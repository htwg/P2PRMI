package example.hello;

import example.hello.IGreeting;


public class GreetingImplDE implements IGreeting {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GreetingImplDE() {
		super();
	}
	
	@Override
	public String greet() {
		return "Willkommen!";
	}
}
