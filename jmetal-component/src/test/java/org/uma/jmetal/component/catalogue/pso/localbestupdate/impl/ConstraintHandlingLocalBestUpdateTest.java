package org.uma.jmetal.component.catalogue.pso.localbestupdate.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.constrainthandling.ConstrainedDominance;

@DisplayName("Unit tests for class ConstraintHandlingLocalBestUpdate")
class ConstraintHandlingLocalBestUpdateTest {

  private static DoubleSolution solution(double f1, double f2, double violation) {
    var solution = new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 1);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = -violation;
    return solution;
  }

  @Nested
  @DisplayName("When updating the local bests")
  class WhenUpdatingTheLocalBests {
    @Test
    @DisplayName("given CDP, then an infeasible particle does not replace a feasible local best")
    void givenTheConstrainedDominationPrinciple_whenUpdating_thenTheFeasibleLocalBestIsKept() {
      // Arrange
      var update = new ConstraintHandlingLocalBestUpdate(new ConstrainedDominance<>(), () -> 0.5);
      var feasibleBest = solution(1.0, 1.0, 0.0);
      var infeasibleBest = solution(1.0, 1.0, 1.0);
      List<DoubleSolution> swarm = List.of(solution(0.0, 0.0, 0.5), solution(2.0, 2.0, 0.0));
      DoubleSolution[] localBest = {feasibleBest, infeasibleBest};

      // Act
      DoubleSolution[] result = update.update(swarm, localBest);

      // Assert: the second particle is feasible, so it replaces the infeasible local best
      assertThat(result[0]).isSameAs(feasibleBest);
      assertThat(result[1].objectives()).containsExactly(2.0, 2.0);
    }

    @Test
    @DisplayName("given a swarm, then the technique is updated with it and the progress")
    void givenASwarm_whenUpdating_thenTheTechniqueIsUpdatedWithItAndTheProgress() {
      // Arrange
      List<List<DoubleSolution>> swarms = new ArrayList<>();
      List<Double> progresses = new ArrayList<>();
      var technique =
          new ConstrainedDominance<DoubleSolution>() {
            @Override
            public void update(List<DoubleSolution> population, double progress) {
              swarms.add(population);
              progresses.add(progress);
            }
          };
      var update = new ConstraintHandlingLocalBestUpdate(technique, () -> 0.75);
      List<DoubleSolution> swarm = List.of(solution(0.0, 0.0, 0.0));

      // Act
      update.update(swarm, new DoubleSolution[] {solution(1.0, 1.0, 0.0)});

      // Assert
      assertThat(swarms).containsExactly(swarm);
      assertThat(progresses).containsExactly(0.75);
      assertThat(update.technique()).isSameAs(technique);
    }
  }
}
