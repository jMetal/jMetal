package org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

class AGEMOEAEnvironmentalSelectionTest {

  @Test
  void executeKeepsTheFeasibleSolutionWithTheDefaultConstrainedRanking() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 1);
    DoubleSolution feasible = solutionWithObjectives(problem, 1.0, 1.0);
    DoubleSolution infeasibleDominating = solutionWithObjectives(problem, 0.5, 0.5);
    infeasibleDominating.constraints()[0] = -1.0;

    List<DoubleSolution> selected =
        new AGEMOEAEnvironmentalSelection<DoubleSolution>(2)
            .execute(List.of(infeasibleDominating, feasible), 1);

    assertEquals(List.of(feasible), selected);
  }

  @Test
  void executeSortsThePopulationWithTheGivenRanking() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 1);
    DoubleSolution feasible = solutionWithObjectives(problem, 1.0, 1.0);
    DoubleSolution infeasibleDominating = solutionWithObjectives(problem, 0.5, 0.5);
    infeasibleDominating.constraints()[0] = -1.0;
    Ranking<DoubleSolution> rankingIgnoringConstraints =
        new FastNonDominatedSortRanking<>(new DefaultDominanceComparator<>());

    List<DoubleSolution> selected =
        new AGEMOEAEnvironmentalSelection<>(2, rankingIgnoringConstraints)
            .execute(List.of(feasible, infeasibleDominating), 1);

    assertEquals(List.of(infeasibleDominating), selected);
  }

  @Test
  void agemoea2EnvironmentalSelectionUsesTheGivenRanking() {
    Ranking<DoubleSolution> ranking =
        new FastNonDominatedSortRanking<>(new DefaultDominanceComparator<>());

    assertSame(ranking, new AGEMOEA2EnvironmentalSelection<>(2, ranking).ranking());
  }

  @Test
  void constructorRejectsANullRanking() {
    assertThrows(
        NullParameterException.class,
        () -> new AGEMOEAEnvironmentalSelection<DoubleSolution>(2, null));
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
