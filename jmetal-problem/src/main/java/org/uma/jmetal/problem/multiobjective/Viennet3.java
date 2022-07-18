package org.uma.jmetal.problem.multiobjective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem Viennet3
 */
@SuppressWarnings("serial")
public class Viennet3 extends AbstractDoubleProblem {
  
 /** 
  * Constructor.
  * Creates a default instance of the Viennet3 problem.
  */
  public Viennet3() {
    int numberOfVariables = 2 ;
    setNumberOfObjectives(3);
    setNumberOfConstraints(0);
    setName("Viennet3") ;

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(-3.0);
      upperLimit.add(3.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables() ;

    double[] f = new double[solution.objectives().length];
      double[] x = new double[10];
      int count = 0;
      for (int i1 = 0; i1 < numberOfVariables; i1++) {
          double v = solution.variables().get(i1);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v;
      }
      x = Arrays.copyOfRange(x, 0, count);

      f[0] = 0.5 * (x[0]*x[0] + x[1]*x[1]) + Math.sin(x[0]*x[0] + x[1]*x[1]) ;

    // Second function
    double value1 = 3.0 * x[0] - 2.0 * x[1] + 4.0 ;
    double value2 = x[0] - x[1] + 1.0 ;
    f[1] = (value1 * value1)/8.0 + (value2 * value2)/27.0 + 15.0 ;

    // Third function
    f[2] = 1.0 / (x[0]*x[0] + x[1]*x[1]+1) - 1.1 *
          Math.exp(-(x[0]*x[0])-(x[1]*x[1])) ;

        
    for (int i = 0; i < solution.objectives().length; i++)
      solution.objectives()[i] = f[i];

    return solution ;
  }
}


