package org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.ranking.impl.StrengthRanking;

class SurvivalScoreComparatorTest {

  @Test
  void compareReadsTheRanksOfTheFastNonDominatedSortingByDefault() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    DoubleSolution dominating = solutionWithObjectives(problem, 0.0, 0.0);
    DoubleSolution dominated = solutionWithObjectives(problem, 1.0, 1.0);
    new FastNonDominatedSortRanking<DoubleSolution>().compute(List.of(dominating, dominated));

    SurvivalScoreComparator<DoubleSolution> comparator = new SurvivalScoreComparator<>();

    assertEquals(-1, comparator.compare(dominating, dominated));
    assertEquals(1, comparator.compare(dominated, dominating));
  }

  @Test
  void compareReadsTheRanksOfTheGivenRanking() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    DoubleSolution dominating = solutionWithObjectives(problem, 0.0, 0.0);
    DoubleSolution dominated = solutionWithObjectives(problem, 1.0, 1.0);
    Ranking<DoubleSolution> ranking = new StrengthRanking<>();
    ranking.compute(List.of(dominating, dominated));

    SurvivalScoreComparator<DoubleSolution> comparator = new SurvivalScoreComparator<>(ranking);

    assertEquals(-1, comparator.compare(dominating, dominated));
    // The default comparator finds no rank of the fast non-dominated sorting in them
    assertEquals(0, new SurvivalScoreComparator<DoubleSolution>().compare(dominating, dominated));
  }

  @Test
  void compareBreaksTiesInRankByTheHigherSurvivalScore() {
    FakeDoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    DoubleSolution first = solutionWithObjectives(problem, 0.0, 1.0);
    DoubleSolution second = solutionWithObjectives(problem, 1.0, 0.0);
    Ranking<DoubleSolution> ranking = new FastNonDominatedSortRanking<>();
    ranking.compute(List.of(first, second));
    first.attributes().put(AGEMOEAEnvironmentalSelection.getAttributeId(), 2.0);
    second.attributes().put(AGEMOEAEnvironmentalSelection.getAttributeId(), 1.0);

    SurvivalScoreComparator<DoubleSolution> comparator = new SurvivalScoreComparator<>(ranking);

    assertEquals(-1, comparator.compare(first, second));
  }

  private static DoubleSolution solutionWithObjectives(
      FakeDoubleProblem problem, double firstObjective, double secondObjective) {
    DoubleSolution solution = problem.createSolution();
    solution.objectives()[0] = firstObjective;
    solution.objectives()[1] = secondObjective;

    return solution;
  }
}
