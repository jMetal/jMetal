package org.uma.jmetal.util.bounds;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BoundsTest {

  @Test
  void testCreateStoresGivenLowerBound() {
    assertEquals(123, Bounds.create(123, 456).getLowerBound());
  }

  @Test
  void testCreateStoresGivenUpperBound() {
    assertEquals(456, Bounds.create(123, 456).getUpperBound());
  }

  @Test
  void testCreateRejectsNullLowerBound() {
    assertThrows(IllegalArgumentException.class, () -> Bounds.create(null, 456));
  }

  @Test
  void testCreateRejectsNullUpperBound() {
    assertThrows(IllegalArgumentException.class, () -> Bounds.create(123, null));
  }

  @Test
  void testCreateRejectsLowerBoundHigherThanUpperBound() {
    assertThrows(IllegalArgumentException.class, () -> Bounds.create(456, 123));
  }

  @Test
  void testCreateAcceptsLowerBoundEqualsUpperBound() {
    assertDoesNotThrow(() -> Bounds.create(123, 123));
  }

  @Test
  void testRestrictToLowerBoundWhenTooSmall() {
    assertEquals(123, Bounds.create(123, 456).restrict(0));
  }

  @Test
  void testRestrictToUpperBoundWhenTooBig() {
    assertEquals(456, Bounds.create(123, 456).restrict(999));
  }

  @Test
  void testRestrictKeepsUnchangedWhenInRange() {
    assertEquals(333, Bounds.create(123, 456).restrict(333));
  }

}
