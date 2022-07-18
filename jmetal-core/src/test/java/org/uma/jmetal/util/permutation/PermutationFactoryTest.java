package org.uma.jmetal.util.permutation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

class PermutationFactoryTest {

  @Test
  void createIntegerPermutationRaisesAnExceptionWhenTheLengthParameterIsNegative() {
    assertThatThrownBy(() -> PermutationFactory.createIntegerPermutation(-1, null));
  }

  @Test
  void createIntegerPermutationRaisesAnExceptionWhenRandomGeneratorIsNull() {
    assertThatThrownBy(() -> PermutationFactory.createIntegerPermutation(4, null));
  }

  @Test
  void createIntegerPermutationReturnsAnEmptyListWhenTheLengthIsZero() {
    var randomGenerator = JMetalRandom.getInstance().getRandomGenerator();
    assertThat(PermutationFactory.createIntegerPermutation(0, randomGenerator)).hasSize(0);
  }

  @Test
  void createIntegerPermutationWithLengthOneReturnAListWithTheElementZero() {
    var randomGenerator = JMetalRandom.getInstance().getRandomGenerator();
    var permutation = PermutationFactory.createIntegerPermutation(1, randomGenerator) ;
    assertThat(permutation).hasSize(1) ;
  }

  @Test
  void createIntegerPermutationWithLengthTwoReturnAListWithZeroAndOne() {
    var randomGenerator = JMetalRandom.getInstance().getRandomGenerator();
    var permutation = PermutationFactory.createIntegerPermutation(2, randomGenerator) ;

    assertThat(permutation)
        .hasSize(2)
        .containsExactlyInAnyOrderElementsOf(List.of(0, 1)) ;
  }

  @Test
  void createIntegerPermutationWithLengthFiveReturnAListWithFiveElements() {
    var randomGenerator = JMetalRandom.getInstance().getRandomGenerator();
    var permutation = PermutationFactory.createIntegerPermutation(5, randomGenerator) ;

    assertThat(permutation)
        .hasSize(5)
        .containsExactlyInAnyOrderElementsOf(List.of(0, 1, 2, 3, 4)) ;
  }
}
