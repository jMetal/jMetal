//  Viennet4.java
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

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem Viennet4
 */
@SuppressWarnings("serial")
public class Viennet4 extends AbstractDoubleProblem implements ConstrainedProblem<DoubleSolution> {
  public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree ;
  public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints ;

 /** 
  * Constructor.
  * Creates a default instance of the Viennet4 problem.
  */
  public Viennet4() {
    setNumberOfVariables(2);
    setNumberOfObjectives(3);
    setNumberOfConstraints(3);
    setName("Viennet4") ;

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-4.0);
      upperLimit.add(4.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);

    overallConstraintViolationDegree = new OverallConstraintViolation<DoubleSolution>() ;
    numberOfViolatedConstraints = new NumberOfViolatedConstraints<DoubleSolution>() ;
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables() ;

    double[] f = new double[getNumberOfObjectives()];
    double[] x = new double[numberOfVariables] ;

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.getVariableValue(i) ;
    }
        
    f[0] = (x[0]-2.0)*(x[0]-2.0)/2.0 + 
             (x[1]+1.0)*(x[1]+1.0)/13.0 + 3.0;
        
    f[1] = (x[0]+ x[1]-3.0)*(x[0]+x[1]-3.0)/175.0 +
             (2.0*x[1]-x[0])*(2.0*x[1]-x[0])/17.0 -13.0;
        
    f[2] = (3.0*x[0]-2.0*x[1]+4.0)*(3.0*x[0]-2.0*x[1]+4.0)/8.0 + 
             (x[0]-x[1]+1.0)*(x[0]-x[1]+1.0)/27.0 + 15.0;
        
        
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      solution.setObjective(i,f[i]);        
    }
  } // evaluate


  /** EvaluateConstraints() method  */
  @Override
  public void evaluateConstraints(DoubleSolution solution)  {
    double[] constraint = new double[this.getNumberOfConstraints()];

    double x1 = solution.getVariableValue(0);
    double x2 = solution.getVariableValue(1);
        
    constraint[0] = -x2 - (4.0 * x1) + 4.0  ;
    constraint[1] = x1 + 1.0 ;
    constraint[2] = x2 - x1 + 2.0 ;

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

