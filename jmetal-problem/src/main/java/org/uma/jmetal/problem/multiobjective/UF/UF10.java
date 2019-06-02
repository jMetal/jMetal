package org.uma.jmetal.problem.multiobjective.UF;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem CEC2009_UF10
 */
@SuppressWarnings("serial")
public class UF10 extends AbstractDoubleProblem {
  
 /** 
  * Constructor.
  * Creates a default instance of problem CEC2009_UF10 (30 decision variables)
  */
  public UF10() {
    this(30);
  }
  
 /**
  * Creates a new instance of problem CEC2009_UF10.
  * @param numberOfVariables Number of variables.
  */
  public UF10(int numberOfVariables) {
    setNumberOfVariables(numberOfVariables) ;
    setNumberOfObjectives(3) ;
    setNumberOfConstraints(0) ;
    setName("UF10") ;

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    for (int i = 2; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-2.0);
      upperLimit.add(2.0);
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

  	int count1, count2, count3;
		double sum1, sum2, sum3, yj, hj;
		sum1   = sum2 = sum3 = 0.0;
		count1 = count2 = count3 = 0;
    
    for (int j = 3 ; j <= getNumberOfVariables(); j++) {
			yj = x[j-1] - 2.0*x[1]*Math.sin(2.0*Math.PI*x[0]+j*Math.PI/getNumberOfVariables());
			hj = 4.0*yj*yj - Math.cos(8.0*Math.PI*yj) + 1.0;
			if(j % 3 == 1) {
				sum1  += hj;
				count1++;
			} else if(j % 3 == 2) {
				sum2  += hj;
				count2++;
			} else {
				sum3  += hj;
				count3++;
			}
    }
    
    solution.setObjective(0, Math.cos(0.5*Math.PI*x[0])*Math.cos(0.5*Math.PI*x[1]) + 2.0*sum1 / (double)count1);
    solution.setObjective(1, Math.cos(0.5*Math.PI*x[0])*Math.sin(0.5*Math.PI*x[1]) + 2.0*sum2 / (double)count2);
    solution.setObjective(2, Math.sin(0.5*Math.PI*x[0])                       + 2.0*sum3 / (double)count3) ;
  }
} 

