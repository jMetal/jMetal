package org.uma.jmetal.problem.multiobjective;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem FourBarTruss
 *
 * 	Measures:
 * 	f = 10kN
 *  e = 200000 kN/cm2
 *  l = 200 cm
 *  sigma = 10kN/cm2
 */
@SuppressWarnings("serial")
public class FourBarTruss extends AbstractDoubleProblem {
  private double f = 10   ;
  private double e = 200000;
  private double l = 200  ;
  private double sigma = 10 ;
	
  /**
   * Constructor
   * Creates a default instance of the FourBarTruss problem
   */
  public FourBarTruss() {
    int numberOfVariables = 4 ;
    numberOfObjectives(2);
    numberOfConstraints(0);
    name("FourBarTruss");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    lowerLimit.add(f / sigma) ;
    lowerLimit.add(Math.sqrt(2.0)*(f / sigma));
    lowerLimit.add(lowerLimit.get(1)) ;
    lowerLimit.add(lowerLimit.get(0)) ;

    upperLimit.add(3*(f / sigma)) ;
    upperLimit.add(upperLimit.get(0)) ;
    upperLimit.add(upperLimit.get(0)) ;
    upperLimit.add(upperLimit.get(0)) ;

    variableBounds(lowerLimit, upperLimit);
  }

  /**
   * Evaluate() method
   */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double [] fx = new double[2] ;
    double [] x = new double[numberOfVariables()] ;
    for (int i = 0 ; i < numberOfVariables(); i++)
    	x[i] = solution.variables().get(i) ;
    
    fx[0] = l *(2*x[0]+ Math.sqrt(2.0)*x[1]+ Math.sqrt(x[2])+x[3]) ;
    fx[1] = (f * l / e)*(2/x[0] + 2*Math.sqrt(2)/x[1] - 2*Math.sqrt(2)/x[2] + 2/x[3]);

    solution.objectives()[0] = fx[0];
    solution.objectives()[1] = fx[1];

    return solution ;
  }
}
