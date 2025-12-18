package org.uma.jmetal.component.catalogue.common.solutionscreation.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * Opposition-based solutions creation strategy. For each randomly generated solution, its opposite
 * solution is also created by mirroring each variable across the center of its bounds.
 *
 * <p>Given a solution x in [lb, ub], its opposite is: opposite(x) = lb + ub - x
 *
 * <p>This strategy can improve exploration by ensuring that opposite regions of the search space
 * are covered. It's particularly useful for problems where good solutions might be located in
 * unexpected regions.
 *
 * <p>The strategy creates pairs of solutions (original + opposite), so the actual number of
 * solutions created is numberOfSolutionsToCreate (half original, half opposite). If an odd number
 * is requested, one extra random solution is added.
 *
 * <p>Control parameters: None
 *
 * @author Antonio J. Nebro
 */
public class OppositionBasedSolutionsCreation implements SolutionsCreation<DoubleSolution> {

  private final int numberOfSolutionsToCreate;
  private final DoubleProblem problem;

  /**
   * Constructor.
   *
   * @param problem The optimization problem
   * @param numberOfSolutionsToCreate Number of solutions to create
   */
  public OppositionBasedSolutionsCreation(DoubleProblem problem, int numberOfSolutionsToCreate) {
    this.problem = problem;
    this.numberOfSolutionsToCreate = numberOfSolutionsToCreate;
  }

  @Override
  public List<DoubleSolution> create() {
    List<DoubleSolution> solutionList = new ArrayList<>(numberOfSolutionsToCreate);

    int pairsToCreate = numberOfSolutionsToCreate / 2;
    boolean needExtraSolution = (numberOfSolutionsToCreate % 2) != 0;

    for (int i = 0; i < pairsToCreate; i++) {
      // Create random solution
      DoubleSolution randomSolution = (DoubleSolution) problem.createSolution();
      solutionList.add(randomSolution);

      // Create opposite solution
      DoubleSolution oppositeSolution = createOppositeSolution(randomSolution);
      solutionList.add(oppositeSolution);
    }

    // Add one extra random solution if odd number requested
    if (needExtraSolution) {
      solutionList.add((DoubleSolution) problem.createSolution());
    }

    return solutionList;
  }

  /**
   * Creates the opposite solution by mirroring each variable across the center of its bounds.
   *
   * @param solution The original solution
   * @return The opposite solution
   */
  private DoubleSolution createOppositeSolution(DoubleSolution solution) {
    DoubleSolution oppositeSolution =
        new DefaultDoubleSolution(
            problem.variableBounds(), problem.numberOfObjectives(), problem.numberOfConstraints());

    for (int j = 0; j < problem.numberOfVariables(); j++) {
      Bounds<Double> bounds = problem.variableBounds().get(j);
      double lowerBound = bounds.getLowerBound();
      double upperBound = bounds.getUpperBound();
      double originalValue = solution.variables().get(j);

      // Opposite value: lb + ub - x
      double oppositeValue = lowerBound + upperBound - originalValue;

      oppositeSolution.variables().set(j, oppositeValue);
    }

    return oppositeSolution;
  }

  public int getNumberOfSolutionsToCreate() {
    return numberOfSolutionsToCreate;
  }
}
