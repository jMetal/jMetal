package org.uma.jmetal.component.catalogue.common.solutionscreation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.bounds.Bounds;

public class LatinHypercubeSamplingSolutionsCreation
    implements SolutionsCreation<DoubleSolution> {
  private final int numberOfSolutionsToCreate;
  private final DoubleProblem problem;

  public LatinHypercubeSamplingSolutionsCreation(
      DoubleProblem problem, int numberOfSolutionsToCreate) {
    this.problem = problem;
    this.numberOfSolutionsToCreate = numberOfSolutionsToCreate;
  }

  public List<DoubleSolution> create() {
    int[][] latinHypercube = new int[numberOfSolutionsToCreate][problem.getNumberOfVariables()];
    for (int dim = 0; dim < problem.getNumberOfVariables(); dim++) {
      List<Integer> permutation = getPermutation(numberOfSolutionsToCreate);
      for (int v = 0; v < numberOfSolutionsToCreate; v++) {
        latinHypercube[v][dim] = permutation.get(v);
      }
    }

    List<DoubleSolution> solutionList = new ArrayList<>(numberOfSolutionsToCreate);
    for (int i = 0; i < numberOfSolutionsToCreate; i++) {
      DoubleSolution newSolution =
          new DefaultDoubleSolution(problem.getVariableBounds(), problem.getNumberOfObjectives(), 0);
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        Bounds<Double> bounds = problem.getVariableBounds().get(j);
        newSolution.variables().set(
            j,
            NormalizeUtils.normalize(
                latinHypercube[i][j],
                bounds.getLowerBound(),
                bounds.getUpperBound(),
                0,
                numberOfSolutionsToCreate));
      }

      solutionList.add(newSolution);
    }

    return solutionList;
  }

  private List<Integer> getPermutation(int permutationLength) {
    List<Integer> randomSequence = IntStream.range(0, permutationLength).boxed().collect(Collectors.toCollection(() -> new ArrayList<>(permutationLength)));

      java.util.Collections.shuffle(randomSequence);

    return randomSequence;
  }
}
