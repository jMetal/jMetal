package org.uma.jmetal.problem.multiobjective.zdt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem ZDT4
 */
@SuppressWarnings("serial")
public class ZDT4 extends ZDT1 {

  /** Constructor. Creates a default instance of problem ZDT4 (10 decision variables */
  public ZDT4() {
    this(10);
  }

  /**
   * Creates a instance of problem ZDT4.
   *
   * @param numberOfVariables Number of variables.
   */
  public ZDT4(Integer numberOfVariables) {
    super(numberOfVariables) ;
    setName("ZDT4");
    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    for (int i = 1; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-5.0);
      upperLimit.add(5.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /**
   * Returns the value of the ZDT4 function G.
   *
   * @param solution Solution
   */
  public double evalG(DoubleSolution solution) {
    double g = IntStream.range(1, solution.variables().size()).mapToDouble(var -> Math.pow(solution.variables().get(var), 2.0) +
            -10.0 * Math.cos(4.0 * Math.PI * solution.variables().get(var))).sum();

      double constant = 1.0 + 10.0 * (solution.variables().size() - 1);
    return g + constant;
  }

  /**
   * Returns the value of the ZDT4 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  public double evalH(double f, double g) {
    return 1.0 - Math.sqrt(f / g);
  }
}
