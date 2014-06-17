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

package org.uma.jmetal.operators.mutation;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encodings.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal.encodings.solutiontype.RealSolutionType;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal.util.wrapper.XReal;

import java.util.HashMap;

/**
 * This class implements a uniform mutation operator.
 */
public class UniformMutation extends Mutation {
  private static final long serialVersionUID = -2304129118963396274L;

  private Double perturbation_;
  private Double mutationProbability_ = null;

  @Deprecated
  public UniformMutation(HashMap<String, Object> parameters) {
    super(parameters);

    if (parameters.get("probability") != null) {
      mutationProbability_ = (Double) parameters.get("probability");
    }
    if (parameters.get("perturbation") != null) {
      perturbation_ = (Double) parameters.get("perturbation");
    }

    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);
  }

  private UniformMutation(Builder builder) {
    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);

    mutationProbability_ = builder.mutationProbability_ ;
    perturbation_ = builder.perturbation_ ;
  }

  /**
   * Performs the operation
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
        double tmp = (rand - 0.5) * perturbation_;

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

  /**
   * Executes the operation
   *
   * @param object An object containing the solution to mutate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Object execute(Object object) throws JMetalException {
    Solution solution = (Solution) object;

    if (!solutionTypeIsValid(solution)) {
      Configuration.logger_.severe("UniformMutation.execute: the solution " +
        "is not of the right type. The type should be 'Real', but " +
        solution.getType() + " is obtained");

      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".execute()");
    }

    doMutation(mutationProbability_, solution);

    return solution;
  }

  /**
   * Builder class
   */
  public static class Builder {
    private double perturbation_ ;
    private double mutationProbability_ ;

    public Builder(double perturbation, double probability) {
      perturbation_ = perturbation ;
      mutationProbability_ = probability ;
    }

    public UniformMutation build() {
      return new UniformMutation(this) ;
    }
  }
}
