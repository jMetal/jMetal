package org.uma.jmetal.util.measure.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.measure.Measure;

class SimpleMeasureManagerTest {

	@Test
  void testStartEmpty() {
		SimpleMeasureManager manager = new SimpleMeasureManager();
		Assertions.assertEquals(0, manager.getMeasureKeys().size());
	}

	@Test
  void testSetGetPullMeasure() {
		SimpleMeasureManager manager = new SimpleMeasureManager();
		CountingMeasure measure1 = new CountingMeasure();
		CountingMeasure measure2 = new CountingMeasure();
		CountingMeasure measure3 = new CountingMeasure();

		manager.setPullMeasure(1, measure1);
		Assertions.assertEquals(measure1, manager.getPullMeasure(1));

		manager.setPullMeasure(2, measure2);
		Assertions.assertEquals(measure2, manager.getPullMeasure(2));

		manager.setPullMeasure(1, measure3);
		Assertions.assertEquals(measure3, manager.getPullMeasure(1));
	}

	@Test
  void testSetGetPushMeasure() {
		SimpleMeasureManager manager = new SimpleMeasureManager();
		CountingMeasure measure1 = new CountingMeasure();
		CountingMeasure measure2 = new CountingMeasure();
		CountingMeasure measure3 = new CountingMeasure();

		manager.setPushMeasure(1, measure1);
		Assertions.assertEquals(measure1, manager.getPushMeasure(1));

		manager.setPushMeasure(2, measure2);
		Assertions.assertEquals(measure2, manager.getPushMeasure(2));

		manager.setPushMeasure(1, measure3);
		Assertions.assertEquals(measure3, manager.getPushMeasure(1));
	}

	@Test
  void testSetMeasureGetBoth() {
		SimpleMeasureManager manager = new SimpleMeasureManager();
		CountingMeasure measure = new CountingMeasure();

		manager.setMeasure(1, measure);
		Assertions.assertEquals(measure, manager.getPullMeasure(1));
		Assertions.assertEquals(measure, manager.getPushMeasure(1));
	}

	@Test
  void testAddMeasureAddKey() {
		SimpleMeasureManager manager = new SimpleMeasureManager();
		CountingMeasure measure = new CountingMeasure();

		manager.setPullMeasure(1, measure);
		Assertions.assertEquals(1, manager.getMeasureKeys().size());
		Assertions.assertTrue(manager.getMeasureKeys().contains(1));

		manager.setPushMeasure(2, measure);
		Assertions.assertEquals(2, manager.getMeasureKeys().size());
		Assertions.assertTrue(manager.getMeasureKeys().contains(1));
		Assertions.assertTrue(manager.getMeasureKeys().contains(2));

		manager.setMeasure(3, measure);
		Assertions.assertEquals(3, manager.getMeasureKeys().size());
		Assertions.assertTrue(manager.getMeasureKeys().contains(1));
		Assertions.assertTrue(manager.getMeasureKeys().contains(2));
		Assertions.assertTrue(manager.getMeasureKeys().contains(3));
	}

	@Test
  void testRemoveBothMeasuresRemoveKey() {
		SimpleMeasureManager manager = new SimpleMeasureManager();
		CountingMeasure measure = new CountingMeasure();

		manager.setPullMeasure(1, measure);
		manager.setPushMeasure(2, measure);
		manager.setMeasure(3, measure);
		Assertions.assertEquals(3, manager.getMeasureKeys().size());
		Assertions.assertTrue(manager.getMeasureKeys().contains(1));
		Assertions.assertTrue(manager.getMeasureKeys().contains(2));
		Assertions.assertTrue(manager.getMeasureKeys().contains(3));

		manager.removePullMeasure(1);
		Assertions.assertEquals(2, manager.getMeasureKeys().size());
		Assertions.assertFalse(manager.getMeasureKeys().contains(1));
		Assertions.assertTrue(manager.getMeasureKeys().contains(2));
		Assertions.assertTrue(manager.getMeasureKeys().contains(3));

		manager.removePushMeasure(2);
		Assertions.assertEquals(1, manager.getMeasureKeys().size());
		Assertions.assertFalse(manager.getMeasureKeys().contains(1));
		Assertions.assertFalse(manager.getMeasureKeys().contains(2));
		Assertions.assertTrue(manager.getMeasureKeys().contains(3));

		manager.removePushMeasure(3);
		Assertions.assertEquals(1, manager.getMeasureKeys().size());
		Assertions.assertFalse(manager.getMeasureKeys().contains(1));
		Assertions.assertFalse(manager.getMeasureKeys().contains(2));
		Assertions.assertTrue(manager.getMeasureKeys().contains(3));

		manager.removePullMeasure(3);
		Assertions.assertEquals(0, manager.getMeasureKeys().size());
	}

