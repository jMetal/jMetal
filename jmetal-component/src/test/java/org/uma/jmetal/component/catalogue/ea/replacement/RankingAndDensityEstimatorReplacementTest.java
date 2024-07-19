package org.uma.jmetal.component.catalogue.ea.replacement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

class RankingAndDensityEstimatorReplacementTest {
  @Test
  void replacementWorksProperlyWith20Solutions() {
    DoubleProblem problem = new FakeDoubleProblem(2, 3, 0);

    double[][] objectiveValues =
        new double[][] {
          {0.07336635446929285, 5.603220188306353},
          {0.43014627330305144, 5.708218645222796},
          {0.7798429543256261, 5.484124010814388},
          {0.49045165212590114, 5.784519349470215},
          {0.843511347097429, 5.435997012510192},
          {0.9279447115273152, 5.285778278767635},
          {0.5932205233840192, 6.887287053050965},
          {0.9455066295318578, 5.655731733404245},
          {0.9228750336383887, 4.8155865600591605},
          {0.022333588871048637, 5.357300649511081},
          {0.07336635446929285, 4.955242979343399},
          {0.9228750336383887, 4.368497851779355},
          {0.8409372615592949, 4.7393211155296315},
          {0.8452552028963248, 5.729254698390962},
          {0.4814413714745963, 4.814059473570379},
          {0.48149159013716136, 5.214371319566827},
          {0.9455066295318578, 5.024547164793679},
          {0.843511347097429, 4.823648491299312},
          {0.06050659328388003, 4.97308823770029},
          {0.07336635446929285, 5.603220188306353}
        };

    List<DoubleSolution> solutionList = new ArrayList<>(objectiveValues.length);
    IntStream.range(0, objectiveValues.length)
        .forEach(
            i -> {
              DoubleSolution solution = problem.createSolution();
              solution.objectives()[0] = objectiveValues[i][0];
              solution.objectives()[1] = objectiveValues[i][1];
              solutionList.add(solution);
            });

    List<DoubleSolution> population = solutionList.subList(0, 10);
    List<DoubleSolution> offspringPopulation = solutionList.subList(10, 20);

    Ranking<DoubleSolution> ranking = new FastNonDominatedSortRanking<>();
    Replacement<DoubleSolution> replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, new CrowdingDistanceDensityEstimator<>(), Replacement.RemovalPolicy.ONE_SHOT);

    var nextPopulation = replacement.replace(population, offspringPopulation);

    assertEquals(10, nextPopulation.size());
  }

  @Test
  void replacementWorksProperlyWith16Solutions() {
    DoubleProblem problem = new FakeDoubleProblem(2, 3, 0);

    double[][] objectiveValues =
        new double[][] {
                {0.5655121242688416, 122.05467685520247},
                {0.5655121250339731, 111.9985560901313},
                {0.43014627330305144, 120.42155746003654},
                {0.07336635446929285, 161.04962975606725},
                {0.43014627330305144, 160.40525052973234},
                {0.6762217810478277, 179.7511934374773},
                {0.43014627330305144, 151.06343252458754},
                {0.43014627330305144, 136.3722551798597},
                {0.7798429543256261, 88.00557995059926},
                {0.07336635446929285, 161.04962975606725},
                {0.43014627330305144, 120.42155746003654},
                {0.5655121250339731, 111.9985560901313},
                {0.5460487092960259, 116.39993882084455},
                {0.07336635446929285, 167.51690132793735},
                {0.5655121242688416, 122.05467685520247},
                {0.43014627330305144, 136.3722551798597}
        };

    List<DoubleSolution> solutionList = new ArrayList<>(objectiveValues.length);
    IntStream.range(0, objectiveValues.length)
        .forEach(
            i -> {
              DoubleSolution solution = problem.createSolution();
              solution.objectives()[0] = objectiveValues[i][0];
              solution.objectives()[1] = objectiveValues[i][1];
              solutionList.add(solution);
            });

    List<DoubleSolution> population = solutionList.subList(0, 8);
    List<DoubleSolution> offspringPopulation = solutionList.subList(8, 16);

    Ranking<DoubleSolution> ranking = new FastNonDominatedSortRanking<>();
    Replacement<DoubleSolution> replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, new CrowdingDistanceDensityEstimator<>(), Replacement.RemovalPolicy.ONE_SHOT);

    var nextPopulation = replacement.replace(population, offspringPopulation);

    assertEquals(8, nextPopulation.size());
  }
}
