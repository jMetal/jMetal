package org.uma.jmetal.util.naming.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class SimpleDescribedEntityTest {

	@Test
	public void testSetGetName() {
		SimpleDescribedEntity entity = new SimpleDescribedEntity();

		entity.setName("test");
		assertEquals("test", entity.name());

		entity.setName("abc");
		assertEquals("abc", entity.name());
	}

	@Test
	public void testSetGetDescription() {
		SimpleDescribedEntity entity = new SimpleDescribedEntity();

		entity.setDescription("test");
		assertEquals("test", entity.description());

		entity.setDescription("abc");
		assertEquals("abc", entity.description());
	}

	class TestedClass extends SimpleDescribedEntity {
	}

	@Test
	public void testClassNameWhenNoName() {
		assertEquals(TestedClass.class.getSimpleName(),
				new TestedClass().name());
	}

	@Test
	public void testNullDescriptionWhenNoDescription() {
		assertNull(new SimpleDescribedEntity().description());
		assertNull(new SimpleDescribedEntity("name").description());
	}

	@Test
	public void testCorrectNameWhenProvided() {
		String name = "named measure";
		assertEquals(name, new SimpleDescribedEntity(name).name());
		assertEquals(name,
				new SimpleDescribedEntity(name, "description").name());
	}

	@Test
	public void testCorrectDescriptionWhenProvided() {
		String description = "My measure description is awesome!";
		assertEquals(description, new SimpleDescribedEntity("measure",
				description).description());
	}

}
