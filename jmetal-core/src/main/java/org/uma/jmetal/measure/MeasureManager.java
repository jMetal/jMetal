package org.uma.jmetal.measure;

import java.util.Collection;

/**
 * A {@link MeasureManager} aims at managing a set of {@link Measure}s.
 * Typically, a {@link Measurable} entity would create a single
 * {@link MeasureManager} to store all the {@link Measure}s it would like to
 * support, each of them being identified by a key.<br/>
 * <br/>
 * Because a {@link Measure} can be whether a {@link PullMeasure} or a
 * {@link PushMeasure}, the two methods {@link #getPullMeasure(Object)} and
 * {@link #getPushMeasure(Object)} are provided. It could be that a single
 * {@link Measure} is also available through both (via a single instance
 * implementing both interfaces or two different instances implementing each
 * interface), leading to have both methods returning a non-<code>null</code>
 * value for the same key.
 * 
 * @author Created by Antonio J. Nebro on 21/10/14 based on the ideas of
 *         Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public interface MeasureManager {
	/**
	 * This method should return all the keys identifying the {@link Measure}s
	 * managed by this {@link MeasureManager}. More precisely, if
	 * {@link #getPullMeasure(Object)} or {@link #getPushMeasure(Object)}
	 * returns a non-<code>null</code> value for a given key, then this key
	 * should be returned by {@link #getMeasureKeys()}. However, it is not
	 * because a key is returned by {@link #getMeasureKeys()} that it
	 * necessarily has a {@link PullMeasure} or a {@link PushMeasure} returned
	 * by this {@link MeasureManager}.
	 * 
	 * @return the set of keys identifying the managed {@link Measure}s
	 */
	public Collection<Object> getMeasureKeys();

	/**
	 * 
	 * @param key
	 *            the key of the {@link Measure}
	 * @return the {@link PullMeasure} identified by this key
	 */
	public <T> PullMeasure<T> getPullMeasure(Object key);

	/**
	 * 
	 * @param key
	 *            the key of the {@link Measure}
	 * @return the {@link PushMeasure} identified by this key
	 */
	public <T> PushMeasure<T> getPushMeasure(Object key);
}
