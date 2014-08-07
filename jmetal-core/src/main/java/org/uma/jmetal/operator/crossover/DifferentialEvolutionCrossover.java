//  DifferentialEvolutionCrossover.java
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

package org.uma.jmetal.operator.crossover;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * Differential evolution crossover operator
 * Comments:
 * - The operator receives two parameters: the current individual and an array
 * of three parent individuals
 * - The best and rand variants depends on the third parent, according whether
 * it represents the current of the "best" individual or a randon one.
 * The implementation of both variants are the same, due to that the parent
 * selection is external to the crossover operator.
 * - Implemented variants:
 * - rand/1/bin (best/1/bin)
 * - rand/1/exp (best/1/exp)
 * - current-to-rand/1 (current-to-best/1)
 * - current-to-rand/1/bin (current-to-best/1/bin)
 * - current-to-rand/1/exp (current-to-best/1/exp)
 */
public class DifferentialEvolutionCrossover extends Crossover {

  private static final double DEFAULT_CR = 0.5;
  private static final double DEFAULT_F = 0.5;
  private static final double DEFAULT_K = 0.5;

  private static final String DEFAULT_DE_VARIANT = "rand/1/bin";

  private static final String[] VALID_VARIANTS = {
    "rand/1/bin",
    "best/1/bin",
    "rand/1/exp",
    "best/1/exp",
    "current-to-rand/1",
    "current-to-best/1",
    "current-to-rand/1/bin",
    "current-to-best/1/bin",
    "current-to-rand/1/exp",
    "current-to-best/1/exp"
  } ;

  private double cr;
  private double f;
  private double k;
  // DE variant (rand/1/bin, rand/1/exp, etc.)
  private String variant;

