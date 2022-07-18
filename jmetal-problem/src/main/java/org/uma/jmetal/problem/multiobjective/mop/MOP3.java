package org.uma.jmetal.problem.multiobjective.mop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MOP3. Defined in
 * H. L. Liu, F. Gu and Q. Zhang, "Decomposition of a Multiobjective 
 * Optimization Problem Into a Number of Simple Multiobjective Subproblems,"
 * in IEEE Transactions on Evolutionary Computation, vol. 18, no. 3, pp. 
 * 450-455, June 2014.
 *
 * @author Mastermay <javismay@gmail.com> 	
 */
@SuppressWarnings("serial")
public class MOP3 extends AbstractDoubleProblem {

  /** Constructor. Creates default instance of problem MOP3 (10 decision variables) */
  public MOP3() {
    this(10);
  }

  /**
   * Creates a new instance of problem MOP3.
   *
   * @param numberOfVariables Number of variables.
   */
  public MOP3(Integer numberOfVariables) {
    setNumberOfObjectives(2);
    setName("MOP3");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (var i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
      var f = new double[solution.objectives().length];

      var g = this.evalG(solution);
    f[0] = (1 + g) * Math.cos(solution.variables().get(0) * Math.PI * 0.5);
    f[1] = (1 + g) * Math.sin(solution.variables().get(0) * Math.PI * 0.5);

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];
    return solution ;
  }

  /**
   * Returns the value of the MOP3 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution) {
      var g = 0.0;
      var bound = solution.variables().size();
      for (var i = 1; i < bound; i++) {
          var t = solution.variables().get(i) - Math.sin(0.5 * Math.PI * solution.variables().get(0));
          var v = Math.abs(t) / (1 + Math.exp(5 * Math.abs(t)));
          g += v;
      }
      g = 10 * Math.sin(0.5 * Math.PI * solution.variables().get(0)) * g;
    return g;
  }

}
