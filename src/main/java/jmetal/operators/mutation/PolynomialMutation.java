//  PolynomialMutation.java
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

package jmetal.operators.mutation;

import jmetal.core.Solution;
import jmetal.core.SolutionType;
import jmetal.encodings.solutiontype.ArrayRealSolutionType;
import jmetal.encodings.solutiontype.RealSolutionType;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.random.PseudoRandom;
import jmetal.util.wrapper.XReal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements a polynomial mutation operator.
 */
public class PolynomialMutation extends Mutation {
  private static final double ETA_M_DEFAULT_ = 20.0;
  private double distributionIndex_ = ETA_M_DEFAULT_;
  /**
   * Valid solution types to apply this operator
   */
  private static final List<Class<? extends SolutionType>> VALID_TYPES =
    Arrays.asList(RealSolutionType.class, ArrayRealSolutionType.class);
  private double mutationProbability_ = 0.0;

  /**
   * Constructor
   * Creates a new instance of the polynomial mutation operator
   */
  public PolynomialMutation(HashMap<String, Object> parameters) {
    super(parameters);
    if (parameters.get("probability") != null) {
      mutationProbability_ = (Double) parameters.get("probability");
    }
    if (parameters.get("distributionIndex") != null) {
      distributionIndex_ = (Double) parameters.get("distributionIndex");
    }
  }

  private PolynomialMutation(Builder builder) {
    super (new HashMap<String, Object>()) ;

    mutationProbability_ = builder.mutationProbability_ ;
    distributionIndex_ = builder.distributionIndex_ ;
  }
  public double getMutationProbability() {
    return mutationProbability_;
  }

  public double getDistributionIndex() {
    return distributionIndex_;
  }


  /**
   * Perform the mutation operation
   *
   * @param probability Mutation probability
   * @param solution    The solution to mutate
   * @throws JMException
   */
  public void doMutation(double probability, Solution solution) throws JMException {
    double rnd, delta1, delta2, mut_pow, deltaq;
    double y, yl, yu, val, xy;
    XReal x = new XReal(solution);
    for (int var = 0; var < solution.numberOfVariables(); var++) {
      if (PseudoRandom.randDouble() <= probability) {
        y = x.getValue(var);
        yl = x.getLowerBound(var);
        yu = x.getUpperBound(var);
        delta1 = (y - yl) / (yu - yl);
        delta2 = (yu - y) / (yu - yl);
        rnd = PseudoRandom.randDouble();
        mut_pow = 1.0 / (distributionIndex_ + 1.0);
        if (rnd <= 0.5) {
          xy = 1.0 - delta1;
          val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, (distributionIndex_ + 1.0)));
          deltaq = java.lang.Math.pow(val, mut_pow) - 1.0;
        } else {
          xy = 1.0 - delta2;
          val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (java.lang.Math
            .pow(xy, (distributionIndex_ + 1.0)));
          deltaq = 1.0 - (java.lang.Math.pow(val, mut_pow));
        }
        y = y + deltaq * (yu - yl);
        if (y < yl) {
          y = yl;
        }
        if (y > yu) {
          y = yu;
        }
        x.setValue(var, y);
      }
    }
  }

  /**
   * Executes the operation
   *
   * @param object An object containing a solution
   * @return An object containing the mutated solution
   * @throws JMException
   */
  public Object execute(Object object) throws JMException {
    Solution solution = (Solution) object;

    if (!VALID_TYPES.contains(solution.getType().getClass())) {
      Configuration.logger_.severe("PolynomialMutation.execute: the solution " +
        "type " + solution.getType() + " is not allowed with this operator");

      Class cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMException("Exception in " + name + ".execute()");
    }

    doMutation(mutationProbability_, solution);
    return solution;
  }

  /**
   * Builder class
   */
  public static class Builder {
    private double distributionIndex_ ;
    private double mutationProbability_ ;

    public Builder() {
    }

    public Builder distributionIndex(double distributionIndex) {
      distributionIndex_ = distributionIndex ;

      return this ;
    }

    public Builder probability(double probability) {
      mutationProbability_ = probability ;

      return this ;
    }

    public PolynomialMutation build() {
      return new PolynomialMutation(this) ;
    }
  }
}
