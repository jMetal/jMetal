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

package org.uma.jmetal.operator.mutation;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionType;
import org.uma.jmetal.encoding.solutiontype.ArrayIntSolutionType;
import org.uma.jmetal.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.IntSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal.util.wrapper.XInt;
import org.uma.jmetal.util.wrapper.XReal;

import java.util.HashMap;

/**
 * This class implements a polynomial mutation operator.
 */
public class PolynomialMutation extends Mutation {
  private static final double ETA_M_DEFAULT_ = 20.0;
  private double distributionIndex_ = ETA_M_DEFAULT_;

  private double mutationProbability_ = 0.0;

  @Deprecated
  public PolynomialMutation(HashMap<String, Object> parameters) {
    super(parameters);

    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);

    if (parameters.get("probability") != null) {
      mutationProbability_ = (Double) parameters.get("probability");
    }
    if (parameters.get("distributionIndex") != null) {
      distributionIndex_ = (Double) parameters.get("distributionIndex");
    }
  }

  private PolynomialMutation(Builder builder) {
    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);

    addValidSolutionType(IntSolutionType.class);
    addValidSolutionType(ArrayIntSolutionType.class);

    mutationProbability_ = builder.mutationProbability_;
    distributionIndex_ = builder.distributionIndex_;
  }

  public double getMutationProbability() {
    return mutationProbability_;
  }

  public double getDistributionIndex() {
    return distributionIndex_;
  }

  /** Execute method */
  public Object execute(Object object) throws JMetalException {
    Solution solution = (Solution) object;

    if (!solutionTypeIsValid(solution)) {
      throw new JMetalException("PolynomialMutation.execute: the solutiontype " +
        "type " + solution.getType() + " is not allowed with this operator");
    }

    doMutation(mutationProbability_, solution);
    return solution;
  }

  /**
   * Perform the mutation operation
   */
  public void doMutation(double probability, Solution solution) throws JMetalException {
    SolutionType type = solution.getType();
    if ((type instanceof RealSolutionType) || (type instanceof ArrayRealSolutionType)) {
      doRealMutation(probability, solution);
    } else {
      doIntegerMutation(probability, solution) ;
    }
  }

  private void doRealMutation(double probability, Solution solution) {
    double rnd, delta1, delta2, mutPow, deltaq;
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
        mutPow = 1.0 / (distributionIndex_ + 1.0);
        if (rnd <= 0.5) {
          xy = 1.0 - delta1;
          val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, (distributionIndex_ + 1.0)));
          deltaq = java.lang.Math.pow(val, mutPow) - 1.0;
        } else {
          xy = 1.0 - delta2;
          val =
            2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (Math.pow(xy, (distributionIndex_ + 1.0)));
          deltaq = 1.0 - (java.lang.Math.pow(val, mutPow));
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

  private void doIntegerMutation(double probability, Solution solution) {
    double rnd, delta1, delta2, mutPow, deltaq;
    double y, yl, yu, val, xy;
    XInt x = new XInt(solution);
    for (int var = 0; var < solution.numberOfVariables(); var++) {
      if (PseudoRandom.randDouble() <= probability) {
        y = x.getValue(var);
        yl = x.getLowerBound(var);
        yu = x.getUpperBound(var);
        delta1 = (y - yl) / (yu - yl);
        delta2 = (yu - y) / (yu - yl);
        rnd = PseudoRandom.randDouble();
        mutPow = 1.0 / (distributionIndex_ + 1.0);
        if (rnd <= 0.5) {
          xy = 1.0 - delta1;
          val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, (distributionIndex_ + 1.0)));
          deltaq = java.lang.Math.pow(val, mutPow) - 1.0;
        } else {
          xy = 1.0 - delta2;
          val =
            2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (Math.pow(xy, (distributionIndex_ + 1.0)));
          deltaq = 1.0 - (java.lang.Math.pow(val, mutPow));
        }
        y = y + deltaq * (yu - yl);
        if (y < yl) {
          y = yl;
        }
        if (y > yu) {
          y = yu;
        }
        x.setValue(var, (int)y);
      }
    }
  }

  /** Builder class */
  public static class Builder {
    private double distributionIndex_ ;
    private double mutationProbability_ ;

    public Builder() {
      distributionIndex_ = ETA_M_DEFAULT_ ;
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
