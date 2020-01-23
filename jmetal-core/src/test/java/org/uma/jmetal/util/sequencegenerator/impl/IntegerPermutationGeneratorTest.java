package org.uma.jmetal.util.sequencegenerator.impl;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class IntegerPermutationGeneratorTest {
  @Test
  public void shouldConstructorRaiseAnExceptionIfTheSequenceSizeIsZero() {
    Exception exception =
        assertThrows(InvalidConditionException.class, () -> new IntegerPermutationGenerator(0));
    assertEquals("Size 0 is not a positive number greater than zero", exception.getMessage());
  }

  @Test
  public void shouldConstructorRaiseAnExceptionIfTheSequenceSizeIsNegative() {
    Exception exception =
        assertThrows(InvalidConditionException.class, () -> new IntegerPermutationGenerator(-1));
    assertEquals("Size -1 is not a positive number greater than zero", exception.getMessage());
  }

  @Test
  public void shouldConstructorCreateANonNullObjectIfTheSizeValeIsCorrect() {
    SequenceGenerator<Integer> sequenceGenerator = new IntegerBoundedSequenceGenerator(2);
    assertNotNull(sequenceGenerator);
  }

  @Test
  public void shouldGetValueReturnAlwaysZeroIfTheSequenceHasSizeOne() {
    SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(1);
    assertEquals(0, (int) sequenceGenerator.getValue());

    sequenceGenerator.generateNext();
    assertEquals(0, (int) sequenceGenerator.getValue());
  }

  @Test
  public void shouldGetValueTheRigthValuesWhenInvokedTwiceAndSequenceLengthIsTwo() {
    int sequenceLength = 2;
    int[] values = new int[sequenceLength];
    SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(sequenceLength);
    IntStream.range(0, sequenceLength)
        .forEach(
            i -> {
              values[i] = sequenceGenerator.getValue();
              sequenceGenerator.generateNext();
            });

    assertTrue(Arrays.equals(values, new int[] {0, 1}) || Arrays.equals(values, new int[] {1, 0}));
  }

  @Test
  public void shouldGetValueTheRigthValuesWhenInvokedFiveTimesAndSequenceLengthIsFive() {
    int sequenceLength = 5;
    int[] values = new int[sequenceLength];
    SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(sequenceLength);
    IntStream.range(0, sequenceLength)
        .forEach(
            i -> {
              values[i] = sequenceGenerator.getValue();
              sequenceGenerator.generateNext();
            });

    Arrays.sort(values);

    assertArrayEquals(new int[] {0, 1, 2, 3, 4}, values);
  }

  @Test
  public void shouldGetValueTheRigthValuesWhenInvokedFiveTimesAndSequenceLengthIsThree() {
    int sequenceLength = 3;
    List<Integer> values = new ArrayList<>(5);
    SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(sequenceLength);
    IntStream.range(0, 5)
        .forEach(
            i -> {
              values.add(sequenceGenerator.getValue());
              sequenceGenerator.generateNext();
            });

    assertEquals(5, values.size());
    assertTrue(values.containsAll(Arrays.asList(0, 1, 2)));
    assertFalse(values.contains(3));
  }
}
