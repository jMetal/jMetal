package org.uma.jmetal.component.catalogue.ea.replacement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.SMSEMOAReplacement;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.WFGHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

class SMSEMOAReplacementTest {
  @Test
  void shouldMatchFullRecomputeForRepeated2DSteadyStateUpdates() {
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    double[][] initialObjectives = {
        {1.0, 6.0}, {2.0, 5.0}, {3.0, 4.0}, {4.0, 3.0}, {5.0, 2.0}
    };
    double[][] offspringSequence = {
        {2.5, 3.5}, {1.5, 4.8}, {4.2, 1.9}, {3.8, 2.4}
    };

    assertRepeatedExactPathMatchesFullRecompute(problem, initialObjectives, offspringSequence);
  }

  @Test
  void shouldMatchLegacyFullRecomputeForRepeated3DSteadyStateUpdates() {
    DoubleProblem problem = new FakeDoubleProblem(2, 3, 0);
    double[][] initialObjectives = {
        {1.0, 7.0, 7.0},
        {2.0, 5.5, 5.0},
        {3.0, 4.0, 4.5},
        {4.0, 3.0, 3.5},
        {5.0, 2.0, 2.5}
    };
    double[][] offspringSequence = {
        {2.5, 4.1, 4.0},
        {1.8, 5.2, 4.6},
        {4.3, 2.4, 2.2},
        {3.6, 3.2, 3.0}
    };

    assertRepeated3DLegacyPathMatchesFullRecompute(problem, initialObjectives, offspringSequence);
  }

  @Test
  void shouldProduceDeterministicExactResultsFor4DProblems() {
    DoubleProblem problem = new FakeDoubleProblem(2, 4, 0);
    double[][] initialObjectives = {
        {1.0, 7.0, 7.0, 7.0},
        {2.0, 5.5, 5.0, 5.8},
        {3.0, 4.2, 4.4, 4.6},
        {4.0, 3.1, 3.4, 3.7},
        {5.0, 2.3, 2.8, 2.9}
    };
    double[][] offspringSequence = {
        {2.6, 4.6, 4.0, 4.3},
        {1.9, 5.1, 4.7, 4.8},
        {4.5, 2.5, 2.4, 2.6},
        {3.4, 3.3, 3.1, 3.2}
    };

    long originalSeed = JMetalRandom.getInstance().getSeed();
    try {
      List<DoubleSolution> firstRun =
          executeReplacementSequence(
              problem,
              initialObjectives,
              offspringSequence,
              true);

      List<DoubleSolution> secondRun =
          executeReplacementSequence(
              problem,
              initialObjectives,
              offspringSequence,
              true);

      assertPopulationObjectivesEqual(firstRun, secondRun);
    } finally {
      JMetalRandom.getInstance().setSeed(originalSeed);
    }
  }

  private void assertRepeatedExactPathMatchesFullRecompute(
      DoubleProblem problem, double[][] initialObjectives, double[][] offspringSequence) {
    List<DoubleSolution> optimizedPopulation = createPopulation(problem, initialObjectives);
    List<DoubleSolution> baselinePopulation = createPopulation(problem, initialObjectives);

    Replacement<DoubleSolution> optimizedReplacement =
        new SMSEMOAReplacement<>(
            new MergeNonDominatedSortRanking<>(),
            new org.uma.jmetal.qualityindicator.impl.hypervolume.impl.WFGHypervolume(
                new double[] {1.0, 1.0, 1.0}));

    for (double[] offspringObjectives : offspringSequence) {
      optimizedPopulation =
          optimizedReplacement.replace(
              optimizedPopulation, List.of(createSolution(problem, offspringObjectives)));

      Replacement<DoubleSolution> baselineReplacement =
          new SMSEMOAReplacement<>(
              new MergeNonDominatedSortRanking<>(),
              new org.uma.jmetal.qualityindicator.impl.hypervolume.impl.WFGHypervolume(
                  new double[] {1.0, 1.0, 1.0}));
      baselinePopulation =
          baselineReplacement.replace(
              baselinePopulation, List.of(createSolution(problem, offspringObjectives)));

      assertPopulationObjectivesEqual(optimizedPopulation, baselinePopulation);
    }
  }

