package org.uma.jmetal.util.sequencegenerator.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

public class IntegerBoundedSequenceGeneratorTest {

  @Test
  public void shouldConstructorRaiseAnExceptionIfTheSequenceSizeIsZero() {
    Exception exception =
            assertThrows(InvalidConditionException.class, () -> new IntegerBoundedSequenceGenerator(0));
    assertEquals("Size 0 is not a positive number greater than zero", exception.getMessage());
  }

  @Test
  public void shouldConstructorRaiseAnExceptionIfTheSequenceSizeIsNegative() {
    Exception exception =
            assertThrows(InvalidConditionException.class, () -> new IntegerBoundedSequenceGenerator(-1));
    assertEquals("Size -1 is not a positive number greater than zero", exception.getMessage());
  }

  @Test
  public void shouldConstructorCreateANonNullObjectIfTheSizeValeIsCorrect() {
    SequenceGenerator<Integer> sequenceGenerator = new IntegerBoundedSequenceGenerator(2);
    assertNotNull(sequenceGenerator);
  }

  @Test
  public void shouldGetValueReturnAlwaysZeroIfTheSequenceHasSizeOne() {
    SequenceGenerator<Integer> sequenceGenerator = new IntegerBoundedSequenceGenerator(1);
    assertEquals(0, (int)sequenceGenerator.getValue());

    sequenceGenerator.generateNext();
    assertEquals(0, (int)sequenceGenerator.getValue());
  }

  @Test
  public void shouldGetValueReturnZeroOneZeroIfTheSequenceHasSizeTwoWhenInvokedThreeTimes() {
    SequenceGenerator<Integer> sequenceGenerator = new IntegerBoundedSequenceGenerator(2);
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
    SequenceGenerator<Integer> sequenceGenerator = new IntegerBoundedSequenceGenerator(4);
    var sequenceLength = 6 ;
    var values = new int[sequenceLength] ;
      for (var i = 0; i < sequenceLength; i++) {
          values[i] = sequenceGenerator.getValue();
          sequenceGenerator.generateNext();
      }

      assertArrayEquals(new int[] {0, 1, 2, 3, 0, 1}, values) ;
  }
}
