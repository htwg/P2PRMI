package example.hello;

import example.hello.IGreeting;

public class GreetingImplFR implements IGreeting {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String greet() {
		return "Bienvenue!";
	}

}
