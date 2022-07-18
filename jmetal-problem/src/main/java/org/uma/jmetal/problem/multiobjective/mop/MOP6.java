package org.uma.jmetal.problem.multiobjective.mop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MOP6. Defined in
 * H. L. Liu, F. Gu and Q. Zhang, "Decomposition of a Multiobjective 
 * Optimization Problem Into a Number of Simple Multiobjective Subproblems,"
 * in IEEE Transactions on Evolutionary Computation, vol. 18, no. 3, pp. 
 * 450-455, June 2014.
 *
 * @author Mastermay <javismay@gmail.com> 	
 */
@SuppressWarnings("serial")
public class MOP6 extends AbstractDoubleProblem {

  /** Constructor. Creates default instance of problem MOP6 (10 decision variables) */
  public MOP6() {
    this(10);
  }

  /**
   * Creates a new instance of problem MOP6.
   *
   * @param numberOfVariables Number of variables.
   */
  public MOP6(Integer numberOfVariables) {
    setNumberOfObjectives(3);
    setName("MOP6");

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
    f[0] = (1 + g) * solution.variables().get(0) * solution.variables().get(1);
    f[1] = (1 + g) * solution.variables().get(0) * (1 - solution.variables().get(1));
    f[2] = (1 + g) * (1 - solution.variables().get(0));

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];
    solution.objectives()[2] = f[2];
    return solution ;
  }

  /**
   * Returns the value of the MOP6 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution) {
    double g = IntStream.range(2, solution.variables().size()).mapToDouble(i -> solution.variables().get(i) - solution.variables().get(0) * solution.variables().get(1)).map(t -> -0.9 * t * t + Math.pow(Math.abs(t), 0.6)).sum();
      g = 2 * Math.sin(Math.PI * solution.variables().get(0)) * g;
    return g;
  }

}
