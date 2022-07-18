package org.uma.jmetal.problem.multiobjective.fda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
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
    setNumberOfObjectives(numberOfObjectives);
    setName("FDA3");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    @NotNull List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    for (var i = limitInfI; i < limitSupI; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    for (var i = limitInfII; i < numberOfVariables; i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    var f = new double[solution.objectives().length];
    f[0] = this.evalF(solution, limitInfI, limitSupI);
    var g = this.evalG(solution, limitInfII);
    var h = this.evalH(f[0], g);
    f[1] = g * h;
    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];
    return solution;
  }

  private double evalF(@NotNull DoubleSolution solution, int limitInf, int limitSup) {
    var aux = 2.0d * Math.sin(0.5d * Math.PI * time);
    var Ft = Math.pow(10.0d, aux);
    var sum = 0.0;
      for (var i = limitInf; i < limitSup; i++) {
        var pow = Math.pow(solution.variables().get(i), Ft);
          sum += pow;
      }
    var f = sum;
    return f;
  }

  /**
   * Returns the value of the FDA3 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution, int limitInf) {

    double g;
    var Gt = Math.abs(Math.sin(0.5d * Math.PI * time));
    var sum = 0.0;
    var bound = solution.variables().size();
      for (var i = limitInf; i < bound; i++) {
        var pow = Math.pow((solution.variables().get(i) - Gt), 2.0);
          sum += pow;
      }
      g = sum;
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
    var h = 1.0d - Math.sqrt(f / g);
    return h;
  }
}
