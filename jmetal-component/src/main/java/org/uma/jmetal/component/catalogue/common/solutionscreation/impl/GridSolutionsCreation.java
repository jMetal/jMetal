package org.uma.jmetal.component.catalogue.common.solutionscreation.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * Grid-based solutions creation strategy. Creates solutions on a regular grid across the search
 * space, ensuring systematic and deterministic coverage.
 *
 * <p>The grid is created by dividing each variable's range into equal intervals. The number of
 * divisions per dimension is calculated as the n-th root of the requested population size, where n
 * is the number of variables.
 *
 * <p>This strategy is useful when:
 * <ul>
 *   <li>Deterministic initialization is desired
 *   <li>Uniform coverage of the search space is important
 *   <li>The problem has few variables (grid size grows exponentially with dimensions)
 * </ul>
 *
 * <p>Note: The actual number of solutions created may differ from the requested number due to
 * rounding when calculating grid divisions.
 *
 * <p>Control parameters: None
 *
 * <p>Reference: M. Clerc, "Standard Particle Swarm Optimisation", HAL open archive, 2012.
 * Available at: <a href="https://hal.archives-ouvertes.fr/hal-00764996">hal-00764996</a>
 *
 * @author Antonio J. Nebro
 */
public class GridSolutionsCreation implements SolutionsCreation<DoubleSolution> {

  private final int numberOfSolutionsToCreate;
  private final DoubleProblem problem;

  /**
   * Constructor.
   *
   * @param problem The optimization problem
   * @param numberOfSolutionsToCreate Approximate number of solutions to create
   */
  public GridSolutionsCreation(DoubleProblem problem, int numberOfSolutionsToCreate) {
    this.problem = problem;
    this.numberOfSolutionsToCreate = numberOfSolutionsToCreate;
  }

  @Override
  public List<DoubleSolution> create() {
    List<DoubleSolution> solutionList = new ArrayList<>();

    int numberOfVariables = problem.numberOfVariables();
    
    // Calculate divisions per dimension (n-th root of population size)
    int divisionsPerDimension = (int) Math.ceil(Math.pow(numberOfSolutionsToCreate, 1.0 / numberOfVariables));
    
    // Ensure at least 2 divisions
    divisionsPerDimension = Math.max(2, divisionsPerDimension);

    // Generate grid points recursively
    double[] currentPoint = new double[numberOfVariables];
    generateGridPoints(solutionList, currentPoint, 0, divisionsPerDimension);

    // Trim to requested size if we generated too many
    while (solutionList.size() > numberOfSolutionsToCreate) {
      solutionList.remove(solutionList.size() - 1);
    }

    return solutionList;
  }

  /**
   * Recursively generates grid points.
   */
  private void generateGridPoints(
      List<DoubleSolution> solutionList,
      double[] currentPoint,
      int dimension,
      int divisionsPerDimension) {
    
    if (dimension == problem.numberOfVariables()) {
      // Create solution from current point
      DoubleSolution solution =
          new DefaultDoubleSolution(
              problem.variableBounds(), problem.numberOfObjectives(), problem.numberOfConstraints());
      
      for (int j = 0; j < problem.numberOfVariables(); j++) {
        solution.variables().set(j, currentPoint[j]);
      }
      solutionList.add(solution);
      return;
    }

    Bounds<Double> bounds = problem.variableBounds().get(dimension);
    double lowerBound = bounds.getLowerBound();
    double upperBound = bounds.getUpperBound();
    double step = (upperBound - lowerBound) / (divisionsPerDimension - 1);

    for (int i = 0; i < divisionsPerDimension; i++) {
      currentPoint[dimension] = lowerBound + i * step;
      generateGridPoints(solutionList, currentPoint, dimension + 1, divisionsPerDimension);
    }
  }

  public int getNumberOfSolutionsToCreate() {
    return numberOfSolutionsToCreate;
  }
}
