package org.uma.jmetal.util.sequencegenerator.impl;

import org.junit.Test;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

import static org.junit.Assert.*;

public class IntegerPermutationGeneratorTest {
    @Test(expected = InvalidConditionException.class)
    public void shouldConstructorRaiseAnExceptionIfTheSequenceSizeIsZero() {
        new IntegerPermutationGenerator(0);
    }

    @Test(expected = InvalidConditionException.class)
    public void shouldConstructorRaiseAnExceptionIfTheSequenceSizeIsNegative() {
        new IntegerPermutationGenerator(-1);
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
        SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(2);
        // TODO
    }
}