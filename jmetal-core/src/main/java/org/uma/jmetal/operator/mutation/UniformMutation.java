//  UniformMutation.java
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
import org.uma.jmetal.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.HashMap;

/**
 * This class implements a uniform mutation operator.
 */
public class UniformMutation extends Mutation {
  private static final long serialVersionUID = -2304129118963396274L;

  private Double perturbation;
  private Double mutationProbability = null;

  /** Constructor */
  private UniformMutation(Builder builder) {
    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);

    mutationProbability = builder.mutationProbability ;
    perturbation = builder.perturbation ;
  }

  public Double getPerturbation() {
    return perturbation;
  }

  public Double getMutationProbability() {
    return mutationProbability;
  }

  /**
   * Perform the operation
   *
   * @param probability Mutation setProbability
   * @param solution    The solutiontype to mutate
   */
  public void doMutation(double probability, Solution solution)  {
    XReal x = new XReal(solution);

    for (int var = 0; var < solution.getDecisionVariables().length; var++) {
      if (PseudoRandom.randDouble() < probability) {
        double rand = PseudoRandom.randDouble();
        double tmp = (rand - 0.5) * perturbation;

        tmp += x.getValue(var);

        if (tmp < x.getLowerBound(var)) {
          tmp = x.getLowerBound(var);
        } else if (tmp > x.getUpperBound(var)) {
          tmp = x.getUpperBound(var);
        }

        x.setValue(var, tmp);
      }
    }
  }

  /** execute() method */
  public Object execute(Object object) throws JMetalException {
    if (null == object) {
      throw new JMetalException("Null parameter") ;
    } else if (!(object instanceof Solution)) {
      throw new JMetalException("Invalid parameter class") ;
    }

    Solution solution = (Solution) object;

    if (!solutionTypeIsValid(solution)) {
      throw new JMetalException("UniformMutation.execute: the solution " +
        "type " + solution.getType() + " is not allowed with this operator");
    }

    doMutation(mutationProbability, solution);

    return solution;
  }

  /** Builder class */
  public static class Builder {
    private double perturbation ;
    private double mutationProbability ;

    public Builder(double perturbation, double probability) {
      this.perturbation = perturbation ;
      mutationProbability = probability ;
    }

    public UniformMutation build() {
      return new UniformMutation(this) ;
    }
  }
}
