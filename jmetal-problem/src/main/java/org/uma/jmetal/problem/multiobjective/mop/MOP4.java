package org.uma.jmetal.problem.multiobjective.mop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MOP4. Defined in
 * H. L. Liu, F. Gu and Q. Zhang, "Decomposition of a Multiobjective 
 * Optimization Problem Into a Number of Simple Multiobjective Subproblems,"
 * in IEEE Transactions on Evolutionary Computation, vol. 18, no. 3, pp. 
 * 450-455, June 2014.
 *
 * @author Mastermay <javismay@gmail.com> 	
 */
@SuppressWarnings("serial")
public class MOP4 extends AbstractDoubleProblem {

  /** Constructor. Creates default instance of problem MOP4 (10 decision variables) */
  public MOP4() {
    this(10);
  }

  /**
   * Creates a new instance of problem MOP4.
   *
   * @param numberOfVariables Number of variables.
   */
  public MOP4(Integer numberOfVariables) {
    setNumberOfObjectives(2);
    setName("MOP4");

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
    f[1] = (1 + g) * (1- Math.sqrt(solution.variables().get(0)) *
    		Math.pow(Math.cos(solution.variables().get(0) * Math.PI * 2), 2));

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];
    return solution ;
  }

  /**
   * Returns the value of the MOP4 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution) {
    double g = IntStream.range(1, solution.variables().size()).mapToDouble(i -> solution.variables().get(i) - Math.sin(0.5 * Math.PI * solution.variables().get(0))).map(t -> Math.abs(t) / (1 + Math.exp(5 * Math.abs(t)))).sum();
      g = 1 + 10 * Math.sin(Math.PI * solution.variables().get(0)) * g;
    return g;
  }

}
