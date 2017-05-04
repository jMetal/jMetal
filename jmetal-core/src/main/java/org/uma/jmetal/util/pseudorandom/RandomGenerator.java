package org.uma.jmetal.util.pseudorandom;

/**
 * A {@link RandomGenerator} aims to provide a random value of a given type. Any
 * value of this type can be generated.<br>
 * <br>
 * A {@link RandomGenerator} is a {@link FunctionalInterface}. It is not
 * intended to be directly implemented by a class, but instead to request a
 * method for generating random values, usually by using lambda expressions.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 * @param <Value>
 *            The type of value to generate
 */
@FunctionalInterface
public interface RandomGenerator<Value> {
	/**
	 * Generate a random value.
	 * 
	 * @return the value generated
	 */
	public Value getRandomValue();
}
