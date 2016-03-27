//  Water.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.util.Arrays;
import java.util.List;

/**
 * Class representing problem Water
 */
@SuppressWarnings("serial")
public class Water extends AbstractDoubleProblem implements ConstrainedProblem<DoubleSolution> {
  public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree ;
  public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints ;

  // defining the lower and upper limits
  public static final Double [] LOWERLIMIT = {0.01, 0.01, 0.01};
  public static final Double [] UPPERLIMIT = {0.45, 0.10, 0.10};

  /**
   * Constructor.
   * Creates a default instance of the Water problem.
   */
  public Water() {
    setNumberOfVariables(3);
    setNumberOfObjectives(5);
    setNumberOfConstraints(7);
    setName("Water") ;

    List<Double> lowerLimit = Arrays.asList(LOWERLIMIT) ;
    List<Double> upperLimit = Arrays.asList(UPPERLIMIT) ;

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);

    overallConstraintViolationDegree = new OverallConstraintViolation<DoubleSolution>() ;
    numberOfViolatedConstraints = new NumberOfViolatedConstraints<DoubleSolution>() ;
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution)  {
    double[] fx = new double[2];
    double[] x = new double[solution.getNumberOfVariables()];
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      x[i] = solution.getVariableValue(i) ;
    }

    fx[0] = 106780.37 * (x[1] + x[2]) + 61704.67 ;
    fx[1] = 3000 * x[0] ;
    fx[2] = 305700 * 2289 * x[1] / Math.pow(0.06*2289, 0.65) ;
    fx[3] = 250 * 2289 * Math.exp(-39.75*x[1]+9.9*x[2]+2.74) ;
    fx[4] = 25 * (1.39 /(x[0]*x[1]) + 4940*x[2] -80) ;

    solution.setObjective(0,fx[0]);
    solution.setObjective(1,fx[1]);
    solution.setObjective(2,fx[2]);
    solution.setObjective(3,fx[3]);
    solution.setObjective(4,fx[4]);
  }

  /** EvaluateConstraints() method */
  @Override
  public void evaluateConstraints(DoubleSolution solution)  {
    double[] constraint = new double[getNumberOfConstraints()];
    double[] x = new double[solution.getNumberOfVariables()];
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      x[i] = solution.getVariableValue(i) ;
    }

    constraint[0] = 1 - (0.00139/(x[0]*x[1])+4.94*x[2]-0.08)             ;
    constraint[1] = 1 - (0.000306/(x[0]*x[1])+1.082*x[2]-0.0986)         ;
    constraint[2] = 50000 - (12.307/(x[0]*x[1]) + 49408.24*x[2]+4051.02) ;
    constraint[3] = 16000 - (2.098/(x[0]*x[1])+8046.33*x[2]-696.71)      ;
    constraint[4] = 10000 - (2.138/(x[0]*x[1])+7883.39*x[2]-705.04)      ;
    constraint[5] = 2000 - (0.417*x[0]*x[1] + 1721.26*x[2]-136.54)       ;
    constraint[6] = 550 - (0.164/(x[0]*x[1])+631.13*x[2]-54.48) ;

    double overallConstraintViolation = 0.0;
    int violatedConstraints = 0;
    for (int i = 0; i < getNumberOfConstraints(); i++) {
      if (constraint[i]<0.0){
        overallConstraintViolation+=constraint[i];
        violatedConstraints++;
      }
    }

    overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
    numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
  }
}
