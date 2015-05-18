package org.uma.jmetal.measure.impl;

import java.util.WeakHashMap;

import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.PullMeasure;
import org.uma.jmetal.measure.PushMeasure;

/**
 * This measure is a facility to evaluate the time spent in
 * {@link MeasureListener}s registered in {@link PushMeasure}s. In order to
 * measure the time spent in a {@link MeasureListener}, you should wrap it by
 * calling {@link #wrapListener(MeasureListener)}. The wrapper returned should
 * be used instead of the original {@link MeasureListener} to allow the
 * {@link ListenerTimeMeasure} to account for its execution time. If you want to
 * wrap automatically all the {@link MeasureListener}s registered to a given
 * {@link PushMeasure}, you can wrap the {@link PushMeasure} through
 * {@link #wrapMeasure(PushMeasure)}: all the {@link MeasureListener}s
 * registered to the wrapper will be wrapped too. You can restart the evaluation
 * by calling {@link #reset()}.<br/>
 * <br/>
 * Notice that the time accounted is not the physical time but the processing
 * time: if several listeners run in parallel, their execution time is summed as
 * if they were running sequentially, thus you can have a measured time which is
 * superior to the physical time spent. If you want to measure the physical time
 * spent in the execution of parallel runs, you should use another way.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class ListenerTimeMeasure extends SimplePullMeasure<Long> implements
		PullMeasure<Long> {

	private Long lastReset = 0L;
	private Long time = 0L;
	private final WeakHashMap<PushMeasure<?>, PushMeasure<?>> measureCache = new WeakHashMap<PushMeasure<?>, PushMeasure<?>>();
	private final WeakHashMap<MeasureListener<?>, MeasureListener<?>> listenerCache = new WeakHashMap<MeasureListener<?>, MeasureListener<?>>();

	/**
	 * This method wrap a {@link MeasureListener} (the wrapped) into another one
	 * (the wrapper). Any notification made via the wrapper will allow to
	 * measure how much time has been spent by the wrapped to treat this
	 * notification.<br/>
	 * <br/>
	 * The wrapped listener is not changed, thus it can be reused in other
	 * {@link PushMeasure}s that we don't want to consider. If a wrapper has
	 * already been made for the given wrapped, it will be returned and no new
	 * one will be instantiated (weak references are used to not keep in memory
	 * the unused wrappers).
	 * 
	 * @param wrapped
	 *            the {@link MeasureListener} to wrap
	 * @return the {@link MeasureListener} wrapper
	 * @throw {@link IllegalArgumentException} if no listener is provided
	 */
	public <Value> MeasureListener<Value> wrapListener(
			final MeasureListener<Value> wrapped) {
		if (wrapped == null) {
			throw new IllegalArgumentException("No listener provided");
		} else {
			@SuppressWarnings("unchecked")
			MeasureListener<Value> wrapper = (MeasureListener<Value>) listenerCache
					.get(wrapped);

			if (wrapper == null) {
				wrapper = new MeasureListener<Value>() {
					@Override
					public void measureGenerated(Value value) {
						long start = System.currentTimeMillis();
						wrapped.measureGenerated(value);
						long stop = System.currentTimeMillis();
						time += stop - Math.max(start, lastReset);
					}
				};
				listenerCache.put(wrapped, wrapper);
			} else {
				// reuse existing one
			}

			return wrapper;
		}
	}

	/**
	 * This method wrap a {@link PushMeasure} (the wrapped) into another one
	 * (the wrapper). Any {@link MeasureListener} registered to the wrapper will
	 * be automatically wrapped via {@link #wrapListener(MeasureListener)}. This
	 * allows to ensure that any {@link MeasureListener} registered will be
	 * considered, independently of who registers it or when it is registered.<br/>
	 * <br/>
	 * The wrapped measure is not changed, thus it can be reused to register
	 * {@link MeasureListener}s that we don't want to consider. If a wrapper has
	 * already been made for the given wrapped, it will be returned and no new
	 * one will be instantiated (weak references are used to not keep in memory
	 * the unused wrappers).
	 * 
	 * @param wrapped
	 *            the {@link PushMeasure} to wrap
	 * @return the {@link PushMeasure} wrapper
	 * @throw {@link IllegalArgumentException} if no measure is provided
	 */
	public <Value> PushMeasure<Value> wrapMeasure(
			final PushMeasure<Value> wrapped) {
		if (wrapped == null) {
			throw new IllegalArgumentException("No measure provided");
		} else {
			@SuppressWarnings("unchecked")
			PushMeasure<Value> wrapper = (PushMeasure<Value>) measureCache
					.get(wrapped);

			if (wrapper == null) {
				wrapper = new PushMeasure<Value>() {

					@Override
					public String getName() {
						return wrapped.getName();
					}

					@Override
					public String getDescription() {
						return wrapped.getDescription();
					}

					@Override
					public void register(MeasureListener<Value> listener) {
						wrapped.register(wrapListener(listener));
					}

					@Override
					public void unregister(MeasureListener<Value> listener) {
						wrapped.unregister(wrapListener(listener));
					}
				};
				measureCache.put(wrapped, wrapper);
			} else {
				// reuse existing one
			}

			return wrapper;
		}
	}

	/**
	 * @return the time spent in the wrapped {@link MeasureListener}s
	 */
	@Override
	public Long get() {
		return time;
	}

	/**
	 * This method reset the time measured to zero. Notice that
	 * {@link MeasureListener}s which are still running will be affected
	 * consequently: their execution time will be measured from the reset time,
	 * not from their own starting time.
	 */
	public void reset() {
		time = 0L;
		lastReset = System.currentTimeMillis();
	}

}
