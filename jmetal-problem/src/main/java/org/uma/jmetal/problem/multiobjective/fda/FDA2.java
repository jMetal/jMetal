package org.uma.jmetal.problem.multiobjective.fda;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Problem FDA2 of Farina, Deb and Amato (see {@link FDA}): type III, the front swings from convex to
 * nonconvex while the Pareto set does not change.
 *
 * <pre>
 * f_1 = x_1,  f_2 = g h,  g = 1 + sum_{x_i in x_II} x_i^2
 * h = 1 - (f_1 / g)^(1 / (H + sum_{x_i in x_III} (x_i - H)^2)),  H = 0.75 + 0.7 sin(0.5 pi t)
 * x_1 in [0, 1],  x_II, x_III in [-1, 1]
 * </pre>
 *
 * <p>x_II are the variables x_2, ..., x_{1 + (n - 1) / 2} and x_III the rest; with the default n =
 * 31, |x_II| = |x_III| = 15, as suggested in the paper.
 *
 * <p>Up to jMetal 7.11 this class implemented {@link FDA2Mod}, the redesign of Mehnen, Wagner and
 * Rudolph, so the results obtained with it in earlier versions are not comparable.
 *
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class FDA2 extends FDA {

  /** Creates the problem with 31 variables, so that |x_II| = |x_III| = 15. */
  public FDA2() {
    this(31, 2);
  }

  /**
   * Creates the problem with tau_T = 5 and n_T = 10.
   *
   * @param numberOfVariables the number of variables, at least 3
   * @param numberOfObjectives the number of objectives, 2
   */
  public FDA2(Integer numberOfVariables, Integer numberOfObjectives) {
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
  public FDA2(int numberOfVariables, int numberOfObjectives, int tauT, int nT) {
    super(tauT, nT);
    Check.that(numberOfObjectives == 2, () -> "FDA2 has 2 objectives (" + numberOfObjectives + ")");
    Check.that(numberOfVariables >= 3, () -> "FDA2 needs 3 variables (" + numberOfVariables + ")");
    numberOfObjectives(numberOfObjectives);
    name("FDA2");
    setVariableBounds(numberOfVariables, 1, -1.0, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    int numberOfVariables = solution.variables().size();
    int firstOfXIII = 1 + (numberOfVariables - 1) / 2;
    double bigH = 0.75 + 0.7 * Math.sin(0.5 * Math.PI * time);

    double f1 = solution.variables().get(0);
    double g = 1.0;
    for (int i = 1; i < firstOfXIII; i++) {
      g += Math.pow(solution.variables().get(i), 2);
    }
    double sumOfXIII = 0.0;
    for (int i = firstOfXIII; i < numberOfVariables; i++) {
      sumOfXIII += Math.pow(solution.variables().get(i) - bigH, 2);
    }
    double h = 1.0 - Math.pow(f1 / g, 1.0 / (bigH + sumOfXIII));

    solution.objectives()[0] = f1;
    solution.objectives()[1] = g * h;
    return solution;
  }
}
