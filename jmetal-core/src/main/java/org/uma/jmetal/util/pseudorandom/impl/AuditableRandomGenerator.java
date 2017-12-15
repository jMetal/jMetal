package org.uma.jmetal.util.pseudorandom.impl;

import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * An {@link AuditableRandomGenerator} is a {@link PseudoRandomGenerator} which can be audited
 * to know when a random generation method is called.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 */
@SuppressWarnings("serial")
public class AuditableRandomGenerator implements PseudoRandomGenerator {

	public static enum RandomMethod {
		BOUNDED_INT, BOUNDED_DOUBLE, DOUBLE
	}

	public static class Bounds {
		final Number lower;
		final Number upper;

		public Bounds(Number lower, Number upper) {
			this.lower = Objects.requireNonNull(lower, "No lower bound provided");
			this.upper = Objects.requireNonNull(upper, "No upper bound provided");
		}
	}

	private final PseudoRandomGenerator generator;
	private final Set<Consumer<Audit>> listeners = new HashSet<Consumer<Audit>>();

	public AuditableRandomGenerator(PseudoRandomGenerator generator) {
		this.generator = Objects.requireNonNull(generator, "No generator provided");
	}

	public static class Audit {
		private final RandomMethod method;
		private final Optional<Bounds> bounds;
		private final Number result;

		public Audit(RandomMethod method, Bounds bounds, Number result) {
			this.method = Objects.requireNonNull(method, "No method provided");
			this.bounds = Optional.ofNullable(bounds);
			this.result = Objects.requireNonNull(result, "No result provided");
		}

		public RandomMethod getMethod() {
			return method;
		}

		public Optional<Bounds> getBounds() {
			return bounds;
		}

		public Number getResult() {
			return result;
		}
	}

	public void addListener(Consumer<Audit> listener) {
		listeners.add(listener);
	}

	public void removeListener(Consumer<Audit> listener) {
		listeners.remove(listener);
	}

	private void notifies(Audit audit) {
		for (Consumer<Audit> listener : listeners) {
			listener.accept(audit);
		}
	}

	@Override
	public int nextInt(int lowerBound, int upperBound) {
		int result = generator.nextInt(lowerBound, upperBound);
		notifies(new Audit(RandomMethod.BOUNDED_INT, new Bounds(lowerBound, upperBound), result));
		return result;
	}

	@Override
	public double nextDouble(double lowerBound, double upperBound) {
		double result = generator.nextDouble(lowerBound, upperBound);
		notifies(new Audit(RandomMethod.BOUNDED_DOUBLE, new Bounds(lowerBound, upperBound), result));
		return result;
	}

	@Override
	public double nextDouble() {
		double result = generator.nextDouble();
		notifies(new Audit(RandomMethod.DOUBLE, null, result));
		return result;
	}

	@Override
	public void setSeed(long seed) {
		generator.setSeed(seed);
	}

	@Override
	public long getSeed() {
		return generator.getSeed();
	}

	@Override
	public String getName() {
		return generator.getName();
	}

}