  /**
   * @deprecated
   */
  @Deprecated
  public DifferentialEvolutionCrossover(HashMap<String, Object> parameters) {
    super(parameters);

    cr = DEFAULT_CR;
    f = DEFAULT_F;
    k = DEFAULT_K;
    variant = DEFAULT_DE_VARIANT;

    if (parameters.get("CR") != null) {
      cr = (Double) parameters.get("CR");
    }
    if (parameters.get("F") != null) {
      f = (Double) parameters.get("F");
    }
    if (parameters.get("K") != null) {
      k = (Double) parameters.get("K");
    }
    if (parameters.get("DE_VARIANT") != null) {
      variant = (String) parameters.get("DE_VARIANT");
    }

    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);
  }

  public DifferentialEvolutionCrossover(Builder builder) {
    super(new HashMap<String, Object>()) ;

    cr = builder.cr;
    f = builder.f;
    k = builder.k;
    variant = builder.variant;

    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);
  }

  /** Execute() method */
  public Object execute(Object object) throws JMetalException {
    Object[] parameters = (Object[]) object;
    Solution current = (Solution) parameters[0];
    Solution[] parent = (Solution[]) parameters[1];

    Solution child;

    if (!solutionTypeIsValid(parent)) {
      throw new JMetalException("DifferentialEvolutionCrossover.execute: " +
        " the solutions " +
        "are not of the right type. The type should be 'Real' or 'ArrayReal', but " +
        parent[0].getType() + " and " +
        parent[1].getType() + " and " +
        parent[2].getType() + " are obtained");
    }

    int jrand;

    child = new Solution(current);

    XReal xParent0 = new XReal(parent[0]);
    XReal xParent1 = new XReal(parent[1]);
    XReal xParent2 = new XReal(parent[2]);
    XReal xCurrent = new XReal(current);
    XReal xChild = new XReal(child);

    int numberOfVariables = xParent0.getNumberOfDecisionVariables();
    jrand = PseudoRandom.randInt(0, numberOfVariables - 1);

    // STEP 4. Checking the DE variant
    if (("rand/1/bin".equals(variant)) ||
      "best/1/bin".equals(variant)) {
      for (int j = 0; j < numberOfVariables; j++) {
        if (PseudoRandom.randDouble(0, 1) < cr || j == jrand) {
          double value;
          value = xParent2.getValue(j) + f * (xParent0.getValue(j) -
            xParent1.getValue(j));

          if (value < xChild.getLowerBound(j)) {
            value = xChild.getLowerBound(j);
          }
          if (value > xChild.getUpperBound(j)) {
            value = xChild.getUpperBound(j);
          }
          xChild.setValue(j, value);
        } else {
          double value;
          value = xCurrent.getValue(j);
          xChild.setValue(j, value);
        }
      }
    } else if ("rand/1/exp".equals(variant) ||
      "best/1/exp".equals(variant)) {
      for (int j = 0; j < numberOfVariables; j++) {
        if (PseudoRandom.randDouble(0, 1) < cr || j == jrand) {
          double value;
          value = xParent2.getValue(j) + f * (xParent0.getValue(j) -
            xParent1.getValue(j));

          if (value < xChild.getLowerBound(j)) {
            value = xChild.getLowerBound(j);
          }
          if (value > xChild.getUpperBound(j)) {
            value = xChild.getUpperBound(j);
          }

          xChild.setValue(j, value);
        } else {
          cr = 0.0;
          double value;
          value = xCurrent.getValue(j);
          xChild.setValue(j, value);
        }
      }
    } else if ("current-to-rand/1".equals(variant) ||
      "current-to-best/1".equals(variant)) {
      for (int j = 0; j < numberOfVariables; j++) {
        double value;
        value = xCurrent.getValue(j) + k * (xParent2.getValue(j) -
          xCurrent.getValue(j)) +
          f * (xParent0.getValue(j) - xParent1.getValue(j));

        if (value < xChild.getLowerBound(j)) {
          value = xChild.getLowerBound(j);
        }
        if (value > xChild.getUpperBound(j)) {
          value = xChild.getUpperBound(j);
        }

        xChild.setValue(j, value);
      }
    } else if ("current-to-rand/1/bin".equals(variant) ||
      "current-to-best/1/bin".equals(variant)) {
      for (int j = 0; j < numberOfVariables; j++) {
        if (PseudoRandom.randDouble(0, 1) < cr || j == jrand) {
          double value;
          value = xCurrent.getValue(j) + k * (xParent2.getValue(j) -
            xCurrent.getValue(j)) +
            f * (xParent0.getValue(j) - xParent1.getValue(j));

          if (value < xChild.getLowerBound(j)) {
            value = xChild.getLowerBound(j);
          }
          if (value > xChild.getUpperBound(j)) {
            value = xChild.getUpperBound(j);
          }

          xChild.setValue(j, value);
        } else {
          double value;
          value = xCurrent.getValue(j);
          xChild.setValue(j, value);
        }
      }
    } else if ("current-to-rand/1/exp".equals(variant) ||
      "current-to-best/1/exp".equals(variant)) {
      for (int j = 0; j < numberOfVariables; j++) {
        if (PseudoRandom.randDouble(0, 1) < cr || j == jrand) {
          double value;
          value = xCurrent.getValue(j) + k * (xParent2.getValue(j) -
            xCurrent.getValue(j)) +
            f * (xParent0.getValue(j) - xParent1.getValue(j));

          if (value < xChild.getLowerBound(j)) {
            value = xChild.getLowerBound(j);
          }
          if (value > xChild.getUpperBound(j)) {
            value = xChild.getUpperBound(j);
          }

          xChild.setValue(j, value);
        } else {
          cr = 0.0;
          double value;
          value = xCurrent.getValue(j);
          xChild.setValue(j, value);
        }
      }
    } else {
      JMetalLogger.logger.severe("DifferentialEvolutionCrossover.execute: " +
        " unknown DE variant (" + variant + ")");
      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".execute()");
    }
    return child;
  }

  /*
   * Getters
   */
  public double getCr() {
    return cr;
  }

  public double getF() {
    return f;
  }

  public double getK() {
    return k;
  }

  public String getVariant() {
    return variant;
  }

  /*
   * Builder class
   */
  public static class Builder {
    private double cr;
    private double f;
    private double k;
    // DE variant (rand/1/bin, rand/1/exp, etc.)
    private String variant;

    public Builder() {
      cr = DEFAULT_CR ;
      f = DEFAULT_F ;
      k = DEFAULT_K ;
      variant = DEFAULT_DE_VARIANT ;
    }

    public Builder cr(double cr) {
      if ((cr < 0) || (cr > 1.0)) {
        throw new JMetalException("Invalid CR value: " + cr ) ;
      } else {
        this.cr = cr ;
      }

      return this ;
    }

    public Builder f(double f) {
      if ((f < 0) || (f > 1.0)) {
        throw new JMetalException("Invalid F value: " + f) ;
      } else {
        this.f = f;
      }

      return this ;
    }

    public Builder k(double k) {
      if ((k < 0) || (k > 1.0)) {
        throw new JMetalException("Invalid K value: " + k) ;
      } else {
        this.k = k;
      }

      return this ;
    }

    public Builder variant(String variant) {
      Vector<String> validVariants = new Vector<String>(Arrays.asList(VALID_VARIANTS)) ;
      if (validVariants.contains(variant)) {
        this.variant = variant ;
      } else {
        throw new JMetalException("Invalid DE variant: " + variant) ;
      }

      return this ;
    }
    public DifferentialEvolutionCrossover build() {
      return new DifferentialEvolutionCrossover(this) ;
    }
  }
}
