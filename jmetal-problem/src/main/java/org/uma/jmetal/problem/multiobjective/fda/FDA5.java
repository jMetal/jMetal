package org.uma.jmetal.problem.multiobjective.fda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/** @author Cristóbal Barba <cbarba@lcc.uma.es> */
@SuppressWarnings("serial")
public class FDA5 extends FDA implements Serializable {
  private final int M = 3;

  public FDA5() {
    this(12, 3);
  }

  public FDA5(Integer numberOfVariables, Integer numberOfObjectives)
      throws JMetalException {
    super();
    setNumberOfObjectives(numberOfObjectives);
    setName("FDA5");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] f = new double[solution.objectives().length];
    double g = this.evalG(solution, M - 1);
    double Ft = 1.0d + 100.0d * Math.pow(Math.sin(0.5d * Math.PI * time), 4.0d);
    f[0] = this.evalF1(solution, g, Ft);
    f[1] = evalFK(solution, g, 2, Ft);
    f[2] = evalFM(solution, g, Ft);
    for (int i = 0; i < solution.objectives().length; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }

  private double evalF1(DoubleSolution solution, double g, double Ft) {
    double f = 1.0d + g;
      double mult = 1.0d;
      for (int i = 1; i <= M - 1; i++) {
          double y_i = Math.pow(solution.variables().get(i - 1), Ft);
          double cos = Math.cos(y_i * Math.PI / 2.0d);
          mult = mult * cos;
      }
      return f * mult;
  }

  private double evalFK(DoubleSolution solution, double g, int k, double Ft) {
    double f = 1.0d + g;
      double mult = 1.0d;
      int bound = M - k;
      for (int i = 1; i <= bound; i++) {
          double y_i = Math.pow(solution.variables().get(i - 1), Ft);
          double cos = Math.cos(y_i * Math.PI / 2.0d);
          mult = mult * cos;
      }
      double yy = Math.pow(solution.variables().get(M - k), Ft);
    mult *= Math.sin(yy * Math.PI / 2.0d);
    return f * mult;
  }

  /**
   * Returns the value of the FDA5 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution, int limitInf) {
    double g;
    double Gt = Math.abs(Math.sin(0.5d * Math.PI * time));
      double sum = 0.0;
      int bound = solution.variables().size();
      for (int i = limitInf; i < bound; i++) {
          double pow = Math.pow((solution.variables().get(i) - Gt), 2.0d);
          sum += pow;
      }
      g = sum;
    return g + Gt;
  }

  private double evalFM(DoubleSolution solution, double g, double Ft) {
    double fm = 1.0d + g;
    double y_1 = Math.pow(solution.variables().get(0), Ft);
    double mult = Math.sin(y_1 * Math.PI / 2.0d);
    return fm * mult;
  }
}
