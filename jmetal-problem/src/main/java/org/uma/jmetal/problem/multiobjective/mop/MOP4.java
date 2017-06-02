package org.uma.jmetal.problem.multiobjective.mop;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

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
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(2);
    setName("MOP4");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  public void evaluate(DoubleSolution solution) {
    double[] f = new double[getNumberOfObjectives()];

    double g = this.evalG(solution);
    f[0] = (1 + g) * solution.getVariableValue(0);
    f[1] = (1 + g) * (1- Math.sqrt(solution.getVariableValue(0)) * 
    		Math.pow(Math.cos(solution.getVariableValue(0) * Math.PI * 2), 2));

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  /**
   * Returns the value of the MOP4 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution) {
    double g = 0.0;
    for (int i = 1; i < solution.getNumberOfVariables(); i++) {
      double t = solution.getVariableValue(i) - Math.sin(0.5 * Math.PI * solution.getVariableValue(0));
      g += Math.abs(t) / (1 + Math.exp(5 * Math.abs(t)));
    }
    g = 1 + 10 * Math.sin(Math.PI * solution.getVariableValue(0)) * g;
    return g;
  }

}
