package org.uma.jmetal.util.sequencegenerator.impl;

import org.junit.Test;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class IntegerPermutationGeneratorTest {

  @Test(expected = InvalidConditionException.class)
  public void shouldConstructorRaiseAnExceptionIfTheSequenceSizeIsZero() {
    new IntegerSequenceGenerator(0);
  }

  @Test(expected = InvalidConditionException.class)
  public void shouldConstructorRaiseAnExceptionIfTheSequenceSizeIsNegative() {
    new IntegerSequenceGenerator(-1);
  }

  @Test
  public void shouldConstructorCreateANonNullObjectIfTheSizeValeIsCorrect() {
    SequenceGenerator<Integer> sequenceGenerator = new IntegerSequenceGenerator(2);
    assertNotNull(sequenceGenerator);
  }

  @Test
  public void shouldGetValueReturnAlwaysZeroIfTheSequenceHasSizeOne() {
    SequenceGenerator<Integer> sequenceGenerator = new IntegerSequenceGenerator(1);
    assertEquals(0, (int)sequenceGenerator.getValue());

    sequenceGenerator.generateNext();
    assertEquals(0, (int)sequenceGenerator.getValue());
  }

  @Test
  public void shouldGetValueReturnZeroOneZeroIfTheSequenceHasSizeTwoWhenInvokedThreeTimes() {
    SequenceGenerator<Integer> sequenceGenerator = new IntegerSequenceGenerator(2);
    int value0 = sequenceGenerator.getValue() ;
    sequenceGenerator.generateNext();
    int value1 = sequenceGenerator.getValue() ;
    sequenceGenerator.generateNext();
    int value2 = sequenceGenerator.getValue() ;
    sequenceGenerator.generateNext();

    assertEquals(0, value0);
    assertEquals(1, value1);
    assertEquals(0, value2);
  }

  @Test
  public void shouldGenerateNextWorkProperlyWhenInvokedSixTimesAndTheSequenceLengthIsFour() {
    SequenceGenerator<Integer> sequenceGenerator = new IntegerSequenceGenerator(4);
    int sequenceLength = 6 ;
    int[] values = new int[sequenceLength] ;
    IntStream.range(0, sequenceLength).forEach(i -> {values[i] = sequenceGenerator.getValue() ; sequenceGenerator.generateNext();}) ;

    assertArrayEquals(new int[] {0, 1, 2, 3, 0, 1}, values) ;
  }
}
