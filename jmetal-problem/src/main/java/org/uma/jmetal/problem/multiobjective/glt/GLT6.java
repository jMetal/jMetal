package org.uma.jmetal.problem.multiobjective.glt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem GLT6. Defined in
 * F. Gu, H.-L. Liu, and K. C. Tan, “A multiobjective evolutionary
 * algorithm using dynamic weight design method,” International Journal
 * of Innovative Computing, Information and Control, vol. 8, no. 5B, pp.
 * 3677–3688, 2012.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class GLT6 extends AbstractDoubleProblem {

  /**
   * Default constructor
   */
  public GLT6() {
    this(10) ;
  }

  /**
   * Constructor
   * @param numberOfVariables
   */
  public GLT6(int numberOfVariables) {
    setNumberOfObjectives(3);
    setName("GLT6");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    lowerLimit.add(0.0) ;
    upperLimit.add(1.0) ;
    lowerLimit.add(0.0) ;
    upperLimit.add(1.0) ;
    for (int i = 2; i < numberOfVariables; i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    solution.objectives()[0] = (1.0 + g(solution))*
        (1.0 - Math.cos(solution.variables().get(0)*Math.PI/2.0))*
        (1.0 - Math.cos(solution.variables().get(1)*Math.PI/2.0));
    solution.objectives()[1] = (1.0 + g(solution))*
        (1.0 - Math.cos(solution.variables().get(0)*Math.PI/2.0))*
        (1.0 - Math.sin(solution.variables().get(1)*Math.PI/2.0));
    solution.objectives()[2] = (1.0 + g(solution))*
        (2.0 - Math.sin(solution.variables().get(0)*Math.PI/2.0)
        - Math.signum(Math.cos(4*solution.variables().get(0)*Math.PI)));
    return solution ;
  }

  private double g(DoubleSolution solution) {
      double result = 0.0;
      int bound = solution.variables().size();
      for (int i = 2; i < bound; i++) {
          double value = solution.variables().get(i)
                  - Math.sin(2 * Math.PI * solution.variables().get(0) + i * Math.PI / solution.variables().size());
          double v = value * value;
          result += v;
      }

      return result ;
  }
}
