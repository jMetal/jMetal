package org.uma.jmetal.util.naming.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.naming.DescribedEntity;

class DescribedEntitySetTest {

	@Test
  void testAddingSameEntityModifiesNothing() {
		SimpleDescribedEntity entity = new SimpleDescribedEntity("Entity");

		DescribedEntitySet<DescribedEntity> set = new DescribedEntitySet<>();
		Assertions.assertTrue(set.add(entity));
		Assertions.assertFalse(set.add(entity));
		Assertions.assertFalse(set.add(entity));
		Assertions.assertFalse(set.add(entity));
		Assertions.assertEquals(1, set.size());
	}

	@Test
  void testAddingDifferentEntityWithDifferentNameProperlyAdds() {
		DescribedEntitySet<DescribedEntity> set = new DescribedEntitySet<>();
		Assertions.assertTrue(set.add(new SimpleDescribedEntity("Entity 1")));
		Assertions.assertTrue(set.add(new SimpleDescribedEntity("Entity 2")));
		Assertions.assertTrue(set.add(new SimpleDescribedEntity("Entity 3")));
		Assertions.assertTrue(set.add(new SimpleDescribedEntity("Entity 4")));
		Assertions.assertEquals(4, set.size());
	}

	@Test
  void testGetReturnsCorrectEntity() {
		SimpleDescribedEntity e1 = new SimpleDescribedEntity("Entity 1");
		SimpleDescribedEntity e2 = new SimpleDescribedEntity("Entity 2");
		SimpleDescribedEntity e3 = new SimpleDescribedEntity("Entity 3");
		SimpleDescribedEntity e4 = new SimpleDescribedEntity("Entity 4");

		DescribedEntitySet<DescribedEntity> set = new DescribedEntitySet<>();
		set.add(e1);
		set.add(e2);
		set.add(e3);
		set.add(e4);

		Assertions.assertEquals(e1, set.get("Entity 1"));
		Assertions.assertEquals(e2, set.get("Entity 2"));
		Assertions.assertEquals(e3, set.get("Entity 3"));
		Assertions.assertEquals(e4, set.get("Entity 4"));
	}

	@Test
  void testAddingDifferentEntityWithSameNameThrowsException() {
		String name = "Entity";
		DescribedEntitySet<DescribedEntity> set = new DescribedEntitySet<>();
		set.add(new SimpleDescribedEntity(name));

		try {
			set.add(new SimpleDescribedEntity(name));
			Assertions.fail("No exception thrown");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
  void testToStringInCaseInsensitiveOrder() {
		DescribedEntitySet<DescribedEntity> set = new DescribedEntitySet<>();
		set.add(new SimpleDescribedEntity("b"));
		set.add(new SimpleDescribedEntity("A"));
		set.add(new SimpleDescribedEntity("a"));
		set.add(new SimpleDescribedEntity("B"));

		Assertions.assertEquals("[A, a, B, b]", set.toString());
	}
}
