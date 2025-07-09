package org.uma.jmetal.util.sequencegenerator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.sequencegenerator.impl.CyclicIntegerSequence;

class CyclicIntegerSequenceTest {

  @Test
  void constructorRaisesExceptionForZeroSize() {
    Exception exception = assertThrows(
        InvalidConditionException.class,
        () -> new CyclicIntegerSequence(0));
    assertEquals("Size 0 must be positive", exception.getMessage());
  }

  @Test
  void constructorRaisesExceptionForNegativeSize() {
    Exception exception = assertThrows(
        InvalidConditionException.class,
        () -> new CyclicIntegerSequence(-1));
    assertEquals("Size -1 must be positive", exception.getMessage());
  }

  @Test
  void constructorCreatesNonNullObjectForValidSize() {
    var generator = new CyclicIntegerSequence(2);
    assertNotNull(generator);
  }

  @Test
  void getValueReturnsZeroForSizeOne() {
    var generator = new CyclicIntegerSequence(1);
    assertEquals(0, (int)generator.getValue());
    generator.generateNext();
    assertEquals(0, (int)generator.getValue());
  }

  @Test
  void sequenceCyclesCorrectlyForSizeTwo() {
    var generator = new CyclicIntegerSequence(2);
    assertEquals(0, (int)generator.getValue());
    generator.generateNext();
    assertEquals(1, (int)generator.getValue());
    generator.generateNext();
    assertEquals(0, (int)generator.getValue());
  }

  @Test
  void sequenceCyclesCorrectlyForMultipleIterations() {
    var generator = new CyclicIntegerSequence(4);
    int[] expected = {0, 1, 2, 3, 0, 1, 2};
    
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], (int)generator.getValue());
      generator.generateNext();
    }
  }
  
  @Test
  void getSequenceLengthReturnsCorrectValue() {
    int size = 7;
    var generator = new CyclicIntegerSequence(size);
    assertEquals(size, generator.getSequenceLength());
  }
  
  @Test
  void valuesAreAlwaysInRange() {
    int size = 5;
    var generator = new CyclicIntegerSequence(size);
    
    for (int i = 0; i < size * 3; i++) {  // Test multiple cycles
      int value = generator.getValue();
      assertThat(value, greaterThanOrEqualTo(0));
      assertThat(value, lessThan(size));
      generator.generateNext();
    }
  }
  
  @Test
  void sequenceContainsAllValues() {
    int size = 10;
    var generator = new CyclicIntegerSequence(size);
    
    Set<Integer> seenValues = new HashSet<>();
    for (int i = 0; i < size; i++) {
      seenValues.add(generator.getValue());
      generator.generateNext();
    }
    
    assertEquals(size, seenValues.size());
    assertTrue(IntStream.range(0, size).allMatch(seenValues::contains));
  }
  
  @Test
  void sequenceIsDeterministic() {
    int size = 10;
    var generator1 = new CyclicIntegerSequence(size);
    var generator2 = new CyclicIntegerSequence(size);
    
    for (int i = 0; i < size * 2; i++) {
      assertEquals(generator1.getValue(), generator2.getValue());
      generator1.generateNext();
      generator2.generateNext();
    }
  }
}
