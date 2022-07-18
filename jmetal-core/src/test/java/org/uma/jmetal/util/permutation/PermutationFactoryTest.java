package org.uma.jmetal.util.permutation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

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
    assertThat(PermutationFactory.createIntegerPermutation(0,
        JMetalRandom.getInstance()::nextInt)).hasSize(0);
  }

  @Test
  void createIntegerPermutationWithLengthOneReturnAListWithTheElementZero() {
    List<Integer> permutation = PermutationFactory.createIntegerPermutation(1,
        JMetalRandom.getInstance()::nextInt);
    assertThat(permutation).hasSize(1);
    assertThat(permutation.get(0)).isZero();
  }

  @Test
  void createIntegerPermutationWithLengthTwoReturnAListWithZeroAndOne() {
    List<Integer> permutation = PermutationFactory.createIntegerPermutation(2,
        JMetalRandom.getInstance()::nextInt);

    assertThat(permutation)
        .hasSize(2)
        .containsExactlyInAnyOrderElementsOf(List.of(0, 1));
  }

  @Test
  void createIntegerPermutationWithLengthFiveReturnAListWithFiveElements() {
    List<Integer> permutation = PermutationFactory.createIntegerPermutation(5,
        JMetalRandom.getInstance()::nextInt);

    assertThat(permutation)
        .hasSize(5)
        .containsExactlyInAnyOrderElementsOf(List.of(0, 1, 2, 3, 4));
  }
}
