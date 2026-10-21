package org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class AGEMOEAEnvironmentalSelectionTest {

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
  void assignConvergenceScoreNormalizesObjectivesWhenInterceptsAreAvailable() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    DoubleSolution solution = solutionWithObjectives(problem, 1.0, 1.0);

    TestEnvironmentalSelection environmentalSelection = new TestEnvironmentalSelection(2);
    environmentalSelection.setState(1.0, List.of(2.0, 2.0));

    environmentalSelection.assignConvergenceScores(List.of(solution));

    assertEquals(
        1.0,
        solution.attributes().get(AGEMOEAEnvironmentalSelection.getAttributeId()));
  }

  @Test
  void executeResetsTheInternalStateBeforeScoringAnotherPopulation() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    TestEnvironmentalSelection environmentalSelection = new TestEnvironmentalSelection(2);

    List<DoubleSolution> firstPopulation =
        List.of(
            solutionWithObjectives(problem, 1.0, 0.0),
            solutionWithObjectives(problem, 0.0, 1.0),
            solutionWithObjectives(problem, 1.0 / Math.sqrt(2.0), 1.0 / Math.sqrt(2.0)));

    environmentalSelection.execute(firstPopulation, 3);

    DoubleSolution dominatedSolution = solutionWithObjectives(problem, 2.0, 2.0);
    List<DoubleSolution> secondPopulation =
        List.of(
            solutionWithObjectives(problem, 0.0, 1.0),
            solutionWithObjectives(problem, 1.0, 0.0),
            dominatedSolution);

    environmentalSelection.execute(secondPopulation, 3);

    // The inverse of the distance (L1, P reset to 1) to the ideal point: 1 / 4
    assertEquals(
        0.25,
        dominatedSolution.attributes().get(AGEMOEAEnvironmentalSelection.getAttributeId()));
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

    void setState(double p, List<Double> intercepts) {
      P = p;
      this.intercepts = new ArrayList<>(intercepts);
    }

    void assignConvergenceScores(List<DoubleSolution> front) {
      assignConvergenceScore(front);
    }
  }
}
