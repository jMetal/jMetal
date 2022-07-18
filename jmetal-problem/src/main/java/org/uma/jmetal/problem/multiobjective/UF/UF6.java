package org.uma.jmetal.problem.multiobjective.UF;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem CEC2009_UF5
 */
@SuppressWarnings("serial")
public class UF6 extends AbstractDoubleProblem {
  int    n       ;
  double epsilon ;

 /** 
  * Constructor.
  * Creates a default instance of problem CEC2009_UF6 (30 decision variables, N =10, epsilon = 0.1)
  */
  public UF6() {
    this(30, 2, 0.1);
  }
  
 /**
  * Creates a new instance of problem CEC2009_UF6.
  * @param numberOfVariables Number of variables.
  */
  public UF6(Integer numberOfVariables, int N, double epsilon) {
    setNumberOfObjectives(2) ;
    setNumberOfConstraints(0) ;
    setName("UF6") ;

    @NotNull List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    @NotNull List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;
    
    n = N  ;
    this.epsilon = epsilon ;

    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    for (var i = 1; i < numberOfVariables; i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public @NotNull DoubleSolution evaluate(@NotNull DoubleSolution solution) {
      var x = new double[getNumberOfVariables()];
    for (var i = 0; i < solution.variables().size(); i++) {
      x[i] = solution.variables().get(i) ;
    }

  	int count1, count2 ;
    double prod1, prod2 ;
    double sum1, sum2, yj, pj ;
		sum1   = sum2   = 0.0;
		count1 = count2 = 0;
 		prod1  = prod2  = 1.0;
    
    for (var j = 2; j <= getNumberOfVariables(); j++) {
			yj = x[j-1]-Math.sin(6.0*Math.PI*x[0]+j*Math.PI/ getNumberOfVariables());
			pj = Math.cos(20.0*yj*Math.PI/Math.sqrt(j));
			if (j % 2 == 0) {
				sum2  += yj*yj;
				prod2 *= pj;
				count2++;
			} else {
				sum1  += yj*yj;
				prod1 *= pj;
				count1++;
			}
    }
      var hj = 2.0 * (0.5 / n + epsilon) * Math.sin(2.0 * n * Math.PI * x[0]);
		if (hj < 0.0) 
      hj = 0.0;
    
    solution.objectives()[0] = x[0] + hj + 2.0*(4.0*sum1 - 2.0*prod1 + 2.0) / (double)count1;
    solution.objectives()[1] = 1.0 - x[0] + hj + 2.0*(4.0*sum2 - 2.0*prod2 + 2.0) / (double)count2;

    return solution ;
  }
}
