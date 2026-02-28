package org.uma.jmetal.component.catalogue.common.solutionscreation.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.random.SobolSequenceGenerator;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Sobol quasi-random sequence solutions creation strategy. Creates solutions using a
 * low-discrepancy Sobol sequence that provides better uniformity than pseudo-random sampling.
 *
 * <p>Sobol sequences are deterministic quasi-random sequences that fill the search space more
 * evenly than pseudo-random numbers. A Cranley-Patterson rotation (random shift modulo 1) is
 * applied to each dimension to introduce randomisation while preserving the low-discrepancy
 * property.
 *
 * <p>Because Sobol sequences require a power-of-two number of points for optimal balance, this
 * implementation generates the smallest power of two greater than or equal to the requested
 * population size and then truncates to the desired count (matching the approach used in scipy).
 *
 * <p>This strategy is useful when:
 * <ul>
 *   <li>Uniform coverage of the search space is important</li>
 *   <li>The number of samples is limited and every point should contribute new information</li>
 *   <li>Initialising populations for optimisation problems with continuous variables</li>
 * </ul>
 *
 * <p>Control parameters: None
 *
 * <p>Reference: I.M. Sobol', "On the distribution of points in a cube and the approximate
 * evaluation of integrals", USSR Computational Mathematics and Mathematical Physics,
 * vol. 7, no. 4, pp. 86-112, 1967.
 * DOI: <a href="https://doi.org/10.1016/0041-5553(67)90144-9">10.1016/0041-5553(67)90144-9</a>
 *
 * @author Antonio J. Nebro
 */
public class SobolSolutionsCreation implements SolutionsCreation<DoubleSolution> {

  private final int numberOfSolutionsToCreate;
  private final DoubleProblem problem;

  public SobolSolutionsCreation(DoubleProblem problem, int numberOfSolutionsToCreate) {
    this.problem = problem;
    this.numberOfSolutionsToCreate = numberOfSolutionsToCreate;
  }

  @Override
  public List<DoubleSolution> create() {
    int numVariables = problem.numberOfVariables();
    var sobol = new SobolSequenceGenerator(numVariables);

    // Compute the smallest power of two >= numberOfSolutionsToCreate
    int nPow2 = smallestPowerOfTwoGreaterOrEqual(Math.max(numberOfSolutionsToCreate, 2));

    // Generate Sobol samples in [0, 1]^d
    double[][] samples = new double[nPow2][numVariables];
    for (int i = 0; i < nPow2; i++) {
      samples[i] = sobol.nextVector();
    }

    // Cranley-Patterson rotation: add a uniform random shift per dimension, mod 1
    JMetalRandom random = JMetalRandom.getInstance();
    double[] shifts = new double[numVariables];
    for (int j = 0; j < numVariables; j++) {
      shifts[j] = random.nextDouble(0.0, 1.0);
    }
    for (int i = 0; i < nPow2; i++) {
      for (int j = 0; j < numVariables; j++) {
        samples[i][j] = (samples[i][j] + shifts[j]) % 1.0;
      }
    }

    // Build solutions, scaling from [0, 1] to variable bounds
    List<DoubleSolution> solutionList = new ArrayList<>(numberOfSolutionsToCreate);
    for (int i = 0; i < numberOfSolutionsToCreate; i++) {
      DoubleSolution newSolution = new DefaultDoubleSolution(
          problem.variableBounds(), problem.numberOfObjectives(), problem.numberOfConstraints());

      for (int j = 0; j < numVariables; j++) {
        Bounds<Double> bounds = problem.variableBounds().get(j);
        double span = bounds.getUpperBound() - bounds.getLowerBound();
        double value = bounds.getLowerBound() + samples[i][j] * span;
        newSolution.variables().set(j, value);
      }

      solutionList.add(newSolution);
    }

    return solutionList;
  }

  /**
   * Returns the smallest power of two that is greater than or equal to the given value.
   *
   * @param n a positive integer
   * @return the smallest power of two &ge; n
   */
  private int smallestPowerOfTwoGreaterOrEqual(int n) {
    int power = 1;
    while (power < n) {
      power *= 2;
    }
    return power;
  }

  public int getNumberOfSolutionsToCreate() {
    return numberOfSolutionsToCreate;
  }
}
