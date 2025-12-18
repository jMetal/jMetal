package org.uma.jmetal.component.catalogue.common.solutionscreation.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Chaos-based solutions creation strategy using the logistic map. Creates solutions using chaotic
 * sequences that provide better coverage than purely random initialization while maintaining
 * unpredictability.
 *
 * <p>The logistic map is defined as: x_{n+1} = r * x_n * (1 - x_n)
 *
 * <p>With r = 4 (fully chaotic regime), the map produces values in [0, 1] that are deterministic
 * but appear random and provide good coverage of the interval.
 *
 * <p>Each variable uses a different chaotic sequence initialized from a random seed, ensuring
 * independence between dimensions while maintaining the beneficial properties of chaotic sequences.
 *
 * <p>This strategy is useful when:
 * <ul>
 *   <li>Better coverage than random initialization is desired
 *   <li>Reproducibility with a fixed seed is needed
 *   <li>The problem benefits from quasi-random sequences
 * </ul>
 *
 * <p>Control parameters: None
 *
 * @author Antonio J. Nebro
 */
public class ChaosBasedSolutionsCreation implements SolutionsCreation<DoubleSolution> {

  private static final double LOGISTIC_R = 4.0; // Fully chaotic regime
  private static final int WARMUP_ITERATIONS = 100; // Discard initial transient

  private final int numberOfSolutionsToCreate;
  private final DoubleProblem problem;

  /**
   * Constructor.
   *
   * @param problem The optimization problem
   * @param numberOfSolutionsToCreate Number of solutions to create
   */
  public ChaosBasedSolutionsCreation(DoubleProblem problem, int numberOfSolutionsToCreate) {
    this.problem = problem;
    this.numberOfSolutionsToCreate = numberOfSolutionsToCreate;
  }

  @Override
  public List<DoubleSolution> create() {
    List<DoubleSolution> solutionList = new ArrayList<>(numberOfSolutionsToCreate);
    JMetalRandom random = JMetalRandom.getInstance();

    int numberOfVariables = problem.numberOfVariables();

    // Initialize chaotic sequences for each variable
    double[] chaoticValues = new double[numberOfVariables];
    for (int j = 0; j < numberOfVariables; j++) {
      // Initialize with random seed in (0, 1), avoiding 0, 0.25, 0.5, 0.75, 1 (fixed points)
      double seed;
      do {
        seed = random.nextDouble();
      } while (isFixedPoint(seed));
      chaoticValues[j] = seed;

      // Warm up the chaotic sequence to avoid transient behavior
      for (int w = 0; w < WARMUP_ITERATIONS; w++) {
        chaoticValues[j] = logisticMap(chaoticValues[j]);
      }
    }

    // Generate solutions using chaotic sequences
    for (int i = 0; i < numberOfSolutionsToCreate; i++) {
      DoubleSolution solution =
          new DefaultDoubleSolution(
              problem.variableBounds(), problem.numberOfObjectives(), problem.numberOfConstraints());

      for (int j = 0; j < numberOfVariables; j++) {
        // Advance chaotic sequence
        chaoticValues[j] = logisticMap(chaoticValues[j]);

        // Map chaotic value [0, 1] to variable bounds
        Bounds<Double> bounds = problem.variableBounds().get(j);
        double value =
            bounds.getLowerBound()
                + chaoticValues[j] * (bounds.getUpperBound() - bounds.getLowerBound());

        solution.variables().set(j, value);
      }

      solutionList.add(solution);
    }

    return solutionList;
  }

  /**
   * Logistic map function: x_{n+1} = r * x_n * (1 - x_n)
   *
   * @param x Current value
   * @return Next value in the chaotic sequence
   */
  private double logisticMap(double x) {
    return LOGISTIC_R * x * (1.0 - x);
  }

  /**
   * Checks if a value is a fixed point or near-fixed point of the logistic map.
   *
   * @param x Value to check
   * @return true if x is a fixed point
   */
  private boolean isFixedPoint(double x) {
    return x < 0.01 || x > 0.99 || Math.abs(x - 0.25) < 0.01 || Math.abs(x - 0.5) < 0.01
        || Math.abs(x - 0.75) < 0.01;
  }

  public int getNumberOfSolutionsToCreate() {
    return numberOfSolutionsToCreate;
  }
}