	@Test
  void testRemoveBothAtOnce() {
		SimpleMeasureManager manager = new SimpleMeasureManager();
		CountingMeasure measure = new CountingMeasure();

		manager.setPullMeasure(1, measure);
		manager.setPushMeasure(1, measure);
		manager.removeMeasure(1);
		Assertions.assertNull(manager.getPullMeasure(1));
		Assertions.assertNull(manager.getPushMeasure(1));
	}

	@Test
  void testAddMultipleMeasures() {
		Map<Object, Measure<?>> measures = new HashMap<Object, Measure<?>>();
		CountingMeasure measure1 = new CountingMeasure();
		LastEvaluationMeasure<Object, Object> measure2 = new LastEvaluationMeasure<>();
		DurationMeasure measure3 = new DurationMeasure();
		measures.put(1, measure1);
		measures.put(2, measure2);
		measures.put(3, measure3);

		SimpleMeasureManager manager = new SimpleMeasureManager();
		manager.setAllMeasures(measures);

		Assertions.assertEquals(3, manager.getMeasureKeys().size());
		Assertions.assertTrue(manager.getMeasureKeys().contains(1));
		Assertions.assertTrue(manager.getMeasureKeys().contains(2));
		Assertions.assertTrue(manager.getMeasureKeys().contains(3));
		Assertions.assertEquals(measure1, manager.getPullMeasure(1));
		Assertions.assertEquals(measure1, manager.getPushMeasure(1));
		Assertions.assertEquals(null, manager.getPullMeasure(2));
		Assertions.assertEquals(measure2, manager.getPushMeasure(2));
		Assertions.assertEquals(measure3, manager.getPullMeasure(3));
		Assertions.assertEquals(null, manager.getPushMeasure(3));
	}

	@Test
  void testRemoveMultipleMeasures() {
		CountingMeasure measure1 = new CountingMeasure();
		CountingMeasure measure2 = new CountingMeasure();
		CountingMeasure measure3 = new CountingMeasure();

		SimpleMeasureManager manager = new SimpleMeasureManager();
		manager.setPullMeasure(1, measure1);
		manager.setPullMeasure(2, measure2);
		manager.setPullMeasure(3, measure3);
		manager.setPushMeasure(4, measure1);
		manager.setPushMeasure(5, measure2);
		manager.setPushMeasure(6, measure3);

		manager.removeAllMeasures(Arrays.asList(1, 3, 5));
		Assertions.assertEquals(3, manager.getMeasureKeys().size());
		Assertions.assertFalse(manager.getMeasureKeys().contains(1));
		Assertions.assertTrue(manager.getMeasureKeys().contains(2));
		Assertions.assertFalse(manager.getMeasureKeys().contains(3));
		Assertions.assertTrue(manager.getMeasureKeys().contains(4));
		Assertions.assertFalse(manager.getMeasureKeys().contains(5));
		Assertions.assertTrue(manager.getMeasureKeys().contains(6));

		manager.removeAllMeasures(Arrays.asList(1, 2, 3));
		Assertions.assertEquals(2, manager.getMeasureKeys().size());
		Assertions.assertFalse(manager.getMeasureKeys().contains(1));
		Assertions.assertFalse(manager.getMeasureKeys().contains(2));
		Assertions.assertFalse(manager.getMeasureKeys().contains(3));
		Assertions.assertTrue(manager.getMeasureKeys().contains(4));
		Assertions.assertFalse(manager.getMeasureKeys().contains(5));
		Assertions.assertTrue(manager.getMeasureKeys().contains(6));

		manager.removeAllMeasures(Arrays.asList(6, 5, 4));
		Assertions.assertEquals(0, manager.getMeasureKeys().size());
	}

}
