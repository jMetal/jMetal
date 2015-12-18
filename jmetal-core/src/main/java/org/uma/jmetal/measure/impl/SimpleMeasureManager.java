package org.uma.jmetal.measure.impl;

import org.uma.jmetal.measure.Measure;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.PullMeasure;
import org.uma.jmetal.measure.PushMeasure;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This {@link SimpleMeasureManager} provides a basic implementation to manage a
 * collection of {@link Measure}s. One can use the setXxxMeasure() methods to
 * configure the {@link MeasureManager} with the finest granularity, or exploit
 * the centralized {@link #setMeasure(Object, Measure)} to register a
 * {@link Measure} depending on the interfaces it implements, or even use the
 * massive {@link #setAllMeasures(Map)} to register a set of {@link Measure}s at
 * once. The corresponding removeXxx methods are also available for each case.
 * However, the only way to access a {@link Measure} is through the finest
 * granularity with {@link #getPullMeasure(Object)} and
 * {@link #getPushMeasure(Object)}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class SimpleMeasureManager implements MeasureManager {

	/**
	 * The {@link PullMeasure}s registered to this {@link SimpleMeasureManager}.
	 */
	private Map<Object, PullMeasure<?>> pullers = new HashMap<>();
	/**
	 * The {@link PushMeasure}s registered to this {@link SimpleMeasureManager}.
	 */
	private Map<Object, PushMeasure<?>> pushers = new HashMap<>();

	/**
	 * Provides the keys of all the {@link Measure}s which are supported by this
	 * {@link SimpleMeasureManager}. If a key is provided, then at least one
	 * version is available through {@link #getPullMeasure(Object)} or
	 * {@link #getPushMeasure(Object)}.
	 */
	@Override
	public Collection<Object> getMeasureKeys() {
		HashSet<Object> keys = new HashSet<>();
		keys.addAll(pullers.keySet());
		keys.addAll(pushers.keySet());
		return keys;
	}

	/**
	 * 
	 * @param key
	 *            the key of the {@link Measure}
	 * @param measure
	 *            the {@link PullMeasure} to register
	 */
	public void setPullMeasure(Object key, PullMeasure<?> measure) {
		if (measure == null) {
			removePullMeasure(key);
		} else {
			pullers.put(key, measure);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> PullMeasure<T> getPullMeasure(Object key) {
		return (PullMeasure<T>) pullers.get(key);
	}

	/**
	 * 
	 * @param key
	 *            the key of the {@link PullMeasure} to remove
	 */
	public void removePullMeasure(Object key) {
		pullers.remove(key);
	}

	/**
	 * 
	 * @param key
	 *            the key of the {@link Measure}
	 * @param measure
	 *            the {@link PushMeasure} to register
	 */
	public void setPushMeasure(Object key, PushMeasure<?> measure) {
		if (measure == null) {
			removePushMeasure(key);
		} else {
			pushers.put(key, measure);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> PushMeasure<T> getPushMeasure(Object key) {
		return (PushMeasure<T>) pushers.get(key);
	}

	/**
	 * 
	 * @param key
	 *            the key of the {@link PushMeasure} to remove
	 */
	public void removePushMeasure(Object key) {
		pushers.remove(key);
	}

	/**
	 * This method call {@link #setPullMeasure(Object, PullMeasure)} or
	 * {@link #setPushMeasure(Object, PushMeasure)} depending on the interfaces
	 * implemented by the {@link Measure} given in argument. If both interfaces
	 * are implemented, both methods are called, allowing to register all the
	 * aspects of the {@link Measure} in one call.
	 * 
	 * @param key
	 *            the key of the {@link Measure}
	 * @param measure
	 *            the {@link Measure} to register
	 */
	public void setMeasure(Object key, Measure<?> measure) {
		if (measure instanceof PullMeasure) {
			setPullMeasure(key, (PullMeasure<?>) measure);
		}
		if (measure instanceof PushMeasure) {
			setPushMeasure(key, (PushMeasure<?>) measure);
		}
	}

	/**
	 * This method removes an entire {@link Measure}, meaning that if both a
	 * {@link PullMeasure} and a {@link PushMeasure} are registered for this
	 * key, then both are removed.
	 * 
	 * @param key
	 *            the key of the {@link Measure} to remove
	 */
	public void removeMeasure(Object key) {
		removePullMeasure(key);
		removePushMeasure(key);
	}

	/**
	 * Massive equivalent of {@link #setMeasure(Object, Measure)}.
	 * 
	 * @param measures
	 *            the {@link Measure}s to register with their corresponding keys
	 */
	public void setAllMeasures(
			Map<? extends Object, ? extends Measure<?>> measures) {
		for (Entry<? extends Object, ? extends Measure<?>> entry : measures
				.entrySet()) {
			setMeasure(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Massive equivalent to {@link #removeMeasure(Object)}.
	 * 
	 * @param keys
	 *            the keys of the {@link Measure}s to remove
	 */
	public void removeAllMeasures(Iterable<? extends Object> keys) {
		for (Object key : keys) {
			removeMeasure(key);
		}
	}
}
