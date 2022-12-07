package org.uma.jmetal.operator.mutation;

import org.junit.Test;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.util.errorchecking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

public class PermutationSwapMutationTest {

	@Test (expected = NullParameterException.class)
	public void shouldExecuteWithNullParameterThrowAnException() {
		PermutationSwapMutation<Integer> mutation = new PermutationSwapMutation<>(0.1) ;

		mutation.execute(null) ;
	}

	@Test (expected = InvalidProbabilityValueException.class)
	public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
		double mutationProbability = -0.1 ;
		new PermutationSwapMutation<>(mutationProbability) ;
	}

	@Test (expected = InvalidProbabilityValueException.class)
	public void shouldConstructorFailWhenPassedAValueHigherThanOne() {
		double mutationProbability = 1.1 ;
		new PermutationSwapMutation<>(mutationProbability) ;
	}
}
