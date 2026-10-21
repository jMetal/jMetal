package org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * The survival score of AGE-MOEA as in the authors' code (PlatEMO): the solutions of the fronts after
 * the first are scored with the inverse of their distance to the ideal point, and the front is
 * normalized with the extreme points and the hyperplane of the front translated to its ideal point.
 */
class AGEMOEASurvivalScoreTest {

  /**
   * A first front (0, 1), (0.5, 0.5), (1, 0), whose geometry gives P = 1, a second front whose L1
   * distances to the ideal point (0, 0) are 1.6, 1.2, 1.7 and 1.45, and a third front.
   */
  private static List<DoubleSolution> populationWithThreeFronts(FakeDoubleProblem problem) {
    return List.of(
        solutionWithObjectives(problem, 0.0, 1.0),
        solutionWithObjectives(problem, 0.5, 0.5),
        solutionWithObjectives(problem, 1.0, 0.0),
        solutionWithObjectives(problem, 0.1, 1.5),
        solutionWithObjectives(problem, 0.6, 0.6),
        solutionWithObjectives(problem, 1.6, 0.1),
        solutionWithObjectives(problem, 0.55, 0.9),
        solutionWithObjectives(problem, 3.0, 3.0));
  }

  @Test
  void executeKeepsTheSolutionsOfTheCutFrontClosestToTheIdealPoint() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    List<DoubleSolution> population = populationWithThreeFronts(problem);

    List<DoubleSolution> selected =
        new AGEMOEAEnvironmentalSelection<DoubleSolution>(2).execute(population, 5);

    // The first front and the two solutions of the second one at distances 1.2 and 1.45
    assertEquals(
        List.of(population.get(0), population.get(1), population.get(2), population.get(4),
            population.get(6)),
        selected.stream().sorted((a, b) -> population.indexOf(a) - population.indexOf(b)).toList());
  }

  @Test
  void theSurvivalScoreOfALaterFrontPrefersTheSolutionCloserToTheIdealPoint() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    List<DoubleSolution> population = populationWithThreeFronts(problem);

    new AGEMOEAEnvironmentalSelection<DoubleSolution>(2).execute(population, 5);

    // (0.6, 0.6), at distance 1.2, against (0.1, 1.5), at 1.6: the scores are the inverses
    String score = AGEMOEAEnvironmentalSelection.getAttributeId();
    assertEquals(1.0 / 1.2, (double) population.get(4).attributes().get(score), 1e-12);
    assertEquals(1.0 / 1.6, (double) population.get(3).attributes().get(score), 1e-12);
    assertEquals(
        -1,
        new SurvivalScoreComparator<DoubleSolution>().compare(population.get(4), population.get(3)));
  }

  @Test
  void theExtremePointsAndTheHyperplaneAreThoseOfTheFrontTranslatedToItsIdealPoint() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    List<DoubleSolution> front =
        List.of(
            solutionWithObjectives(problem, 2.0, -10.0),
            solutionWithObjectives(problem, 5.0, -20.0),
            solutionWithObjectives(problem, 10.0, -30.0),
            solutionWithObjectives(problem, 20.0, -35.0));
    AGEMOEAEnvironmentalSelection<DoubleSolution> environmentalSelection =
        new AGEMOEAEnvironmentalSelection<>(2);

    List<DoubleSolution> extremes = environmentalSelection.findExtremes(front);
    List<Double> intercepts = environmentalSelection.constructHyperplane(front, extremes);

    // Translated by the ideal point (2, -35), the front goes from (0, 25) to (18, 0); without the
    // translation (2, -10) was the extreme point of both axes
    assertEquals(List.of(front.get(3), front.get(0)), extremes);
    assertEquals(18.0, intercepts.get(0), 1e-12);
    assertEquals(25.0, intercepts.get(1), 1e-12);
  }

  @Test
  void executeScoresAFrontWithANegativeObjective() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    List<DoubleSolution> front =
        List.of(
            solutionWithObjectives(problem, 2.0, -10.0),
            solutionWithObjectives(problem, 5.0, -20.0),
            solutionWithObjectives(problem, 10.0, -30.0),
            solutionWithObjectives(problem, 20.0, -35.0));

    new AGEMOEAEnvironmentalSelection<DoubleSolution>(2).execute(front, 4);

    String score = AGEMOEAEnvironmentalSelection.getAttributeId();
    assertTrue(Double.isInfinite((double) front.get(0).attributes().get(score)));
    assertTrue(Double.isInfinite((double) front.get(3).attributes().get(score)));
    for (DoubleSolution solution : front.subList(1, 3)) {
      double value = (double) solution.attributes().get(score);
      assertTrue(Double.isFinite(value) && value > 0.0, String.valueOf(value));
    }
  }

  @Test
  void constructHyperplaneFallsBackToTheMaximaWhenTheExtremePointsCoincide() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    List<DoubleSolution> front =
        List.of(
            solutionWithObjectives(problem, 1.0, 3.0),
            solutionWithObjectives(problem, 2.0, 2.0),
            solutionWithObjectives(problem, 4.0, 1.0));

    List<Double> intercepts =
        new AGEMOEAEnvironmentalSelection<DoubleSolution>(2)
            .constructHyperplane(front, List.of(front.get(1), front.get(1)));

    // The maxima of the front translated by its ideal point (1, 1)
    assertEquals(List.of(3.0, 2.0), intercepts);
  }

  @Test
  void constructHyperplaneFallsBackToTheMaximaWhenAnInterceptIsNegative() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    List<DoubleSolution> front =
        List.of(
            solutionWithObjectives(problem, 1.0, 0.0),
            solutionWithObjectives(problem, 2.0, 0.5),
            solutionWithObjectives(problem, 0.0, 1.0));

    // The line through (1, 0) and (2, 0.5) cuts the second axis at -2
    List<Double> intercepts =
        new AGEMOEAEnvironmentalSelection<DoubleSolution>(2)
            .constructHyperplane(front, List.of(front.get(0), front.get(1)));

    assertEquals(List.of(2.0, 1.0), intercepts);
  }

  @Test
  void computeGeometryWithoutACentralPointAssumesAFlatFront() {
    TestEnvironmentalSelection environmentalSelection = new TestEnvironmentalSelection(2);

    double p =
        environmentalSelection.geometry(
            List.of(new double[] {1.0, 0.0}, new double[] {0.0, 1.0}), List.of(0, 1));

    assertEquals(1.0, p);
    assertFalse(Double.isNaN(p));
  }

  private static DoubleSolution solutionWithObjectives(
      FakeDoubleProblem problem, double firstObjective, double secondObjective) {
    DoubleSolution solution = problem.createSolution();
    solution.objectives()[0] = firstObjective;
    solution.objectives()[1] = secondObjective;

    return solution;
  }

  private static class TestEnvironmentalSelection
      extends AGEMOEAEnvironmentalSelection<DoubleSolution> {

    TestEnvironmentalSelection(int numberOfObjectives) {
      super(numberOfObjectives);
    }

    double geometry(List<double[]> normalizedFront, List<Integer> extremePoints) {
      return computeGeometry(normalizedFront, extremePoints);
    }
  }
}
