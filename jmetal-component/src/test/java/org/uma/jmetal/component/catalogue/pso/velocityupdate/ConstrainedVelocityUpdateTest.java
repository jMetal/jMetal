package org.uma.jmetal.component.catalogue.pso.velocityupdate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.ConstantValueStrategy;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.impl.ConstrainedVelocityUpdate;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;

@DisplayName("Unit tests for ConstrainedVelocityUpdate (SMPSO)")
class ConstrainedVelocityUpdateTest {
  private static final DoubleProblem PROBLEM = new ZDT1(3);

  /** Gives access to the protected formula of the constriction coefficient. */
  private static class Visible extends ConstrainedVelocityUpdate {
    Visible() {
      super(1.5, 2.5, 1.5, 2.5, PROBLEM);
    }

    double coefficient(double c1, double c2) {
      return constrictionCoefficient(c1, c2);
    }
  }

  @Test
  @DisplayName("given c1 + c2 not above four, when computed, then the coefficient is one")
  void givenASumNotAboveFour_whenComputed_thenTheCoefficientIsOne() {
    assertThat(new Visible().coefficient(1.5, 1.5)).isEqualTo(1.0);
    assertThat(new Visible().coefficient(2.0, 2.0)).isEqualTo(1.0);
  }

  @Test
  @DisplayName("given c1 + c2 above four, when computed, then the coefficient is the one of the original SMPSO, which is negative")
  void givenASumAboveFour_whenComputed_thenTheCoefficientIsThatOfTheOriginalSMPSO() {
    double rho = 5.0;

    assertThat(new Visible().coefficient(2.5, 2.5))
        .isCloseTo(2.0 / (2.0 - rho - Math.sqrt(rho * rho - 4.0 * rho)), within(1e-12))
        .isNegative();
  }

  @Test
  @DisplayName("given a particle that is far from its bests, when the velocity is updated, then it is limited to half the range of the variable")
  void givenAFarParticle_whenTheVelocityIsUpdated_thenItIsLimited() {
    // Arrange: the range of the variables of ZDT1 is [0, 1], so the limit is 0.5
    DoubleSolution particle = PROBLEM.createSolution();
    DoubleSolution localBest = PROBLEM.createSolution();
    DoubleSolution globalBest = PROBLEM.createSolution();
    for (int i = 0; i < PROBLEM.numberOfVariables(); i++) {
      particle.variables().set(i, 0.0);
      localBest.variables().set(i, 1.0);
      globalBest.variables().set(i, 1.0);
    }
    BoundedArchive<DoubleSolution> leaders = new CrowdingDistanceArchive<>(10);
    leaders.add(globalBest);
    GlobalBestSelection selection = solutions -> globalBest;
    double[][] speed = new double[1][PROBLEM.numberOfVariables()];

    // Act
    double[][] updated =
        new ConstrainedVelocityUpdate(1.5, 2.5, 1.5, 2.5, PROBLEM)
            .update(
                List.of(particle),
                speed,
                new DoubleSolution[] {localBest},
                leaders,
                selection,
                new ConstantValueStrategy(0.1));

    // Assert
    for (double value : updated[0]) {
      assertThat(Math.abs(value)).isLessThanOrEqualTo(0.5);
    }
  }
}
