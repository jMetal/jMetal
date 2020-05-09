package org.uma.jmetal.problem.multiobjective.fda;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** @author Cristóbal Barba <cbarba@lcc.uma.es> */
@SuppressWarnings("serial")
public class FDA2 extends FDA implements Serializable {

  public FDA2() {
    this(31, 2);
  }

  public FDA2(Integer numberOfVariables, Integer numberOfObjectives) {
    super();
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setName("FDA2");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    for (int i = 1; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  @Override
  public void evaluate(DoubleSolution solution) {
    double[] f = new double[getNumberOfObjectives()];
    f[0] = solution.getVariable(0);
    double g = this.evalG(solution, 1, (solution.getNumberOfVariables() / 2) + 1);
    double h = this.evalH(f[0], g);
    f[1] = g * h; // 1-Math.sqrt(f[0]);
    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  /**
   * Returns the value of the FDA2 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution, int limitInf, int limitSup) {

    double g = 0.0;
    for (int i = limitInf; i < limitSup; i++) {
      g += Math.pow(solution.getVariable(i), 2.0);
    }
    for (int i = limitSup; i < solution.getNumberOfVariables(); i++) {
      g += Math.pow((solution.getVariable(i) + 1.0), 2.0);
    }
    g = g + 1.0;
    return g;
  }

  /**
   * Returns the value of the FDA function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  private double evalH(double f, double g) {
    double HT = 0.2 + 4.8 * Math.pow(time, 2.0);
    double h = 1.0 - Math.pow((f / g), HT);
    return h;
  }
}
