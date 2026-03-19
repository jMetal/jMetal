package org.uma.jmetal.component.catalogue.common.solutionscreation.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Cauchy-based solutions creation strategy. Creates solutions using a Cauchy
 * distribution
 * centered at the middle of each variable's bounds.
 *
 * <p>
 * Unlike uniform sampling (which assumes all regions are equally likely) or
 * normal sampling
 * (which strongly concentrates around the center), the Cauchy distribution has
 * "heavy tails".
 * This means it concentrates many solutions near the center of the search
 * space, but occasionally
 * generates solutions extremely far away from the center.
 *
 * <p>
 * This strategy is particularly useful for objective landscapes with massive
 * local optima,
 * where the algorithm needs both intensive local exploitation (the center) and
 * distant jumps
 * (the heavy tails) to escape basins of attraction. Fast Evolutionary
 * Programming (FEP) is
 * a classic example of exploiting Cauchy distributions.
 *
 * <p>
 * If a generated value falls outside the variable bounds, it is clamped to the
 * bounds.
 * By default, the scale factor is 0.1, meaning the scale of the Cauchy
 * distribution is 10%
 * of the total range of the variable.
 *
 * <p>
 * Control parameters:
 * <ul>
 * <li>scaleFactor: Controls the spread of the distribution relative to the
 * variable bounds.</li>
 * </ul>
 *
 * <p>
 * Reference: X. Yao, Y. Liu and G. Lin, "Evolutionary programming made faster,"
 * IEEE Transactions on Evolutionary Computation, vol. 3, no. 2, pp. 82-102,
 * July 1999.
 * DOI: <a href="https://doi.org/10.1109/4235.771163">10.1109/4235.771163</a>
 *
 * @author Antonio J. Nebro
 */
public class CauchySolutionsCreation implements SolutionsCreation<DoubleSolution> {

  private final int numberOfSolutionsToCreate;
  private final DoubleProblem problem;
  private final double scaleFactor;

  /**
   * Constructor with default scale factor (0.1).
   *
   * @param problem                   The optimization problem
   * @param numberOfSolutionsToCreate Number of solutions to create
   */
  public CauchySolutionsCreation(DoubleProblem problem, int numberOfSolutionsToCreate) {
    this(problem, numberOfSolutionsToCreate, 0.1);
  }

  /**
   * Constructor with an explicit scale factor.
   *
   * @param problem                   The optimization problem
   * @param numberOfSolutionsToCreate Number of solutions to create
   * @param scaleFactor               A strictly positive value (typically between
   *                                  0.05 and 0.2)
   */
  public CauchySolutionsCreation(DoubleProblem problem, int numberOfSolutionsToCreate, double scaleFactor) {
    Check.notNull(problem, "problem");
    Check.valueIsPositive(numberOfSolutionsToCreate, "numberOfSolutionsToCreate");
    Check.valueIsPositive(scaleFactor, "scaleFactor");
    this.problem = problem;
    this.numberOfSolutionsToCreate = numberOfSolutionsToCreate;
    this.scaleFactor = scaleFactor;
  }

  @Override
  public List<DoubleSolution> create() {
    List<DoubleSolution> solutionList = new ArrayList<>(numberOfSolutionsToCreate);
    JMetalRandom random = JMetalRandom.getInstance();

    for (int i = 0; i < numberOfSolutionsToCreate; i++) {
      DoubleSolution solution = new DefaultDoubleSolution(
          problem.variableBounds(), problem.numberOfObjectives(), problem.numberOfConstraints());

      for (int j = 0; j < problem.numberOfVariables(); j++) {
        Bounds<Double> bounds = problem.variableBounds().get(j);
        double lowerBound = bounds.getLowerBound();
        double upperBound = bounds.getUpperBound();

        double location = (lowerBound + upperBound) / 2.0;
        double scale = (upperBound - lowerBound) * scaleFactor;

        // Sample from Cauchy distribution using Inverse CDF method: x = x0 + gamma *
        // tan(pi * (u - 0.5))
        double u = random.nextDouble(0.0, 1.0);
        double value = location + scale * Math.tan(Math.PI * (u - 0.5));

        // Clamp to bounds if necessary
        value = Math.max(lowerBound, Math.min(upperBound, value));

        solution.variables().set(j, value);
      }

      solutionList.add(solution);
    }

    return solutionList;
  }

  public int getNumberOfSolutionsToCreate() {
    return numberOfSolutionsToCreate;
  }

  public double getScaleFactor() {
    return scaleFactor;
  }
}
