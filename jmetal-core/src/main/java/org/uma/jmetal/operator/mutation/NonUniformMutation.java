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
import org.uma.jmetal.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;

import java.util.HashMap;

/**
 * This class implements a non-uniform mutation operator.
 */
public class NonUniformMutation extends Mutation {
  private static final long serialVersionUID = -2440053123382478633L;

  private double perturbation;
  private int maxIterations;
  private double mutationProbability;

  private int currentIteration;

  @Deprecated
  public NonUniformMutation(HashMap<String, Object> parameters) {
    super(parameters);
    if (parameters.get("probability") != null) {
      mutationProbability = (Double) parameters.get("probability");
    }
    if (parameters.get("perturbation") != null) {
      perturbation = (Double) parameters.get("perturbation");
    }
    if (parameters.get("maxIterations") != null) {
      maxIterations = (Integer) parameters.get("maxIterations");
    }

    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);
  }

  /** Constructor */
  private NonUniformMutation(Builder builder) {
    super(new HashMap<String, Object>()) ;

    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);

    mutationProbability = builder.mutationProbability ;
    perturbation = builder.perturbation ;
    maxIterations = builder.maxIterations ;
  }

  public double getPerturbation() {
    return perturbation;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public double getMutationProbability() {
    return mutationProbability;
  }

  public int getCurrentIteration() {
    return currentIteration;
  }

  public void setCurrentIteration(int currentIteration) {
    if (currentIteration < 0) {
      throw new JMetalException("Iteration number cannot be a negative value: " + currentIteration) ;
    }

    this.currentIteration = currentIteration;
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
      throw new JMetalException("NonUniformMutation.execute: the solution " +
        "type " + solution.getType() + " is not allowed with this operator");
    }


    if (getParameter("currentIteration") != null) {
      currentIteration = (Integer) getParameter("currentIteration");
    }

    doMutation(mutationProbability, solution);

    return solution;
  }

  /**
   * Perform the mutation operation
   *
   * @param probability Mutation probability
   * @param solution    The solutiontype to mutate
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
            perturbation);
          tmp += x.getValue(var);
        } else {
          tmp = delta(x.getLowerBound(var) - x.getValue(var),
            perturbation);
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


  /** Calculates the delta value used in NonUniform mutation operator */
  private double delta(double y, double bMutationParameter) {
    double rand = PseudoRandom.randDouble();
    int it, maxIt;
    it = currentIteration;
    maxIt = maxIterations;

    return (y * (1.0 -
      Math.pow(rand,
        Math.pow((1.0 - it / (double) maxIt), bMutationParameter)
      )));
  }

  /** Builder class */
  static public class Builder {
    private Double perturbation ;
    private Integer maxIterations ;
    private Double mutationProbability ;

    public Builder(double perturbation, double probability, int maxIterations) {
      this.perturbation = perturbation ;
      mutationProbability = probability ;
      this.maxIterations = maxIterations ;
    }

    public NonUniformMutation build() {
      return new NonUniformMutation(this) ;
    }
  }
}
