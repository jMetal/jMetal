package org.uma.jmetal.problem.multiobjective.glt;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem GLT1. Defined in
 * F. Gu, H.-L. Liu, and K. C. Tan, “A multiobjective evolutionary
 * algorithm using dynamic weight design method,” International Journal
 * of Innovative Computing, Information and Control, vol. 8, no. 5B, pp.
 * 3677–3688, 2012.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class GLT1 extends AbstractDoubleProblem {

  /**
   * Default constructor
   */
  public GLT1() {
    this(10) ;
  }

  /**
   * Constructor
   * @param numberOfVariables
   */
  public GLT1(int numberOfVariables) {
    numberOfObjectives(2);
    name("GLT1");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    lowerLimit.add(0.0) ;
    upperLimit.add(1.0) ;
    for (int i = 1; i < numberOfVariables; i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    solution.objectives()[0] = (1.0 + g(solution))*solution.variables().get(0);
    solution.objectives()[1] = (1.0 + g(solution))*(2.0-solution.variables().get(0)
        -Math.signum(Math.cos(2*Math.PI*solution.variables().get(0))));
    return solution ;
  }

  private double g(DoubleSolution solution) {
    double result = 0.0 ;

    for (int i = 1; i < solution.variables().size(); i++) {
      double value =solution.variables().get(i)
          - Math.sin(2*Math.PI*solution.variables().get(0)+i*Math.PI/solution.variables().size()) ;

      result += value * value ;
    }

    return result ;
  }
}
