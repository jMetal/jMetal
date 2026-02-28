package org.uma.jmetal.component.catalogue.common.solutionscreation.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.random.HaltonSequenceGenerator;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Halton quasi-random sequence solutions creation strategy. Creates solutions using a
 * low-discrepancy Halton sequence based on the van der Corput sequence with successive prime bases.
 *
 * <p>Halton sequences produce well-distributed points in moderate dimensions. Unlike Sobol
 * sequences, they do not require a power-of-two number of samples, so the requested population
 * size is used directly. A Cranley-Patterson rotation (random shift modulo 1) is applied to each
 * dimension to introduce randomisation while preserving the low-discrepancy property.
 *
 * <p>This strategy is useful when:
 * <ul>
 *   <li>Uniform coverage of the search space is important</li>
 *   <li>The population size is not a power of two</li>
 *   <li>The number of variables is moderate (Halton quality degrades in very high dimensions)</li>
 * </ul>
 *
 * <p>Control parameters: None
 *
 * <p>Reference: J.H. Halton, "On the efficiency of certain quasi-random sequences of points in
 * evaluating multi-dimensional integrals", Numerische Mathematik, vol. 2, pp. 84-90, 1960.
 * DOI: <a href="https://doi.org/10.1007/BF01386213">10.1007/BF01386213</a>
 *
 * @author Antonio J. Nebro
 */
public class HaltonSolutionsCreation implements SolutionsCreation<DoubleSolution> {

  private final int numberOfSolutionsToCreate;
  private final DoubleProblem problem;

  public HaltonSolutionsCreation(DoubleProblem problem, int numberOfSolutionsToCreate) {
    this.problem = problem;
    this.numberOfSolutionsToCreate = numberOfSolutionsToCreate;
  }

  @Override
  public List<DoubleSolution> create() {
    int numVariables = problem.numberOfVariables();
    var halton = new HaltonSequenceGenerator(numVariables);

    // Generate Halton samples in [0, 1]^d
    double[][] samples = new double[numberOfSolutionsToCreate][numVariables];
    for (int i = 0; i < numberOfSolutionsToCreate; i++) {
      samples[i] = halton.nextVector();
    }

    // Cranley-Patterson rotation: add a uniform random shift per dimension, mod 1
    JMetalRandom random = JMetalRandom.getInstance();
    double[] shifts = new double[numVariables];
    for (int j = 0; j < numVariables; j++) {
      shifts[j] = random.nextDouble(0.0, 1.0);
    }
    for (int i = 0; i < numberOfSolutionsToCreate; i++) {
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

  public int getNumberOfSolutionsToCreate() {
    return numberOfSolutionsToCreate;
  }
}
