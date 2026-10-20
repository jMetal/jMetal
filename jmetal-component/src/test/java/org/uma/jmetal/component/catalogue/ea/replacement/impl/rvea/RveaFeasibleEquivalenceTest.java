package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Unit tests for the RVEA-family selections on feasible candidates")
class RveaFeasibleEquivalenceTest {
  private static final long SEED = 17L;

  static Stream<Arguments> cases() {
    return Stream.of("RVEA", "RVEAStar", "IRVEA")
        .flatMap(
            variant ->
                Stream.of(2, 3, 4, 7)
                    .flatMap(
                        objectives ->
                            Stream.of("curved", "disconnected", "collapsed")
                                .map(shape -> Arguments.of(variant, objectives, shape))));
  }

  @Nested
  @DisplayName("When every candidate satisfies its constraints")
  class WhenEveryCandidateSatisfiesItsConstraints {
    @ParameterizedTest(name = "{0}, M={1}, {2}")
    @MethodSource(
        "org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea"
            + ".RveaFeasibleEquivalenceTest#cases")
    @DisplayName(
        "given satisfied constraints, when evolving, then the states are those of the same"
            + " candidates without constraints")
    void givenSatisfiedConstraints_whenEvolving_thenTheStatesMatchTheUnconstrainedOnes(
        String variant, int objectives, String shape) {
      // Arrange
      List<String> unconstrained =
          RveaTrajectoryFixtures.trajectory(variant, objectives, shape, SEED, 0);

      // Act
      List<String> constrained =
          RveaTrajectoryFixtures.trajectory(variant, objectives, shape, SEED, 3);

      // Assert
      assertThat(constrained).isEqualTo(unconstrained);
    }
  }

  @Nested
  @DisplayName("When an infeasible candidate dominates every objective")
  class WhenAnInfeasibleCandidateDominatesEveryObjective {
    @ParameterizedTest(name = "{0}, M={1}, {2}")
    @MethodSource(
        "org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea"
            + ".RveaFeasibleEquivalenceTest#cases")
    @DisplayName(
        "given an infeasible ideal point in every generation, when evolving, then the feasible"
            + " survivors, vectors and archive are those obtained without it")
    void givenAnInfeasibleIdeal_whenEvolving_thenTheFeasibleStatesDoNotChange(
        String variant, int objectives, String shape) {
      // Arrange
      List<String> withoutInfeasible =
          RveaTrajectoryFixtures.trajectory(variant, objectives, shape, SEED, 0, false);

      // Act
      List<String> withInfeasible =
          RveaTrajectoryFixtures.trajectory(variant, objectives, shape, SEED, 0, true);

      // Assert
      assertThat(withInfeasible).isEqualTo(withoutInfeasible);
    }
  }
}
