package org.uma.jmetal.problem.multiobjective.glt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem GLT4. Defined in
 * F. Gu, H.-L. Liu, and K. C. Tan, “A multiobjective evolutionary
 * algorithm using dynamic weight design method,” International Journal
 * of Innovative Computing, Information and Control, vol. 8, no. 5B, pp.
 * 3677–3688, 2012.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class GLT4 extends AbstractDoubleProblem {

  /**
   * Default constructor
   */
  public GLT4() {
    this(10) ;
  }

  /**
   * Constructor
   * @param numberOfVariables
   */
  public GLT4(int numberOfVariables) {
    setNumberOfObjectives(2);
    setName("GLT4");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    lowerLimit.add(0.0) ;
    upperLimit.add(1.0) ;
    IntStream.range(1, numberOfVariables).forEach(i -> {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    });

    setVariableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    solution.objectives()[0] = (1.0 + g(solution))*solution.variables().get(0);
    solution.objectives()[1] = (1.0 + g(solution))* (2.0 -
        2.0*Math.pow(solution.variables().get(0), 0.5)*
        Math.pow(Math.cos(2*Math.pow(solution.variables().get(0), 0.5)*Math.PI), 2.0)) ;
    return solution ;
  }

  private double g(DoubleSolution solution) {
    double result = IntStream.range(1, solution.variables().size()).mapToDouble(i -> solution.variables().get(i)
            - Math.sin(2 * Math.PI * solution.variables().get(0) + i * Math.PI / solution.variables().size())).map(value -> value * value).sum();

      return result ;
  }
}
