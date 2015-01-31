package org.uma.jmetal.util.measurement.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleMeasureTest {

	@Test
	public void testSetGetName() {
		SimpleMeasure measure = new SimpleMeasure();

		measure.setName("test");
		assertEquals("test", measure.getName());

		measure.setName("abc");
		assertEquals("abc", measure.getName());
	}

	@Test
	public void testSetGetDescription() {
		SimpleMeasure measure = new SimpleMeasure();

		measure.setDescription("test");
		assertEquals("test", measure.getDescription());

		measure.setDescription("abc");
		assertEquals("abc", measure.getDescription());
	}

	@Test
	public void testClassNameWhenNoName() {
		assertEquals(SimpleMeasure.class.getSimpleName(),
				new SimpleMeasure().getName());
	}

	@Test
	public void testNullDescriptionWhenNoDescription() {
		assertNull(new SimpleMeasure().getDescription());
		assertNull(new SimpleMeasure("name").getDescription());
	}

	@Test
	public void testCorrectNameWhenProvided() {
		String name = "named measure";
		assertEquals(name, new SimpleMeasure(name).getName());
		assertEquals(name, new SimpleMeasure(name, "description").getName());
	}

	@Test
	public void testCorrectDescriptionWhenProvided() {
		String description = "My measure description is awesome!";
		assertEquals(description,
				new SimpleMeasure("measure", description).getDescription());
	}

}
