package org.uma.jmetal.util.measurement.impl;

import org.uma.jmetal.util.measurement.Measure;
import org.uma.jmetal.util.measurement.MeasureListener;
import org.uma.jmetal.util.measurement.PullMeasure;
import org.uma.jmetal.util.measurement.PushMeasure;

/**
 * The {@link MeasureFactory} provides some useful methods to build specific
 * {@link Measure}s.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class MeasureFactory {

	public <Value> PullMeasure<Value> createPullFromPush(
			final PushMeasure<Value> push, Value initialValue) {
		final Object[] cache = { initialValue };
		final MeasureListener<Value> listener = new MeasureListener<Value>() {

			@Override
			public void measureGenerated(Value value) {
				cache[0] = value;
			}
		};
		push.register(listener);
		return new PullMeasure<Value>() {

			@Override
			public String getName() {
				return push.getName();
			}

			@Override
			public String getDescription() {
				return push.getDescription();
			}

			@SuppressWarnings("unchecked")
			@Override
			public Value get() {
				return (Value) cache[0];
			}

			@Override
			protected void finalize() throws Throwable {
				push.unregister(listener);
				super.finalize();
			}
		};
	}
}
