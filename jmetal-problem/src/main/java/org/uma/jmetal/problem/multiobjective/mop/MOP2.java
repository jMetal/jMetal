package org.uma.jmetal.problem.multiobjective.mop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MOP2. Defined in
 * H. L. Liu, F. Gu and Q. Zhang, "Decomposition of a Multiobjective 
 * Optimization Problem Into a Number of Simple Multiobjective Subproblems,"
 * in IEEE Transactions on Evolutionary Computation, vol. 18, no. 3, pp. 
 * 450-455, June 2014.
 *
 * @author Mastermay <javismay@gmail.com> 	
 */
@SuppressWarnings("serial")
public class MOP2 extends AbstractDoubleProblem {

  /** Constructor. Creates default instance of problem MOP2 (10 decision variables) */
  public MOP2() {
    this(10);
  }

  /**
   * Creates a new instance of problem MOP2.
   *
   * @param numberOfVariables Number of variables.
   */
  public MOP2(Integer numberOfVariables) {
    setNumberOfObjectives(2);
    setName("MOP2");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] f = new double[solution.objectives().length];

    double g = this.evalG(solution);
    f[0] = (1 + g) * solution.variables().get(0);
    f[1] = (1 + g) * (1 - solution.variables().get(0) * solution.variables().get(0));

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];
    return solution ;
  }

  /**
   * Returns the value of the MOP2 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution) {
      double g = 0.0;
      int bound = solution.variables().size();
      for (int i = 1; i < bound; i++) {
          double t = solution.variables().get(i) - Math.sin(0.5 * Math.PI * solution.variables().get(0));
          double v = Math.abs(t) / (1 + Math.exp(5 * Math.abs(t)));
          g += v;
      }
      g = 10 * Math.sin(Math.PI * solution.variables().get(0)) * g;
    return g;
  }

}
