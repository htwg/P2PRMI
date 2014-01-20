package example.hello;

import example.hello.IGreeting;


public class GreetingImplEN implements IGreeting {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GreetingImplEN() {
		super();
	}
	
	@Override
	public String greet() {
		return "Welcome!";
	}
}
