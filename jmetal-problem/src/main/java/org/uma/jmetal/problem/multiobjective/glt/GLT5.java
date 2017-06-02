package org.uma.jmetal.problem.multiobjective.glt;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Problem GLT5. Defined in
 * F. Gu, H.-L. Liu, and K. C. Tan, “A multiobjective evolutionary
 * algorithm using dynamic weight design method,” International Journal
 * of Innovative Computing, Information and Control, vol. 8, no. 5B, pp.
 * 3677–3688, 2012.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class GLT5 extends AbstractDoubleProblem {

  /**
   * Default constructor
   */
  public GLT5() {
    this(10) ;
  }

  /**
   * Constructor
   * @param numberOfVariables
   */
  public GLT5(int numberOfVariables) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(3);
    setName("GLT5");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    lowerLimit.add(0.0) ;
    upperLimit.add(1.0) ;
    lowerLimit.add(0.0) ;
    upperLimit.add(1.0) ;
    for (int i = 2; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  @Override
  public void evaluate(DoubleSolution solution) {
    solution.setObjective(0, (1.0 + g(solution))*
        (1.0 - Math.cos(solution.getVariableValue(0)*Math.PI/2.0))*
        (1.0 - Math.cos(solution.getVariableValue(1)*Math.PI/2.0)));
    solution.setObjective(1, (1.0 + g(solution))*
        (1.0 - Math.cos(solution.getVariableValue(0)*Math.PI/2.0))*
        (1.0 - Math.sin(solution.getVariableValue(1)*Math.PI/2.0)));
    solution.setObjective(2, (1.0 + g(solution))*
        (1.0 - Math.sin(solution.getVariableValue(0)*Math.PI/2.0)));
  }

  private double g(DoubleSolution solution) {
    double result = 0.0 ;

    for (int i = 2; i < solution.getNumberOfVariables(); i++) {
      double value =solution.getVariableValue(i)
          - Math.sin(2*Math.PI*solution.getVariableValue(0)+i*Math.PI/solution.getNumberOfVariables()) ;

      result += value * value ;
    }

    return result ;
  }
}
