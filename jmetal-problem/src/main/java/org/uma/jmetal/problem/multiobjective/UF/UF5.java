package org.uma.jmetal.problem.multiobjective.UF;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem CEC2009_UF5
 */
@SuppressWarnings("serial")
public class UF5 extends AbstractDoubleProblem {
  int    n       ;
  double epsilon ;
 /** 
  * Constructor.
  * Creates a default instance of problem CEC2009_UF5 (30 decision variables)
  */
  public UF5() {
    this(30, 10, 0.1);
  }
  
 /**
  * Creates a new instance of problem CEC2009_UF5.
  * @param numberOfVariables Number of variables.
  */
  public UF5(int numberOfVariables, int N, double epsilon) {
    setNumberOfVariables(numberOfVariables) ;
    setNumberOfObjectives(2) ;
    setNumberOfConstraints(0) ;
    setName("UF5") ;

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;
    
    this.n       = N ;
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

  	int count1, count2;
		double sum1, sum2, yj, hj ;
		sum1   = sum2   = 0.0;
		count1 = count2 = 0;
    
    for (int j = 2 ; j <= getNumberOfVariables(); j++) {
			yj = x[j-1]-Math.sin(6.0*Math.PI*x[0]+j*Math.PI/getNumberOfVariables());
			hj = 2.0*yj*yj - Math.cos(4.0*Math.PI*yj) + 1.0;
			if (j % 2 == 0) {
				sum2  += hj;
				count2++;
			} else {
				sum1  += hj;
				count1++;
			}
    }
    hj = (0.5/n + epsilon)*Math.abs(Math.sin(2.0*n*Math.PI*x[0]));

    solution.setObjective(0, x[0] + hj + 2.0*sum1 / (double)count1);
    solution.setObjective(1, 1.0 - x[0] + hj + 2.0*sum2 / (double)count2);
  }
}
