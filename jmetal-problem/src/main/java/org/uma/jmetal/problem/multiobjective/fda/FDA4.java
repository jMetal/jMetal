package org.uma.jmetal.problem.multiobjective.fda;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/** @author Cristóbal Barba <cbarba@lcc.uma.es> */
@SuppressWarnings("serial")
public class FDA4 extends FDA {
  private final int M = 3;
  public FDA4() {
    this(12, 3);
  }

  public FDA4(Integer numberOfVariables, Integer numberOfObjectives)
      throws JMetalException {
    super();
    setNumberOfObjectives(numberOfObjectives);
    setName("FDA4");

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
    f[0] = this.evalF1(solution, g);
    f[1] = evalFK(solution, g, 2);
    f[2] = evalFM(solution, g);
    for (int i = 0; i < solution.objectives().length; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }

  private double evalF1(DoubleSolution solution, double g) {
    double f = 1.0d + g;
      double mult = 1.0d;
      for (int i = 1; i <= M - 1; i++) {
          double cos = Math.cos(solution.variables().get(i - 1) * Math.PI / 2.0d);
          mult = mult * cos;
      }
      return f * mult;
  }

  private double evalFK(DoubleSolution solution, double g, int k) {
    double f = 1.0d + g;
    double mult;
    double aux = Math.sin((solution.variables().get(M - k) * Math.PI) / 2.0d);
      double acc = 1.0d;
      int bound = M - k;
      for (int i = 1; i <= bound; i++) {
          double cos = Math.cos(solution.variables().get(i - 1) * Math.PI / 2.0d);
          acc = acc * cos;
      }
      mult = acc;
    mult *= aux;
    return f * mult;
  }

  /**
   * Returns the value of the FDA4 function G.
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
    return g;
  }

  private double evalFM(DoubleSolution solution, double g) {
    double fm = 1.0d + g;
    fm *= Math.sin(solution.variables().get(0) * Math.PI / 2);
    return fm;
  }
}
