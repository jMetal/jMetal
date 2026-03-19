package org.uma.jmetal.util.sequencegenerator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.sequencegenerator.impl.RandomPermutationCycle;

class RandomPermutationCycleTest {

  @Test
  void constructorRaisesExceptionForZeroSize() {
    Exception exception = assertThrows(
        InvalidConditionException.class,
        () -> new RandomPermutationCycle(0));
    assertEquals("Size 0 must be positive", exception.getMessage());
  }

  @Test
  void constructorRaisesExceptionForNegativeSize() {
    Exception exception = assertThrows(
        InvalidConditionException.class,
        () -> new RandomPermutationCycle(-1));
    assertEquals("Size -1 must be positive", exception.getMessage());
  }

  @Test
  void constructorCreatesNonNullObjectForValidSize() {
    var generator = new RandomPermutationCycle(5);
    assertNotNull(generator);
  }

  @Test
  void getValueReturnsValuesInRange() {
    int size = 10;
    var generator = new RandomPermutationCycle(size);
    
    for (int i = 0; i < size * 2; i++) {  // Test multiple cycles
      int value = generator.getValue();
      assertThat(value, greaterThanOrEqualTo(0));
      assertThat(value, lessThan(size));
      generator.generateNext();
    }
  }

  @Test
  void generateNextAdvancesToNextValue() {
    int size = 5;
    var generator = new RandomPermutationCycle(size);
    
    int firstValue = generator.getValue();
    generator.generateNext();
    int secondValue = generator.getValue();
    
    assertNotEquals(firstValue, secondValue);
  }

  @Test
  void sequenceContainsAllValuesExactlyOnce() {
    int size = 10;
    var generator = new RandomPermutationCycle(size);
    
    Set<Integer> seenValues = new HashSet<>();
    for (int i = 0; i < size; i++) {
      int value = generator.getValue();
      assertFalse(seenValues.contains(value), "Duplicate value " + value + " found in sequence");
      seenValues.add(value);
      generator.generateNext();
    }
    
    assertEquals(size, seenValues.size());
    assertTrue(IntStream.range(0, size).allMatch(seenValues::contains));
  }

  @Test
  void sequenceResetsAfterFullCycle() {
    int size = 25;
    var generator = new RandomPermutationCycle(size);
    
    // Get the first permutation
    int[] firstPermutation = new int[size];
    for (int i = 0; i < size; i++) {
      firstPermutation[i] = generator.getValue();
      generator.generateNext();
    }
    
    // After size steps, we should have a new permutation
    int[] secondPermutation = new int[size];
    for (int i = 0; i < size; i++) {
      secondPermutation[i] = generator.getValue();
      generator.generateNext();
    }

    // The two permutations should be different (very high probability)
    assertFalse(Arrays.equals(firstPermutation, secondPermutation), "The permutations shold be different");
  }

  @Test
  void getSequenceLengthReturnsCorrectValue() {
    int size = 7;
    var generator = new RandomPermutationCycle(size);
    assertEquals(size, generator.getSequenceLength());
  }

  @Test
  void sequenceIsDifferentOnDifferentInstances() {
    int size = 10;
    var generator1 = new RandomPermutationCycle(size);
    var generator2 = new RandomPermutationCycle(size);
    
    // It's possible but extremely unlikely that two random permutations are identical
    boolean allEqual = true;
    for (int i = 0; i < size; i++) {
      if (generator1.getValue() != generator2.getValue()) {
        allEqual = false;
        break;
      }
      generator1.generateNext();
      generator2.generateNext();
    }
    
    assertFalse(allEqual, "Two different generators produced the same sequence");
  }
}
