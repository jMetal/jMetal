package org.uma.jmetal.problem.multiobjective.UF;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem CEC2009_UF9
 */
@SuppressWarnings("serial")
public class UF9 extends AbstractDoubleProblem {
  double epsilon ;
  
 /** 
  * Constructor.
  * Creates a default instance of problem CEC2009_UF9 (30 decision variables, epsilon = 0.1)
  */
  public UF9() {
    this(30, 0.1);
  }
  
 /**
  * Creates a new instance of problem CEC2009_UF9.
  * @param numberOfVariables Number of variables.
  */
  public UF9(int numberOfVariables, double epsilon) {
    setNumberOfObjectives(3) ;
    setNumberOfConstraints(0) ;
    setName("UF9") ;

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    this.epsilon = epsilon ;

    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    for (var i = 2; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-2.0);
      upperLimit.add(2.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
      var x = new double[getNumberOfVariables()];
    for (var i = 0; i < solution.variables().size(); i++) {
      x[i] = solution.variables().get(i) ;
    }

  	int count1, count2, count3;
		double sum1, sum2, sum3, yj;
		sum1   = sum2 = sum3 = 0.0;
		count1 = count2 = count3 = 0;
    
    for (var j = 3; j <= getNumberOfVariables(); j++) {
			yj = x[j-1] - 2.0*x[1]*Math.sin(2.0*Math.PI*x[0]+j*Math.PI/ getNumberOfVariables());
			if(j % 3 == 1) {
				sum1  += yj*yj;
				count1++;
			} else if(j % 3 == 2) {
				sum2  += yj*yj;
				count2++;
			} else {
				sum3  += yj*yj;
				count3++;
			}
    }
    
    yj = (1.0+epsilon)*(1.0-4.0*(2.0*x[0]-1.0)*(2.0*x[0]-1.0));
		if (yj < 0.0) 
      yj = 0.0;
        
    solution.objectives()[0] = 0.5*(yj + 2*x[0])*x[1]		+ 2.0*sum1 / (double)count1;
    solution.objectives()[1] = 0.5*(yj - 2*x[0] + 2.0)*x[1] + 2.0*sum2 / (double)count2;
    solution.objectives()[2] = 1.0 - x[1]                   + 2.0*sum3 / (double)count3 ;

    return solution ;
  }
}
