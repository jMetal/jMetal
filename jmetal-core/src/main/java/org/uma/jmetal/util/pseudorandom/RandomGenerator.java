package org.uma.jmetal.util.pseudorandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

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

	/**
	 * Create a {@link RandomGenerator} over a {@link Collection} based on a
	 * random selector.
	 * 
	 * @param indexSelector
	 *            the random selector
	 * @param values
	 *            the values to return
	 * @return a {@link RandomGenerator} on the provided values
	 */
	static <T> RandomGenerator<T> forCollection(BoundedRandomGenerator<Integer> indexSelector,
			Collection<T> values) {
		ArrayList<T> list = new ArrayList<>(values);
		return () -> list.get(indexSelector.getRandomValue(0, values.size() - 1));
	}

	/**
	 * Create a {@link RandomGenerator} over an array based on a random
	 * selector.
	 * 
	 * @param indexSelector
	 *            the random selector
	 * @param values
	 *            the values to return
	 * @return a {@link RandomGenerator} on the provided values
	 */
	@SafeVarargs
	static <T> RandomGenerator<T> forArray(BoundedRandomGenerator<Integer> indexSelector, T... values) {
		return forCollection(indexSelector, Arrays.asList(values));
	}

	/**
	 * Create a {@link RandomGenerator} over {@link Enum} values based on a
	 * random selector.
	 * 
	 * @param indexSelector
	 *            the random selector
	 * @param enumClass
	 *            the {@link Enum} to cover
	 * @return a {@link RandomGenerator} on the {@link Enum} values
	 */
	static <T extends Enum<T>> RandomGenerator<T> forEnum(BoundedRandomGenerator<Integer> indexSelector,
			Class<T> enumClass) {
		return forArray(indexSelector, enumClass.getEnumConstants());
	}

	/**
	 * Reduce a {@link RandomGenerator} range. The returned
	 * {@link RandomGenerator} uses the provided one to generate random values,
	 * but regenerate them if they do not pass the filter. Consequently, the
	 * initial {@link RandomGenerator} may be called several times o generate a
	 * single value. The impact on performance depends on the part of the
	 * distribution which is filtered out: if a significant part of the
	 * distribution is rejected, it might be more interesting to create a
	 * dedicated {@link RandomGenerator}.
	 * 
	 * @param generator
	 *            the {@link RandomGenerator} to filter
	 * @param filter
	 *            the filter to pass to be an acceptable value
	 * @return a {@link RandomGenerator} which provides only acceptable values
	 */
	static <T> RandomGenerator<T> filter(RandomGenerator<T> generator, Predicate<T> filter) {
		return new RandomGenerator<T>() {

			@Override
			public T getRandomValue() {
				T value;
				do {
					value = generator.getRandomValue();
				} while (!filter.test(value));
				return value;
			}
		};
	}
}
