//  NonUniformMutation.java
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

package org.uma.jmetal.operator.mutation;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solution.ArrayRealSolution;
import org.uma.jmetal.encoding.solution.RealSolution;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal.util.wrapper.XReal;

import java.util.HashMap;

/**
 * This class implements a non-uniform mutation operator.
 */
public class NonUniformMutation extends Mutation {
  private static final long serialVersionUID = -2440053123382478633L;

  private Double perturbation_ ;
  private Integer maxIterations_ ;
  private Integer currentIteration_ ;
  private Double mutationProbability_ ;


  public NonUniformMutation(HashMap<String, Object> parameters) {
    super(parameters);
    if (parameters.get("probability") != null) {
      mutationProbability_ = (Double) parameters.get("probability");
    }
    if (parameters.get("perturbation") != null) {
      perturbation_ = (Double) parameters.get("perturbation");
    }
    if (parameters.get("maxIterations") != null) {
      maxIterations_ = (Integer) parameters.get("maxIterations");
    }

    addValidSolutionType(RealSolution.class);
    addValidSolutionType(ArrayRealSolution.class);
  }

  /**
   * Perform the mutation operation
   *
   * @param probability Mutation probability
   * @param solution    The solution to mutate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void doMutation(double probability, Solution solution) throws JMetalException {
    XReal x = new XReal(solution);
    for (int var = 0; var < solution.getDecisionVariables().length; var++) {
      if (PseudoRandom.randDouble() < probability) {
        double rand = PseudoRandom.randDouble();
        double tmp;

        if (rand <= 0.5) {
          tmp = delta(x.getUpperBound(var) - x.getValue(var),
            perturbation_);
          tmp += x.getValue(var);
        } else {
          tmp = delta(x.getLowerBound(var) - x.getValue(var),
            perturbation_);
          tmp += x.getValue(var);
        }

        if (tmp < x.getLowerBound(var)) {
          tmp = x.getLowerBound(var);
        } else if (tmp > x.getUpperBound(var)) {
          tmp = x.getUpperBound(var);
        }
        x.setValue(var, tmp);
      }
    }
  }


  /**
   * Calculates the delta value used in NonUniform mutation operator
   */
  private double delta(double y, double bMutationParameter) {
    double rand = PseudoRandom.randDouble();
    int it, maxIt;
    it = currentIteration_;
    maxIt = maxIterations_;

    return (y * (1.0 -
      Math.pow(rand,
        Math.pow((1.0 - it / (double) maxIt), bMutationParameter)
      )));
  }

  /**
   * Executes the operation
   *
   * @param object An object containing a solution
   * @return An object containing the mutated solution
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Object execute(Object object) throws JMetalException {
    Solution solution = (Solution) object;

    if (!solutionTypeIsValid(solution)) {
      Configuration.logger_.severe("NonUniformMutation.execute: the solution " +
        solution.getType() + "is not of the right type");

      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".execute()");
    } // if  

    if (getParameter("currentIteration") != null) {
      currentIteration_ = (Integer) getParameter("currentIteration");
    }

    doMutation(mutationProbability_, solution);

    return solution;
  }
}
