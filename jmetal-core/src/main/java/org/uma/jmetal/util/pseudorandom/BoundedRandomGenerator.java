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
	Value getRandomValue(Value lowerBound, Value upperBound);

	/**
	 * Create a {@link BoundedRandomGenerator} which generates {@link Integer}
	 * values from a {@link BoundedRandomGenerator} which generate
	 * {@link Double} values. The distribution is preserved.
	 * 
	 * @param doubleGenerator
	 *            {@link BoundedRandomGenerator} which generates {@link Double}
	 *            values
	 * @return {@link BoundedRandomGenerator} which generates {@link Integer}
	 *         values based on the provided generator
	 */
	static BoundedRandomGenerator<Integer> fromDoubleToInteger(BoundedRandomGenerator<Double> doubleGenerator) {
		return (min, max) -> (int) Math.floor(doubleGenerator.getRandomValue(min.doubleValue(), max.doubleValue() + 1));
	}

	/**
	 * Create a {@link BoundedRandomGenerator} which generates {@link Integer}
	 * values from a {@link BoundedRandomGenerator} which generate
	 * {@link Double} values between 0 and 1 (inclusive or exclusive). The
	 * distribution is preserved.
	 * 
	 * @param doubleGenerator
	 *            {@link RandomGenerator} which generates {@link Double} values
	 * @return {@link BoundedRandomGenerator} which generates {@link Integer}
	 *         values based on the provided generator
	 */
	static BoundedRandomGenerator<Integer> fromDoubleToInteger(RandomGenerator<Double> doubleGenerator) {
		return fromDoubleToInteger(bound(doubleGenerator));
	}

	/**
	 * Create a {@link BoundedRandomGenerator} from a {@link RandomGenerator}
	 * which generate {@link Double} values between 0 and 1 (inclusive or
	 * exclusive). The distribution is preserved.
	 * 
	 * @param unboundedGenerator
	 *            {@link RandomGenerator} which generates values between 0 and 1
	 * @return {@link BoundedRandomGenerator} which generates {@link Double}
	 *         values based on the provided generator
	 */
	static BoundedRandomGenerator<Double> bound(RandomGenerator<Double> unboundedGenerator) {
		return (min, max) -> {
			/*
			 * The modulo replaces the value 1 by 0. Because it is a generator
			 * of values between 0 and 1, whether this case does not happen
			 * because the values are in [0;1[, whether it happens because the
			 * values are in [0;1]. Then, because it is a generator of double
			 * values, the case equal to 1 should have a negligible probability
			 * to occur, such that replacing it by any other value should not
			 * significantly impact the distribution, thus making it equivalent
			 * to [0;1[ in any case.
			 */
			Double doubleValue = unboundedGenerator.getRandomValue() % 1;

			return doubleValue * (max - min) + min;
		};
	}
}
