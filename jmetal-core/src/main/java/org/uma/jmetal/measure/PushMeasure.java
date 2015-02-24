package org.uma.jmetal.measure;

/**
 * A {@link PushMeasure} is a {@link Measure} which provides its {@link Value}
 * through notifications. As such, any observer on a {@link PushMeasure} should
 * register a {@link MeasureListener} through {@link #register(MeasureListener)}
 * to specify what to do with the {@link Value} once it is received.
 * 
 * @author Created by Antonio J. Nebro on 21/10/14 based on the ideas of
 *         Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 *            the type of value the {@link PushMeasure} can provide
 */
public interface PushMeasure<Value> extends Measure<Value> {
	/**
	 * Register a {@link MeasureListener} to use the {@link Value}s of the
	 * {@link PushMeasure} when they are generated.
	 * 
	 * @param listener
	 *            the {@link MeasureListener} to register
	 */
	public void register(MeasureListener<Value> listener);

	/**
	 * Unregister a {@link MeasureListener} registered with
	 * {@link #register(MeasureListener)} to stop receiving the notifications of
	 * the {@link PushMeasure}.
	 * 
	 * @param listener
	 *            the {@link MeasureListener} to unregister
	 */
	public void unregister(MeasureListener<Value> listener);
}
