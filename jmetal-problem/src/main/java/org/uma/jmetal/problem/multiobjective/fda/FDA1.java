package org.uma.jmetal.problem.multiobjective.fda;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.observable.Observable;

/**
 * Problem FDA1 of Farina, Deb and Amato (see {@link FDA}): type I, convex front.
 *
 * <pre>
 * f_1 = x_1,  f_2 = g h,  g = 1 + sum_{i=2..n} (x_i - G)^2,  h = 1 - sqrt(f_1 / g)
 * G = sin(0.5 pi t),  x_1 in [0, 1],  x_i in [-1, 1]
 * </pre>
 *
 * <p>The paper suggests n = 20, the default.
 *
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class FDA1 extends FDA {

  /**
   * Creates the problem with 20 variables. The observable is not used.
   *
   * @deprecated use {@link #FDA1()}
   */
  @Deprecated
  public FDA1(Observable<Integer> observable) {
    this();
  }

  /** Creates the problem with 20 variables, as suggested in the paper. */
  public FDA1() {
    this(20, 2);
  }

  /**
   * Creates the problem with tau_T = 5 and n_T = 10.
   *
   * @param numberOfVariables the number of variables, at least 2
   * @param numberOfObjectives the number of objectives, 2
   */
  public FDA1(Integer numberOfVariables, Integer numberOfObjectives) {
    this(numberOfVariables, numberOfObjectives, DEFAULT_TAU_T, DEFAULT_N_T);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   * @param numberOfObjectives the number of objectives, 2
   * @param tauT the number of values of the counter for which t remains fixed
   * @param nT the number of distinct steps in t
   */
  public FDA1(int numberOfVariables, int numberOfObjectives, int tauT, int nT) {
    super(tauT, nT);
    Check.that(numberOfObjectives == 2, () -> "FDA1 has 2 objectives (" + numberOfObjectives + ")");
    Check.that(numberOfVariables >= 2, () -> "FDA1 needs 2 variables (" + numberOfVariables + ")");
    numberOfObjectives(numberOfObjectives);
    name("FDA1");
    setVariableBounds(numberOfVariables, 1, -1.0, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double f1 = solution.variables().get(0);
    double gT = Math.sin(0.5 * Math.PI * time);
    double g = 1.0;
    for (int i = 1; i < solution.variables().size(); i++) {
      g += Math.pow(solution.variables().get(i) - gT, 2);
    }
    double h = 1.0 - Math.sqrt(f1 / g);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = g * h;
    return solution;
  }
}
