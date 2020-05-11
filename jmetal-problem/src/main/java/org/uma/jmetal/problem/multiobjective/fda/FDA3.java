package org.uma.jmetal.problem.multiobjective.fda;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** @author Cristóbal Barba <cbarba@lcc.uma.es> */
@SuppressWarnings("serial")
public class FDA3 extends FDA implements Serializable {

  private final int limitInfI = 0;
  private final int limitSupI = 1;
  private final int limitInfII = 1;

  public FDA3() {
    this(30, 2);
  }


  public FDA3(Integer numberOfVariables, Integer numberOfObjectives)
      throws JMetalException {
    super();
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setName("FDA3");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    for (int i = limitInfI; i < limitSupI; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    for (int i = limitInfII; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  @Override
  public void evaluate(DoubleSolution solution) {
    double[] f = new double[getNumberOfObjectives()];
    f[0] = this.evalF(solution, limitInfI, limitSupI);
    double g = this.evalG(solution, limitInfII);
    double h = this.evalH(f[0], g);
    f[1] = g * h;
    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  private double evalF(DoubleSolution solution, int limitInf, int limitSup) {
    double f = 0.0d;
    double aux = 2.0d * Math.sin(0.5d * Math.PI * time);
    double Ft = Math.pow(10.0d, aux);
    for (int i = limitInf; i < limitSup; i++) {
      f += Math.pow(solution.getVariable(i), Ft);
    }
    return f;
  }

  /**
   * Returns the value of the FDA3 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution, int limitInf) {

    double g = 0.0d;
    double Gt = Math.abs(Math.sin(0.5d * Math.PI * time));
    for (int i = limitInf; i < solution.getNumberOfVariables(); i++) {
      g += Math.pow((solution.getVariable(i) - Gt), 2.0);
    }
    g = g + 1.0 + Gt;
    return g;
  }

  /**
   * Returns the value of the FDA3 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  private double evalH(double f, double g) {
    double h = 1.0d - Math.sqrt(f / g);
    return h;
  }
}
