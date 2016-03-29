package org.uma.jmetal.measure.impl;

import org.uma.jmetal.measure.Measure;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.PushMeasure;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link SimplePushMeasure} is a basic implementation of {@link PushMeasure}.
 * As a {@link PushMeasure}, it is intended to be fed by the algorithm while
 * external entities should use {@link #register(MeasureListener)} to be
 * notified in real time. For the algorithm to feed it, it should provide a
 * solution and its value to {@link #push(Object, Object)}, leading to the
 * notification of the registered observers.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 */
@SuppressWarnings("serial")
public class SimplePushMeasure<Value> extends SimpleMeasure<Value> implements
		PushMeasure<Value> {

	/**
	 * The observers registered to this {@link SimplePushMeasure}.
	 */
	private final Set<MeasureListener<Value>> listeners = new HashSet<>();

	/**
	 * Create a {@link SimplePushMeasure} with a given name and a given
	 * description.
	 * 
	 * @param name
	 *            the name of the {@link Measure}
	 * @param description
	 *            the description of the {@link Measure}
	 */
	public SimplePushMeasure(String name, String description) {
		super(name, description);
	}

	/**
	 * Create a {@link SimplePushMeasure} with a given name and a
	 * <code>null</code> description.
	 * 
	 * @param name
	 *            the name of the {@link Measure}
	 */
	public SimplePushMeasure(String name) {
		super(name);
	}

	/**
	 * Create a {@link SimplePushMeasure} with the class name as its name and a
	 * <code>null</code> description.
	 */
	public SimplePushMeasure() {
		super();
	}

	@Override
	public void register(MeasureListener<Value> listener) {
		listeners.add(listener);
	}

	@Override
	public void unregister(MeasureListener<Value> listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify the observers which has registered a {@link MeasureListener}
	 * through {@link #register(MeasureListener)} about a value.
	 * 
	 * @param value
	 *            the value to send to the observers
	 */
	public void push(Value value) {
		for (MeasureListener<Value> listener : listeners) {
			listener.measureGenerated(value);
		}
	}

}
