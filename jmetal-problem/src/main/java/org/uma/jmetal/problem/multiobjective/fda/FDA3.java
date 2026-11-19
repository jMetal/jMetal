package org.uma.jmetal.problem.multiobjective.fda;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Problem FDA3 of Farina, Deb and Amato (see {@link FDA}): type II, convex fronts whose density of
 * solutions changes with t.
 *
 * <pre>
 * f_1 = sum_{x_i in x_I} x_i^F,  f_2 = g h,  g = 1 + G + sum_{x_i in x_II} (x_i - G)^2
 * h = 1 - sqrt(f_1 / g),  G = |sin(0.5 pi t)|,  F = 10^(2 sin(0.5 pi t))
 * x_I in [0, 1],  x_II in [-1, 1]
 * </pre>
 *
 * <p>The paper suggests |x_I| = 5 and |x_II| = 25, the defaults. The experiments of InDM2 (Nebro et
 * al., 2018) use |x_I| = 1 and 31 variables, which {@code new FDA3(31, 2, 1)} gives; up to jMetal
 * 7.11 this class always used |x_I| = 1.
 *
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class FDA3 extends FDA {
  private final int sizeOfXI;

  /** Creates the problem with |x_I| = 5 and |x_II| = 25, as suggested in the paper. */
  public FDA3() {
    this(30, 2);
  }

  /**
   * Creates the problem with |x_I| = 5, tau_T = 5 and n_T = 10.
   *
   * @param numberOfVariables the number of variables, at least 6
   * @param numberOfObjectives the number of objectives, 2
   */
  public FDA3(Integer numberOfVariables, Integer numberOfObjectives) {
    this(numberOfVariables, numberOfObjectives, 5);
  }

  /**
   * Creates the problem with tau_T = 5 and n_T = 10.
   *
   * @param numberOfVariables the number of variables, more than {@code sizeOfXI}
   * @param numberOfObjectives the number of objectives, 2
   * @param sizeOfXI the number of variables of x_I, which define f_1
   */
  public FDA3(int numberOfVariables, int numberOfObjectives, int sizeOfXI) {
    this(numberOfVariables, numberOfObjectives, sizeOfXI, DEFAULT_TAU_T, DEFAULT_N_T);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, more than {@code sizeOfXI}
   * @param numberOfObjectives the number of objectives, 2
   * @param sizeOfXI the number of variables of x_I, which define f_1, at least 1
   * @param tauT the number of values of the counter for which t remains fixed
   * @param nT the number of distinct steps in t
   */
  public FDA3(int numberOfVariables, int numberOfObjectives, int sizeOfXI, int tauT, int nT) {
    super(tauT, nT);
    Check.that(numberOfObjectives == 2, () -> "FDA3 has 2 objectives (" + numberOfObjectives + ")");
    Check.that(sizeOfXI >= 1, () -> "FDA3 needs at least one variable in x_I (" + sizeOfXI + ")");
    Check.that(
        numberOfVariables > sizeOfXI,
        () -> "FDA3 needs more variables (" + numberOfVariables + ") than x_I (" + sizeOfXI + ")");
    this.sizeOfXI = sizeOfXI;
    numberOfObjectives(numberOfObjectives);
    name("FDA3");
    setVariableBounds(numberOfVariables, sizeOfXI, -1.0, 1.0);
  }

  public int sizeOfXI() {
    return sizeOfXI;
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double bigF = Math.pow(10.0, 2.0 * Math.sin(0.5 * Math.PI * time));
    double bigG = Math.abs(Math.sin(0.5 * Math.PI * time));

    double f1 = 0.0;
    for (int i = 0; i < sizeOfXI; i++) {
      f1 += Math.pow(solution.variables().get(i), bigF);
    }
    double g = 1.0 + bigG;
    for (int i = sizeOfXI; i < solution.variables().size(); i++) {
      g += Math.pow(solution.variables().get(i) - bigG, 2.0);
    }
    double h = 1.0 - Math.sqrt(f1 / g);

    solution.objectives()[0] = f1;
    solution.objectives()[1] = g * h;
    return solution;
  }
}
