package org.uma.jmetal.measure;

/**
 * A {@link PullMeasure} is a {@link Measure} from which the {@link Value} can
 * be accessed on demand through the {@link #get()} method. As such, a
 * {@link PullMeasure} should ensure that its current {@link Value} is always
 * available or generated before to be returned by {@link #get()}.
 * 
 * @author Created by Antonio J. Nebro on 21/10/14 based on the ideas of
 *         Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 *            the type of value the {@link PullMeasure} can provide
 */
public interface PullMeasure<Value> extends Measure<Value> {
	/**
	 * 
	 * @return the current {@link Value} of the {@link Measure}
	 */
	public Value get();
}