  private void assertRepeated3DLegacyPathMatchesFullRecompute(
      DoubleProblem problem, double[][] initialObjectives, double[][] offspringSequence) {
    List<DoubleSolution> optimizedPopulation = createPopulation(problem, initialObjectives);
    List<DoubleSolution> legacyPopulation = createPopulation(problem, initialObjectives);

    Replacement<DoubleSolution> optimizedReplacement =
        new SMSEMOAReplacement<>(
            new FastNonDominatedSortRanking<>(),
            new WFGHypervolume(new double[] {1.0, 1.0, 1.0}));

    for (double[] offspringObjectives : offspringSequence) {
      DoubleSolution optimizedOffspring = createSolution(problem, offspringObjectives);
      optimizedPopulation =
          optimizedReplacement.replace(optimizedPopulation, List.of(optimizedOffspring));

      DoubleSolution legacyOffspring = createSolution(problem, offspringObjectives);
      legacyPopulation = applyExact3DReplacement(legacyPopulation, legacyOffspring);

      assertPopulationObjectivesEqual(optimizedPopulation, legacyPopulation);
    }
  }

  private List<DoubleSolution> executeReplacementSequence(
      DoubleProblem problem,
      double[][] initialObjectives,
      double[][] offspringSequence,
      boolean resetSeedBeforeRun) {
    if (resetSeedBeforeRun) {
      JMetalRandom.getInstance().setSeed(12345L);
    }

    Replacement<DoubleSolution> replacement =
        new SMSEMOAReplacement<>(
            new MergeNonDominatedSortRanking<>(),
            new org.uma.jmetal.qualityindicator.impl.hypervolume.impl.WFGHypervolume(
                new double[] {1.0, 1.0, 1.0}));

    List<DoubleSolution> population = createPopulation(problem, initialObjectives);
    for (double[] offspringObjectives : offspringSequence) {
      population = replacement.replace(population, List.of(createSolution(problem, offspringObjectives)));
    }

    return population;
  }

  private List<DoubleSolution> applyExact3DReplacement(
      List<DoubleSolution> currentPopulation, DoubleSolution offspring) {
    var ranking = new FastNonDominatedSortRanking<DoubleSolution>();
    List<DoubleSolution> jointPopulation = new ArrayList<>(currentPopulation.size() + 1);
    jointPopulation.addAll(currentPopulation);
    jointPopulation.add(offspring);

    ranking.compute(jointPopulation);

    int worstFrontIndex = ranking.getNumberOfSubFronts() - 1;
    List<DoubleSolution> worstFront = new ArrayList<>(ranking.getSubFront(worstFrontIndex));
    double[] contributions =
        computeExactContributionsByLeaveOneOut(
            createObjectiveMatrix(worstFront), calculateReferencePoint(jointPopulation));
    int leastContributorIndex = findLeastContributorIndex(contributions);

    List<DoubleSolution> resultPopulation = new ArrayList<>(currentPopulation.size());
    for (int frontIndex = 0; frontIndex < worstFrontIndex; frontIndex++) {
      resultPopulation.addAll(ranking.getSubFront(frontIndex));
    }
    for (int index = 0; index < worstFront.size(); index++) {
      if (index != leastContributorIndex) {
        resultPopulation.add(worstFront.get(index));
      }
    }

    return resultPopulation;
  }

  private List<DoubleSolution> createPopulation(DoubleProblem problem, double[][] objectiveMatrix) {
    List<DoubleSolution> population = new ArrayList<>(objectiveMatrix.length);
    for (double[] objectives : objectiveMatrix) {
      population.add(createSolution(problem, objectives));
    }
    return population;
  }

  private DoubleSolution createSolution(DoubleProblem problem, double[] objectives) {
    DoubleSolution solution = problem.createSolution();
    for (int index = 0; index < objectives.length; index++) {
      solution.objectives()[index] = objectives[index];
    }
    return solution;
  }

  private double[] calculateReferencePoint(List<DoubleSolution> solutionList) {
    int numberOfObjectives = solutionList.get(0).objectives().length;
    double[] referencePoint = new double[numberOfObjectives];

    for (int objective = 0; objective < numberOfObjectives; objective++) {
      double maxValue = Double.NEGATIVE_INFINITY;
      for (DoubleSolution solution : solutionList) {
        maxValue = Math.max(maxValue, solution.objectives()[objective]);
      }
      referencePoint[objective] = maxValue * 1.1;
    }

    return referencePoint;
  }

