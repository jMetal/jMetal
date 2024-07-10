package org.uma.jmetal.util.naming.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SimpleDescribedEntityTest {

	@Test
  void testSetGetName() {
		SimpleDescribedEntity entity = new SimpleDescribedEntity();

		entity.setName("test");
		Assertions.assertEquals("test", entity.name());

		entity.setName("abc");
		Assertions.assertEquals("abc", entity.name());
	}

	@Test
  void testSetGetDescription() {
		SimpleDescribedEntity entity = new SimpleDescribedEntity();

		entity.setDescription("test");
		Assertions.assertEquals("test", entity.description());

		entity.setDescription("abc");
		Assertions.assertEquals("abc", entity.description());
	}

	class TestedClass extends SimpleDescribedEntity {
	}

	@Test
  void testClassNameWhenNoName() {
		Assertions.assertEquals(TestedClass.class.getSimpleName(), new TestedClass().name());
	}

	@Test
  void testNullDescriptionWhenNoDescription() {
		Assertions.assertNull(new SimpleDescribedEntity().description());
		Assertions.assertNull(new SimpleDescribedEntity("name").description());
	}

	@Test
  void testCorrectNameWhenProvided() {
		String name = "named measure";
		Assertions.assertEquals(name, new SimpleDescribedEntity(name).name());
		Assertions.assertEquals(name, new SimpleDescribedEntity(name, "description").name());
	}

	@Test
  void testCorrectDescriptionWhenProvided() {
		String description = "My measure description is awesome!";
		Assertions.assertEquals(description, new SimpleDescribedEntity("measure",
				description).description());
	}

}
