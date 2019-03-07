package org.uma.jmetal.problem.multiobjective.UF;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

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
    setNumberOfVariables(numberOfVariables) ;
    setNumberOfObjectives(2) ;
    setNumberOfConstraints(0) ;
    setName("UF6") ;

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;
    
    n = N  ;
    this.epsilon = epsilon ;

    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    for (int i = 1; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    double[] x = new double[getNumberOfVariables()];
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      x[i] = solution.getVariableValue(i) ;
    }

  	int count1, count2 ;
    double prod1, prod2 ;
    double sum1, sum2, yj, hj, pj ;
		sum1   = sum2   = 0.0;
		count1 = count2 = 0;
 		prod1  = prod2  = 1.0;
    
    for (int j = 2 ; j <= getNumberOfVariables(); j++) {
			yj = x[j-1]-Math.sin(6.0*Math.PI*x[0]+j*Math.PI/getNumberOfVariables());
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
		hj = 2.0*(0.5/n + epsilon)*Math.sin(2.0*n*Math.PI*x[0]);
		if (hj < 0.0) 
      hj = 0.0;
    
    solution.setObjective(0, x[0] + hj + 2.0*(4.0*sum1 - 2.0*prod1 + 2.0) / (double)count1);
    solution.setObjective(1, 1.0 - x[0] + hj + 2.0*(4.0*sum2 - 2.0*prod2 + 2.0) / (double)count2);
  }
}
