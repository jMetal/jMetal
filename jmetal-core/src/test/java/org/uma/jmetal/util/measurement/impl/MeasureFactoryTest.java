package org.uma.jmetal.util.measurement.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.uma.jmetal.util.measurement.PullMeasure;

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

}
