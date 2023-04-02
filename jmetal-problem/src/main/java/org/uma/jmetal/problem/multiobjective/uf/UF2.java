package org.uma.jmetal.problem.multiobjective.uf;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem CEC2009_UF2
 */
@SuppressWarnings("serial")
public class UF2 extends AbstractDoubleProblem {

  /**
   * Constructor.
   * Creates a default instance of problem CEC2009_UF2 (30 decision variables)
   */
  public UF2()  {
    this(30);
  }

  /**
   * Creates a new instance of problem CEC2009_UF2.
   * @param numberOfVariables Number of variables.
   */
  public UF2(int numberOfVariables) {
    numberOfObjectives(2) ;
    numberOfConstraints(0) ;
    name("UF2") ;

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    for (int i = 1; i < numberOfVariables; i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    variableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = new double[numberOfVariables()];
    for (int i = 0; i < solution.variables().size(); i++) {
      x[i] = solution.variables().get(i) ;
    }

    int count1, count2;
    double sum1, sum2, yj;
    sum1   = sum2   = 0.0;
    count1 = count2 = 0;

    for (int j = 2 ; j <= numberOfVariables(); j++) {
      if(j % 2 == 0) {
        yj = x[j-1] -
            (0.3 * x[0] * x[0] * Math.cos(24 * Math.PI * x[0] + 4 * j * Math.PI / numberOfVariables()) + 0.6 * x[0])*
                Math.sin(6.0 * Math.PI* x[0] + j * Math.PI / numberOfVariables());
        sum2 += yj*yj;
        count2++;
      } else {

        yj = x[j-1] -
            (0.3 * x[0] * x[0] * Math.cos(24 * Math.PI * x[0] + 4 * j * Math.PI / numberOfVariables()) + 0.6 * x[0])*
                Math.cos(6.0 * Math.PI* x[0] + j * Math.PI / numberOfVariables());

        sum1 += yj*yj;
        count1++;
      }
    }

    solution.objectives()[0] = x[0] + 2.0 * sum1 / (double)count1;
    solution.objectives()[1] = 1.0 - Math.sqrt(x[0]) + 2.0 * sum2 / (double)count2;

    return solution ;
  }
}
