package org.uma.jmetal.algorithm.multiobjective.abyss;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

/**
 * Created by ajnebro on 11/6/15.
 */
public class ABYSS extends AbstractABYSS<DoubleSolution> {
  protected JMetalRandom randomGenerator ;

  /**
   * These variables are used in the diversification method.
   */
  protected int numberOfSubRanges;
  protected int[] sumOfFrequencyValues;
  protected int[] sumOfReverseFrequencyValues;
  protected int[][] frequency;
  protected int[][] reverseFrequency;

  public ABYSS(DoubleProblem problem, int maxEvaluations, int populationSize, int referenceSet1Size,
      int referenceSet2Size, int archiveSize, Archive<DoubleSolution> archive,
      LocalSearchOperator<DoubleSolution> localSearch,
      CrossoverOperator<List<DoubleSolution>, List<DoubleSolution>> crossoverOperator,
      int numberOfSubRanges) {
    super(problem, maxEvaluations, populationSize, referenceSet1Size, referenceSet2Size, archiveSize,
        archive, localSearch, crossoverOperator) ;

    this.numberOfSubRanges = numberOfSubRanges ;
    randomGenerator = JMetalRandom.getInstance() ;

    sumOfFrequencyValues       = new int[problem.getNumberOfVariables()] ;
    sumOfReverseFrequencyValues = new int[problem.getNumberOfVariables()] ;
    frequency       = new int[numberOfSubRanges][problem.getNumberOfVariables()] ;
    reverseFrequency = new int[numberOfSubRanges][problem.getNumberOfVariables()] ;

    evaluations = 0 ;
  }

  @Override protected DoubleSolution diversificationGeneration() {
    DoubleSolution solution = problem.createSolution();

    double value;
    int range;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      sumOfReverseFrequencyValues[i] = 0;
      for (int j = 0; j < numberOfSubRanges; j++) {
        reverseFrequency[j][i] = sumOfFrequencyValues[i] - frequency[j][i];
        sumOfReverseFrequencyValues[i] += reverseFrequency[j][i];
      }

      if (sumOfReverseFrequencyValues[i] == 0) {
        range = randomGenerator.nextInt(0, numberOfSubRanges - 1);
      } else {
        value = randomGenerator.nextInt(0, sumOfReverseFrequencyValues[i] - 1);
        range = 0;
        while (value > reverseFrequency[range][i]) {
          value -= reverseFrequency[range][i];
          range++;
        }
      }

      frequency[range][i]++;
      sumOfFrequencyValues[i]++;

      double low = ((DoubleProblem)problem).getLowerBound(i) + range *
          (((DoubleProblem)problem).getUpperBound(i) -
              ((DoubleProblem)problem).getLowerBound(i)) / numberOfSubRanges;
      double high = low + (((DoubleProblem)problem).getUpperBound(i) -
          ((DoubleProblem)problem).getLowerBound(i)) / numberOfSubRanges;

      value = randomGenerator.nextDouble(low, high);
      solution.setVariableValue(i, value);
    }
    return solution;
  }

  @Override
  public void initializationPhase() {
    super.initializationPhase();

    for (int i = 0 ; i < getPopulationSize(); i++) {
      problem.evaluate(getPopulation().get(i));
      evaluations++ ;
    }
  }

  @Override protected DoubleSolution improvement(DoubleSolution solution) {
    DoubleSolution improvedSolution = (DoubleSolution) solution.copy();
    return solution;
  }

}
