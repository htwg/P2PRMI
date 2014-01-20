package example.hello;

import java.io.Serializable;

/**
 * codebase classes need to implement {@link Serializable}
 * 
 * @author Marco Krammer
 *
 */
public interface IGreeting extends Serializable {
	
	/**
	 * Method returns a greeting text
	 * @return
	 */
    String greet();
}