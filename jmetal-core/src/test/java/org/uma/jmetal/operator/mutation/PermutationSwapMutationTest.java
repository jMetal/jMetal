package org.uma.jmetal.operator.mutation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.util.errorchecking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

public class PermutationSwapMutationTest {

	@Test
	public void shouldExecuteWithNullParameterThrowAnException() {
		PermutationSwapMutation<Integer> mutation = new PermutationSwapMutation<>(0.1) ;

		assertThrows(NullParameterException.class, () -> mutation.execute(null));
	}

	@Test
	public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
		double mutationProbability = -0.1 ;
		assertThrows(InvalidProbabilityValueException.class, () -> new PermutationSwapMutation<>(mutationProbability));
	}

	@Test
	public void shouldConstructorFailWhenPassedAValueHigherThanOne() {
		double mutationProbability = 1.1 ;
		assertThrows(InvalidProbabilityValueException.class, () -> new PermutationSwapMutation<>(mutationProbability));
	}
}
