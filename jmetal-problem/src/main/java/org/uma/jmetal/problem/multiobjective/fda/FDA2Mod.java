package org.uma.jmetal.problem.multiobjective.fda;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Problem FDA2mod of J. Mehnen, T. Wagner and G. Rudolph, "Evolutionary Optimization of Dynamic
 * Multiobjective Test Functions", 2006, a redesign of {@link FDA2} in which the front changes from
 * convex to concave whatever the values of the variables:
 *
 * <pre>
 * f_1 = x_1,  f_2 = g h,  g = 1 + sum_{x_i in x_II} x_i^2 + sum_{x_i in x_III} (x_i + 1)^2
 * h = 1 - (f_1 / g)^H,  H = 0.2 + 4.8 t^2
 * x_1 in [0, 1],  x_II, x_III in [-1, 1]
 * </pre>
 *
 * <p>x_II are the variables x_2, ..., x_{1 + n / 2} and x_III the rest. H is not periodic: it grows
 * with t. This is the FDA2 of jMetalSP and of jMetal up to version 7.11, the one used in the
 * experiments of InDM2 (Nebro et al., Swarm and Evolutionary Computation, 2018), whose Eq. (5)
 * describes the original FDA2 instead.
 *
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class FDA2Mod extends FDA {

  /** Creates the problem with 31 variables. */
  public FDA2Mod() {
    this(31, 2);
  }

  /**
   * Creates the problem with tau_T = 5 and n_T = 10.
   *
   * @param numberOfVariables the number of variables, at least 3
   * @param numberOfObjectives the number of objectives, 2
   */
  public FDA2Mod(Integer numberOfVariables, Integer numberOfObjectives) {
    this(numberOfVariables, numberOfObjectives, DEFAULT_TAU_T, DEFAULT_N_T);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 3
   * @param numberOfObjectives the number of objectives, 2
   * @param tauT the number of values of the counter for which t remains fixed
   * @param nT the number of distinct steps in t
   */
  public FDA2Mod(int numberOfVariables, int numberOfObjectives, int tauT, int nT) {
    super(tauT, nT);
    Check.that(
        numberOfObjectives == 2, () -> "FDA2Mod has 2 objectives (" + numberOfObjectives + ")");
    Check.that(
        numberOfVariables >= 3, () -> "FDA2Mod needs 3 variables (" + numberOfVariables + ")");
    numberOfObjectives(numberOfObjectives);
    name("FDA2Mod");
    setVariableBounds(numberOfVariables, 1, -1.0, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    int numberOfVariables = solution.variables().size();
    int firstOfXIII = numberOfVariables / 2 + 1;

    double f1 = solution.variables().get(0);
    double g = 1.0;
    for (int i = 1; i < firstOfXIII; i++) {
      g += Math.pow(solution.variables().get(i), 2.0);
    }
    for (int i = firstOfXIII; i < numberOfVariables; i++) {
      g += Math.pow(solution.variables().get(i) + 1.0, 2.0);
    }
    double bigH = 0.2 + 4.8 * Math.pow(time, 2.0);
    double h = 1.0 - Math.pow(f1 / g, bigH);

    solution.objectives()[0] = f1;
    solution.objectives()[1] = g * h;
    return solution;
  }
}
