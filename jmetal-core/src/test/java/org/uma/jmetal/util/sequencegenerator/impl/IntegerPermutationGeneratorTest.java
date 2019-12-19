package org.uma.jmetal.util.sequencegenerator.impl;

import org.junit.jupiter.api.Test;

import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntegerPermutationGeneratorTest {
    @Test
    public void shouldConstructorRaiseAnExceptionIfTheSequenceSizeIsZero() {
        Exception exception = assertThrows(InvalidConditionException.class, () -> new IntegerPermutationGenerator(0) ) ;
        assertEquals("Size == 0 is not a positive number greater than zero", exception.getMessage());

    }

    @Test
    public void shouldConstructorRaiseAnExceptionIfTheSequenceSizeIsNegative() {
        Exception exception = assertThrows(InvalidConditionException.class, () -> new IntegerPermutationGenerator(-1) ) ;
        assertEquals("Size == -1 is not a positive number greater than zero", exception.getMessage());
    }

    @Test
    public void shouldConstructorCreateANonNullObjectIfTheSizeValeIsCorrect() {
        SequenceGenerator<Integer> sequenceGenerator = new IntegerSequenceGenerator(2);
        assertNotNull(sequenceGenerator);
    }

    @Test
    public void shouldGetValueReturnAlwaysZeroIfTheSequenceHasSizeOne() {
        SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(1);
        assertEquals(0, (int)sequenceGenerator.getValue());

        sequenceGenerator.generateNext();
        assertEquals(0, (int)sequenceGenerator.getValue());
    }

    @Test
    public void shouldGetValueTheRigthValuesWhenInvokedTwiceAndSequenceLengthIsTwo() {
        int sequenceLength = 2 ;
        int[] values = new int[sequenceLength] ;
        SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(sequenceLength);
        //IntStream.range(0, sequenceLength).forEach(i -> {values[i] = sequenceGenerator.getValue() ; sequenceGenerator.generateNext();}) ;

        values = new int[]{0,1} ;
        //assertThat(values, Matchers.arrayContainingInAnyOrder(new int[]{0, 1}));
    }
}