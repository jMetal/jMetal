package org.uma.jmetal.component.catalogue.common.solutionscreation.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Latin Hypercube Sampling (LHS) solutions creation strategy. Creates solutions using a
 * space-filling design that ensures each variable's range is divided into equal intervals and
 * sampled exactly once per interval.
 *
 * <p>LHS provides better coverage of the search space than pure random sampling by ensuring
 * that samples are more evenly distributed across each dimension.
 *
 * <p>The algorithm works by:
 * <ul>
 *   <li>Dividing each variable's range into n equal intervals (n = population size)
 *   <li>Creating a random permutation of interval indices for each dimension
 *   <li>Assigning one sample point from each interval per dimension
 * </ul>
 *
 * <p>This strategy is useful when:
 * <ul>
 *   <li>Good space coverage with few samples is desired
 *   <li>Reducing the risk of missing important regions of the search space
 *   <li>Initializing population for optimization problems with continuous variables
 * </ul>
 *
 * <p>Control parameters: None
 *
 * <p>Reference: M.D. McKay, R.J. Beckman, W.J. Conover, "A Comparison of Three Methods for
 * Selecting Values of Input Variables in the Analysis of Output from a Computer Code",
 * Technometrics, vol. 21, no. 2, pp. 239-245, 1979.
 * DOI: <a href="https://doi.org/10.2307/1268522">10.2307/1268522</a>
 *
 * @author Antonio J. Nebro
 */
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
    int[][] latinHypercube = new int[numberOfSolutionsToCreate][problem.numberOfVariables()];
    for (int dim = 0; dim < problem.numberOfVariables(); dim++) {
      List<Integer> permutation = getPermutation(numberOfSolutionsToCreate);
      for (int v = 0; v < numberOfSolutionsToCreate; v++) {
        latinHypercube[v][dim] = permutation.get(v);
      }
    }

    List<DoubleSolution> solutionList = new ArrayList<>(numberOfSolutionsToCreate);
    JMetalRandom random = JMetalRandom.getInstance();
    
    for (int i = 0; i < numberOfSolutionsToCreate; i++) {
      DoubleSolution newSolution =
          new DefaultDoubleSolution(problem.variableBounds(), problem.numberOfObjectives(), problem.numberOfConstraints());
      for (int j = 0; j < problem.numberOfVariables(); j++) {
        Bounds<Double> bounds = problem.variableBounds().get(j);
        double range = bounds.getUpperBound() - bounds.getLowerBound();
        double intervalSize = range / numberOfSolutionsToCreate;
        
        // Sample randomly within the interval
        int intervalIndex = latinHypercube[i][j];
        double randomOffset = random.nextDouble(0.0, 1.0);
        double value = bounds.getLowerBound() + (intervalIndex + randomOffset) * intervalSize;
        
        newSolution.variables().set(j, value);
      }

      solutionList.add(newSolution);
    }

    return solutionList;
  }

  /**
   * Generates a random permutation of integers from 0 to permutationLength-1 using the
   * Fisher-Yates shuffle algorithm with JMetalRandom for reproducibility.
   *
   * @param permutationLength Length of the permutation
   * @return A random permutation of integers [0, permutationLength-1]
   */
  private List<Integer> getPermutation(int permutationLength) {
    List<Integer> randomSequence = new ArrayList<>(permutationLength);

    for (int j = 0; j < permutationLength; j++) {
      randomSequence.add(j);
    }

    // Fisher-Yates shuffle using JMetalRandom for reproducibility
    JMetalRandom random = JMetalRandom.getInstance();
    for (int i = randomSequence.size() - 1; i > 0; i--) {
      int j = random.nextInt(0, i);
      // Swap elements i and j
      Integer temp = randomSequence.get(i);
      randomSequence.set(i, randomSequence.get(j));
      randomSequence.set(j, temp);
    }

    return randomSequence;
  }

  public int getNumberOfSolutionsToCreate() {
    return numberOfSolutionsToCreate;
  }
}
