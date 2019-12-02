package org.uma.jmetal.component.initialsolutioncreation.impl;

import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

public class ScatterSearchSolutionsCreation implements InitialSolutionsCreation<DoubleSolution> {
  private final int numberOfSolutionsToCreate;
  private final DoubleProblem problem;
  private final int numberOfSubRanges;
  protected int[] sumOfFrequencyValues;
  protected int[] sumOfReverseFrequencyValues;
  protected int[][] frequency;
  protected int[][] reverseFrequency;

  public ScatterSearchSolutionsCreation(
      DoubleProblem problem, int numberOfSolutionsToCreate, int numberOfSubRanges) {
    this.problem = problem;
    this.numberOfSolutionsToCreate = numberOfSolutionsToCreate;
    this.numberOfSubRanges = numberOfSubRanges;

    sumOfFrequencyValues = new int[problem.getNumberOfVariables()];
    sumOfReverseFrequencyValues = new int[problem.getNumberOfVariables()];
    frequency = new int[numberOfSubRanges][problem.getNumberOfVariables()];
    reverseFrequency = new int[numberOfSubRanges][problem.getNumberOfVariables()];
  }

  public List<DoubleSolution> create() {
    List<DoubleSolution> solutionList = new ArrayList<>(numberOfSolutionsToCreate);

    for (int i = 0; i < numberOfSolutionsToCreate; i++) {
      List<Double> variables = generateVariables();
      DoubleSolution newSolution =
          new DefaultDoubleSolution(problem.getBounds(), problem.getNumberOfObjectives());
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        newSolution.setVariable(j, variables.get(j));
      }

      solutionList.add(newSolution);
    }

    return solutionList;
  }

  private List<Double> generateVariables() {
    List<Double> vars = new ArrayList<>(problem.getNumberOfVariables());

    double value;
    int range;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      sumOfReverseFrequencyValues[i] = 0;
      for (int j = 0; j < numberOfSubRanges; j++) {
        reverseFrequency[j][i] = sumOfFrequencyValues[i] - frequency[j][i];
        sumOfReverseFrequencyValues[i] += reverseFrequency[j][i];
      }

      if (sumOfReverseFrequencyValues[i] == 0) {
        range = JMetalRandom.getInstance().nextInt(0, numberOfSubRanges - 1);
      } else {
        value = JMetalRandom.getInstance().nextInt(0, sumOfReverseFrequencyValues[i] - 1);
        range = 0;
        while (value > reverseFrequency[range][i]) {
          value -= reverseFrequency[range][i];
          range++;
        }
      }

      frequency[range][i]++;
      sumOfFrequencyValues[i]++;

      double low =
          problem.getLowerBound(i)
              + range * (problem.getUpperBound(i) - problem.getLowerBound(i)) / numberOfSubRanges;
      double high = low + (problem.getUpperBound(i) - problem.getLowerBound(i)) / numberOfSubRanges;

      vars.add(i, JMetalRandom.getInstance().nextDouble(low, high));
    }

    return vars;
  }
}