  private double[][] createObjectiveMatrix(List<DoubleSolution> front) {
    int numberOfObjectives = front.get(0).objectives().length;
    double[][] matrix = new double[front.size()][numberOfObjectives];

    for (int solutionIndex = 0; solutionIndex < front.size(); solutionIndex++) {
      System.arraycopy(front.get(solutionIndex).objectives(), 0, matrix[solutionIndex], 0,
          numberOfObjectives);
    }

    return matrix;
  }

  private double[] computeExactContributionsByLeaveOneOut(
      double[][] objectiveMatrix, double[] referencePoint) {
    double[] contributions = new double[objectiveMatrix.length];

    if (objectiveMatrix.length == 1) {
      contributions[0] =
          computeExactHypervolumeByInclusionExclusion(objectiveMatrix, referencePoint);
      return contributions;
    }

    double totalHypervolume =
        computeExactHypervolumeByInclusionExclusion(objectiveMatrix, referencePoint);
    for (int omittedIndex = 0; omittedIndex < objectiveMatrix.length; omittedIndex++) {
      contributions[omittedIndex] =
          totalHypervolume
              - computeExactHypervolumeByInclusionExclusion(
                  removePoint(objectiveMatrix, omittedIndex), referencePoint);
    }

    return contributions;
  }

  private double[][] removePoint(double[][] objectiveMatrix, int omittedIndex) {
    double[][] reducedFront = new double[objectiveMatrix.length - 1][objectiveMatrix[0].length];
    int reducedIndex = 0;
    for (int pointIndex = 0; pointIndex < objectiveMatrix.length; pointIndex++) {
      if (pointIndex == omittedIndex) {
        continue;
      }

      System.arraycopy(
          objectiveMatrix[pointIndex], 0, reducedFront[reducedIndex], 0, objectiveMatrix[0].length);
      reducedIndex++;
    }
    return reducedFront;
  }

  private double computeExactHypervolumeByInclusionExclusion(
      double[][] objectiveMatrix, double[] referencePoint) {
    double hypervolume = 0.0;
    int numberOfPoints = objectiveMatrix.length;
    int totalMasks = 1 << numberOfPoints;

    for (int mask = 1; mask < totalMasks; mask++) {
      double intersectionVolume = 1.0;
      for (int objective = 0; objective < referencePoint.length; objective++) {
        double minimumDistance = Double.POSITIVE_INFINITY;
        for (int pointIndex = 0; pointIndex < numberOfPoints; pointIndex++) {
          if ((mask & (1 << pointIndex)) == 0) {
            continue;
          }
          minimumDistance =
              Math.min(minimumDistance, referencePoint[objective] - objectiveMatrix[pointIndex][objective]);
        }
        intersectionVolume *= Math.max(0.0, minimumDistance);
      }

      if (Integer.bitCount(mask) % 2 == 1) {
        hypervolume += intersectionVolume;
      } else {
        hypervolume -= intersectionVolume;
      }
    }

    return hypervolume;
  }

  private int findLeastContributorIndex(double[] contributions) {
    int leastContributorIndex = 0;
    double leastContribution = contributions[0];

    for (int index = 1; index < contributions.length; index++) {
      if (contributions[index] <= leastContribution) {
        leastContribution = contributions[index];
        leastContributorIndex = index;
      }
    }

    return leastContributorIndex;
  }

  private void assertPopulationObjectivesEqual(
      List<DoubleSolution> firstPopulation, List<DoubleSolution> secondPopulation) {
    assertEquals(firstPopulation.size(), secondPopulation.size());

    double[][] firstMatrix = sortObjectives(firstPopulation);
    double[][] secondMatrix = sortObjectives(secondPopulation);

    for (int row = 0; row < firstMatrix.length; row++) {
      assertEquals(Arrays.toString(firstMatrix[row]), Arrays.toString(secondMatrix[row]));
    }
  }

  private double[][] sortObjectives(List<DoubleSolution> population) {
    double[][] matrix = population.stream().map(solution -> solution.objectives().clone())
        .toArray(double[][]::new);

    Arrays.sort(
        matrix,
        Comparator.comparingDouble((double[] objectives) -> objectives[0])
            .thenComparingDouble(objectives -> objectives.length > 1 ? objectives[1] : 0.0)
            .thenComparingDouble(objectives -> objectives.length > 2 ? objectives[2] : 0.0)
            .thenComparingDouble(objectives -> objectives.length > 3 ? objectives[3] : 0.0));

    return matrix;
  }
}
