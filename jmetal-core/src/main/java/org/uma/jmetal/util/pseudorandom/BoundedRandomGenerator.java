package org.uma.jmetal.util.pseudorandom;

/**
 * A {@link BoundedRandomGenerator} aims to provide a random value within a
 * specific range. The range is inclusive, such that the lower bound and upper
 * bound can be generated. Because lower and upper bounds make no sense if
 * values cannot be compared, only {@link Comparable} values can be generated
 * through this kind of generator.<br>
 * <br>
 * A {@link BoundedRandomGenerator} is a {@link FunctionalInterface}. It is not
 * intended to be directly implemented by a class, but instead to request a
 * method for generating random values, usually by using lambda expressions.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 * @param <Value>
 *            The type of value to generate
 */
@FunctionalInterface
public interface BoundedRandomGenerator<Value extends Comparable<Value>> {
	/**
	 * Generate a random value within the provided range.
	 * 
	 * @param lowerBound
	 *            the minimal value which can be generated
	 * @param upperBound
	 *            the maximal value which can be generated
	 * @return the value generated
	 */
	public Value getRandomValue(Value lowerBound, Value upperBound);
}
