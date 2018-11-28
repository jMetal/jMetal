package org.uma.jmetal.measure.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.PullMeasure;
import org.uma.jmetal.measure.PushMeasure;

import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class MeasureFactoryTest {

	@Test
	public void testCreatePullFromPush() {
		MeasureFactory factory = new MeasureFactory();
		SimplePushMeasure<Integer> push = new SimplePushMeasure<>();
		PullMeasure<Integer> pull = factory.createPullFromPush(push, null);

		assertEquals(null, (Object) pull.get());
		push.push(3);
		assertEquals(3, (Object) pull.get());
		push.push(5);
		assertEquals(5, (Object) pull.get());
		push.push(null);
		assertEquals(null, (Object) pull.get());
		push.push(-65);
		push.push(8);
		push.push(4);
		push.push(-10);
		assertEquals(-10, (Object) pull.get());
	}

  @Ignore @Test
  @SuppressWarnings("serial")
	public void testCreatePushFromPullNotifiesWithTheCorrectFrequency()
			throws InterruptedException {
		// create a pull measure
		/*
		 * The measure runs during some time and then returns a value, always
		 * different. The execution time allows to check that also heavy
		 * computations are managed well. The different result after each call
		 * ensure that it leads to a proper notification, so it is not ignored.
		 */
		final int maxExecutionTime = 5;
		PullMeasure<Integer> pull = new SimplePullMeasure<Integer>() {
			private final Random rand = new Random();
			private int count = 0;

			@Override
			public Integer get() {
				int executionTime = rand.nextInt(maxExecutionTime) + 1;
				try {
					Thread.sleep(executionTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return count++;
			}
		};

		// choose the update frequency
		/*
		 * We choose a period which is surely above the execution time to avoid
		 * extreme cases, but not too much to save as much time as possible in
		 * running the test.
		 */
		final int period = 2 * maxExecutionTime;

		// create the push measure
		MeasureFactory factory = new MeasureFactory();
		PushMeasure<Integer> push = factory.createPushFromPull(pull, period);

		// register for notifications from now
		final long start = System.currentTimeMillis();
		final LinkedList<Long> timestamps = new LinkedList<>();
		push.register(new MeasureListener<Integer>() {

			@Override
			public void measureGenerated(Integer value) {
				// store the time spent since the registration
				timestamps.add(System.currentTimeMillis() - start);
			}
		});

		// decide the number of notifications to wait for
		/*
		 * This decision is a soft one. We will wait enough to get them, but we
		 * should get something around this value. Some differences can happen
		 * due to the inexact time management of the JVM, which is by itself
		 * dependent on the ability of the CPU, which does its best.
		 */
		int iterations = 100;

		// wait for notifications to come
		Thread.sleep(iterations * period);

		// decide the sensibility in ]0;1[
		/*
		 * It gives the difference ratio accepted as reasonable. Closer to zero
		 * means more strict, while closer to one means more flexible.
		 */
		double sensibility = 0.05;

		// check we have a reasonable number of notifications
		/*
		 * This test is a preliminary one to ensure that we test the right thing
		 * at the end: if no more notifications are sent after a few, it could
		 * be that the few ones have a reasonable average time (i.e. the test
		 * passes), but we will miss the notification stop. Thus, we check that
		 * the number of notifications in average is also reasonable.
		 */
		int minIterations = (int) Math.floor(iterations * (1 - sensibility));
		int maxIterations = (int) Math.ceil(iterations * (1 + sensibility));
		assertTrue(timestamps.size() + " notifications, it should be between "
				+ minIterations + " and " + maxIterations,
				timestamps.size() >= minIterations
						&& timestamps.size() <= maxIterations);

		// check the average period is reasonable
		long average = timestamps.getLast() / timestamps.size();
		long minPeriod = (long) Math.floor(period * (1 - sensibility));
		long maxPeriod = (long) Math.ceil(period * (1 + sensibility));
		assertTrue(average + "ms, it should be between " + minPeriod + " and "
				+ maxPeriod, average >= minPeriod && average <= maxPeriod);
	}

	@Test
	@SuppressWarnings("serial")
	public void testCreatePushFromPullStopNotificationsWhenPullDestroyed()
			throws InterruptedException {
		// create a pull measure which is always different, thus leading to
		// generate a notification at every check
		PullMeasure<Integer> pull = new SimplePullMeasure<Integer>() {

			int count = 0;

			@Override
			public Integer get() {
				count++;
				return count;
			}
		};

		// create the push measure
		MeasureFactory factory = new MeasureFactory();
		final int period = 10;
		PushMeasure<Integer> push = factory.createPushFromPull(pull, period);

		// destroy the pull measure
		pull = null;
		System.gc();
		System.gc();

		// register for notifications only from now
		final LinkedList<Integer> timestamps = new LinkedList<>();
		push.register(new MeasureListener<Integer>() {

			@Override
			public void measureGenerated(Integer value) {
				timestamps.add(value);
			}
		});

		// check no notifications are coming anymore
		Thread.sleep(10 * period);
		assertEquals(0, timestamps.size());
	}

	@Test
	@SuppressWarnings("serial")
	public void testCreatePushFromPullStopNotificationsWhenPushDestroyed()
			throws InterruptedException {
		// create a pull measure which is always different, thus leading to
		// generate a notification at every check
		final boolean[] isCalled = { false };
		PullMeasure<Integer> pull = new SimplePullMeasure<Integer>() {

			int count = 0;

			@Override
			public Integer get() {
				isCalled[0] = true;
				count++;
				return count;
			}
		};

		// create the push measure
		MeasureFactory factory = new MeasureFactory();
		final int period = 10;
		factory.createPushFromPull(pull, period);
		
		// destroy the push measure
		System.gc();

		// check no periodical check are made anymore
		isCalled[0] = false;
		Thread.sleep(10 * period);
		assertFalse(isCalled[0]);
	}

	@Test
	@SuppressWarnings("serial")
	public void testCreatePushFromPullNotifiesOnlyWhenValueChanged()
			throws InterruptedException {
		// create a pull measure which changes only when we change the value of
		// the array
		final Integer[] value = { null };
		PullMeasure<Integer> pull = new SimplePullMeasure<Integer>() {
			@Override
			public Integer get() {
				return value[0];
			}
		};

		// create the push measure
		MeasureFactory factory = new MeasureFactory();
		final int period = 10;
		PushMeasure<Integer> push = factory.createPushFromPull(pull, period);

		// register for notifications from now
		final LinkedList<Integer> notified = new LinkedList<>();
		push.register(new MeasureListener<Integer>() {

			@Override
			public void measureGenerated(Integer value) {
				notified.add(value);
			}
		});

		// check no change provide no notifications
		Thread.sleep(10 * period);
		assertEquals(0, notified.size());

		// check 1 change provides 1 notification with the correct value
		value[0] = 56;
		Thread.sleep(10 * period);
		assertEquals(1, notified.size());
		assertEquals(56, (Object) notified.get(0));

		// check 1 more change provides 1 more notification with the new value
		value[0] = 43;
		Thread.sleep(10 * period);
		assertEquals(2, notified.size());
		assertEquals(56, (Object) notified.get(0));
		assertEquals(43, (Object) notified.get(1));

		// check 1 more change provides 1 more notification with the new value
		value[0] = -43;
		Thread.sleep(10 * period);
		assertEquals(3, notified.size());
		assertEquals(56, (Object) notified.get(0));
		assertEquals(43, (Object) notified.get(1));
		assertEquals(-43, (Object) notified.get(2));

		// check no change provide no more notifications
		Thread.sleep(10 * period);
		assertEquals(3, notified.size());
		assertEquals(56, (Object) notified.get(0));
		assertEquals(43, (Object) notified.get(1));
		assertEquals(-43, (Object) notified.get(2));
	}

	@Test
	public void testCreatePullsFromGettersRetrieveNothingFromEmptyObject() {
		MeasureFactory factory = new MeasureFactory();
		Map<String, PullMeasure<?>> measures = factory
				.createPullsFromGetters(new Object());
		assertTrue(measures.toString(), measures.isEmpty());
	}

	@Test
	public void testCreatePullsFromFieldsRetrieveNothingFromEmptyObject() {
		MeasureFactory factory = new MeasureFactory();
		Map<String, PullMeasure<?>> measures = factory
				.createPullsFromFields(new Object());
		assertTrue(measures.toString(), measures.isEmpty());
	}

	@SuppressWarnings("unused")
	private class Parent {
		public boolean parentPublic = true;
		protected boolean parentProtected = true;
		private boolean parentPrivate = true;

		public String getParentPublic() {
			return "parent-test";
		}

		protected String getParentProtected() {
			return "parent-protected";
		}

		private String getParentPrivate() {
			return "parent-private";
		}
	}

	@SuppressWarnings("unused")
	private class Child extends Parent {
		public boolean childPublic = false;
		protected boolean childProtected = false;
		private boolean childPrivate = false;

		public String getChildPublic() {
			return "child-test";
		}

		protected String getChildProtected() {
			return "child-protected";
		}

		private String getChildPrivate() {
			return "child-private";
		}
	}

	@Test
	public void testCreatePullsFromGettersRetrieveAllInstantiatedPublicGetters() {
		MeasureFactory factory = new MeasureFactory();
		Map<String, PullMeasure<?>> measures = factory
				.createPullsFromGetters(new Child());
		assertTrue(measures.toString(), measures.containsKey("ChildPublic"));
		assertEquals("child-test", measures.get("ChildPublic").get());
	}

	@Test
	public void testCreatePullsFromFieldsRetrieveAllInstantiatedPublicFields() {
		MeasureFactory factory = new MeasureFactory();
		Map<String, PullMeasure<?>> measures = factory
				.createPullsFromFields(new Child());
		assertTrue(measures.toString(), measures.containsKey("childPublic"));
		assertEquals(false, measures.get("childPublic").get());
	}

	@Test
	public void testCreatePullsFromGettersRetrieveAllInheritedPublicGetters() {
		MeasureFactory factory = new MeasureFactory();
		Map<String, PullMeasure<?>> measures = factory
				.createPullsFromGetters(new Child());
		assertTrue(measures.toString(), measures.containsKey("ParentPublic"));
		assertEquals("parent-test", measures.get("ParentPublic").get());
	}

	@Test
	public void testCreatePullsFromFieldsRetrieveAllInheritedPublicFields() {
		MeasureFactory factory = new MeasureFactory();
		Map<String, PullMeasure<?>> measures = factory
				.createPullsFromFields(new Child());
		assertTrue(measures.toString(), measures.containsKey("parentPublic"));
		assertEquals(true, measures.get("parentPublic").get());
	}

	@Test
	public void testCreatePullsFromGettersRetrieveNoInstantiatedProtectedNorPrivateGetter() {
		MeasureFactory factory = new MeasureFactory();
		Map<String, PullMeasure<?>> measures = factory
				.createPullsFromGetters(new Child());
		assertFalse(measures.toString(), measures.containsKey("ChildProtected"));
		assertFalse(measures.toString(), measures.containsKey("ChildPrivate"));
	}

	@Test
	public void testCreatePullsFromFieldsRetrieveNoInstantiatedProtectedNorPrivateField() {
		MeasureFactory factory = new MeasureFactory();
		Map<String, PullMeasure<?>> measures = factory
				.createPullsFromFields(new Child());
		assertFalse(measures.toString(), measures.containsKey("childProtected"));
		assertFalse(measures.toString(), measures.containsKey("childPrivate"));
	}

	@Test
	public void testCreatePullsFromGettersRetrieveNoInheritedProtectedNorPrivateGetter() {
		MeasureFactory factory = new MeasureFactory();
		Map<String, PullMeasure<?>> measures = factory
				.createPullsFromGetters(new Child());
		assertFalse(measures.toString(),
				measures.containsKey("ParentProtected"));
		assertFalse(measures.toString(), measures.containsKey("ParentPrivate"));
	}

	@Test
	public void testCreatePullsFromFieldsRetrieveNoInheritedProtectedNorPrivateField() {
		MeasureFactory factory = new MeasureFactory();
		Map<String, PullMeasure<?>> measures = factory
				.createPullsFromFields(new Child());
		assertFalse(measures.toString(),
				measures.containsKey("parentProtected"));
		assertFalse(measures.toString(), measures.containsKey("parentPrivate"));
	}

}
