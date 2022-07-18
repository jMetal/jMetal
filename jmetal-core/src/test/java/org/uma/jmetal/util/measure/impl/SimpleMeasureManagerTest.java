package org.uma.jmetal.util.measure.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.uma.jmetal.util.measure.Measure;

public class SimpleMeasureManagerTest {

	@Test
	public void testStartEmpty() {
		var manager = new SimpleMeasureManager();
		assertEquals(0, manager.getMeasureKeys().size());
	}

	@Test
	public void testSetGetPullMeasure() {
		var manager = new SimpleMeasureManager();
		var measure1 = new CountingMeasure();
		var measure2 = new CountingMeasure();
		var measure3 = new CountingMeasure();

		manager.setPullMeasure(1, measure1);
		assertEquals(measure1, manager.getPullMeasure(1));

		manager.setPullMeasure(2, measure2);
		assertEquals(measure2, manager.getPullMeasure(2));

		manager.setPullMeasure(1, measure3);
		assertEquals(measure3, manager.getPullMeasure(1));
	}

	@Test
	public void testSetGetPushMeasure() {
		var manager = new SimpleMeasureManager();
		var measure1 = new CountingMeasure();
		var measure2 = new CountingMeasure();
		var measure3 = new CountingMeasure();

		manager.setPushMeasure(1, measure1);
		assertEquals(measure1, manager.getPushMeasure(1));

		manager.setPushMeasure(2, measure2);
		assertEquals(measure2, manager.getPushMeasure(2));

		manager.setPushMeasure(1, measure3);
		assertEquals(measure3, manager.getPushMeasure(1));
	}

	@Test
	public void testSetMeasureGetBoth() {
		var manager = new SimpleMeasureManager();
		var measure = new CountingMeasure();

		manager.setMeasure(1, measure);
		assertEquals(measure, manager.getPullMeasure(1));
		assertEquals(measure, manager.getPushMeasure(1));
	}

	@Test
	public void testAddMeasureAddKey() {
		var manager = new SimpleMeasureManager();
		var measure = new CountingMeasure();

		manager.setPullMeasure(1, measure);
		assertEquals(1, manager.getMeasureKeys().size());
		assertTrue(manager.getMeasureKeys().contains(1));

		manager.setPushMeasure(2, measure);
		assertEquals(2, manager.getMeasureKeys().size());
		assertTrue(manager.getMeasureKeys().contains(1));
		assertTrue(manager.getMeasureKeys().contains(2));

		manager.setMeasure(3, measure);
		assertEquals(3, manager.getMeasureKeys().size());
		assertTrue(manager.getMeasureKeys().contains(1));
		assertTrue(manager.getMeasureKeys().contains(2));
		assertTrue(manager.getMeasureKeys().contains(3));
	}

	@Test
	public void testRemoveBothMeasuresRemoveKey() {
		var manager = new SimpleMeasureManager();
		var measure = new CountingMeasure();

		manager.setPullMeasure(1, measure);
		manager.setPushMeasure(2, measure);
		manager.setMeasure(3, measure);
		assertEquals(3, manager.getMeasureKeys().size());
		assertTrue(manager.getMeasureKeys().contains(1));
		assertTrue(manager.getMeasureKeys().contains(2));
		assertTrue(manager.getMeasureKeys().contains(3));

		manager.removePullMeasure(1);
		assertEquals(2, manager.getMeasureKeys().size());
		assertFalse(manager.getMeasureKeys().contains(1));
		assertTrue(manager.getMeasureKeys().contains(2));
		assertTrue(manager.getMeasureKeys().contains(3));

		manager.removePushMeasure(2);
		assertEquals(1, manager.getMeasureKeys().size());
		assertFalse(manager.getMeasureKeys().contains(1));
		assertFalse(manager.getMeasureKeys().contains(2));
		assertTrue(manager.getMeasureKeys().contains(3));

		manager.removePushMeasure(3);
		assertEquals(1, manager.getMeasureKeys().size());
		assertFalse(manager.getMeasureKeys().contains(1));
		assertFalse(manager.getMeasureKeys().contains(2));
		assertTrue(manager.getMeasureKeys().contains(3));

		manager.removePullMeasure(3);
		assertEquals(0, manager.getMeasureKeys().size());
	}

	@Test
	public void testRemoveBothAtOnce() {
		var manager = new SimpleMeasureManager();
		var measure = new CountingMeasure();

		manager.setPullMeasure(1, measure);
		manager.setPushMeasure(1, measure);
		manager.removeMeasure(1);
		assertNull(manager.getPullMeasure(1));
		assertNull(manager.getPushMeasure(1));
	}

	@Test
	public void testAddMultipleMeasures() {
		Map<Object, Measure<?>> measures = new HashMap<Object, Measure<?>>();
		var measure1 = new CountingMeasure();
		var measure2 = new LastEvaluationMeasure<Object, Object>();
		var measure3 = new DurationMeasure();
		measures.put(1, measure1);
		measures.put(2, measure2);
		measures.put(3, measure3);

		var manager = new SimpleMeasureManager();
		manager.setAllMeasures(measures);

		assertEquals(3, manager.getMeasureKeys().size());
		assertTrue(manager.getMeasureKeys().contains(1));
		assertTrue(manager.getMeasureKeys().contains(2));
		assertTrue(manager.getMeasureKeys().contains(3));
		assertEquals(measure1, manager.getPullMeasure(1));
		assertEquals(measure1, manager.getPushMeasure(1));
		assertEquals(null, manager.getPullMeasure(2));
		assertEquals(measure2, manager.getPushMeasure(2));
		assertEquals(measure3, manager.getPullMeasure(3));
		assertEquals(null, manager.getPushMeasure(3));
	}

	@Test
	public void testRemoveMultipleMeasures() {
		var measure1 = new CountingMeasure();
		var measure2 = new CountingMeasure();
		var measure3 = new CountingMeasure();

		var manager = new SimpleMeasureManager();
		manager.setPullMeasure(1, measure1);
		manager.setPullMeasure(2, measure2);
		manager.setPullMeasure(3, measure3);
		manager.setPushMeasure(4, measure1);
		manager.setPushMeasure(5, measure2);
		manager.setPushMeasure(6, measure3);

		manager.removeAllMeasures(Arrays.asList(1, 3, 5));
		assertEquals(3, manager.getMeasureKeys().size());
		assertFalse(manager.getMeasureKeys().contains(1));
		assertTrue(manager.getMeasureKeys().contains(2));
		assertFalse(manager.getMeasureKeys().contains(3));
		assertTrue(manager.getMeasureKeys().contains(4));
		assertFalse(manager.getMeasureKeys().contains(5));
		assertTrue(manager.getMeasureKeys().contains(6));

		manager.removeAllMeasures(Arrays.asList(1, 2, 3));
		assertEquals(2, manager.getMeasureKeys().size());
		assertFalse(manager.getMeasureKeys().contains(1));
		assertFalse(manager.getMeasureKeys().contains(2));
		assertFalse(manager.getMeasureKeys().contains(3));
		assertTrue(manager.getMeasureKeys().contains(4));
		assertFalse(manager.getMeasureKeys().contains(5));
		assertTrue(manager.getMeasureKeys().contains(6));

		manager.removeAllMeasures(Arrays.asList(6, 5, 4));
		assertEquals(0, manager.getMeasureKeys().size());
	}

}
