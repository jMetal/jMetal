//  QualityIndicatorGetter.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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

package org.uma.jmetal.qualityIndicator;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;

/**
 * QualityIndicatorGetter class
 */
public class QualityIndicatorGetter {
  public org.uma.jmetal.qualityIndicator.util.MetricsUtil utils;
  SolutionSet trueParetoFront;
  double trueParetoFrontHypervolume;
  Problem problem;

  /**
   * Constructor
   *
   * @param problem         The problem
   * @param paretoFrontFile Pareto front file
   */
  public QualityIndicatorGetter(Problem problem, String paretoFrontFile) {
    this.problem = problem;
    utils = new org.uma.jmetal.qualityIndicator.util.MetricsUtil();
    trueParetoFront = utils.readNonDominatedSolutionSet(paretoFrontFile);
    trueParetoFrontHypervolume = new Hypervolume().hypervolume(
      trueParetoFront.writeObjectivesToMatrix(),
      trueParetoFront.writeObjectivesToMatrix());
  }

  /**
   * Returns the hypervolume of solution set
   *
   * @param solutionSet Solution set
   * @return The value of the hypervolume indicator
   */
  public double getHypervolume(SolutionSet solutionSet) {
    return new Hypervolume().hypervolume(solutionSet.writeObjectivesToMatrix(),
      trueParetoFront.writeObjectivesToMatrix());
  }


  /**
   * Returns the hypervolume of the true Pareto front
   *
   * @return The hypervolume of the true Pareto front
   */
  public double getTrueParetoFrontHypervolume() {
    return trueParetoFrontHypervolume;
  }

  /**
   * Returns the inverted generational distance of solution set
   *
   * @param solutionSet Solution set
   * @return The value of the hypervolume indicator
   */
  public double getIGD(SolutionSet solutionSet) {
    return new InvertedGenerationalDistance().invertedGenerationalDistance(
      solutionSet.writeObjectivesToMatrix(),
      trueParetoFront.writeObjectivesToMatrix(),
      problem.getNumberOfObjectives());
  }

  /**
   * Returns the generational distance of solution set
   *
   * @param solutionSet Solution set
   * @return The value of the hypervolume indicator
   */
  public double getGD(SolutionSet solutionSet) {
    return new GenerationalDistance().generationalDistance(
      solutionSet.writeObjectivesToMatrix(),
      trueParetoFront.writeObjectivesToMatrix(),
      problem.getNumberOfObjectives());
  }

  /**
   * Returns the spread of solution set
   *
   * @param solutionSet Solution set
   * @return The value of the hypervolume indicator
   */
  public double getSpread(SolutionSet solutionSet) {
    return new Spread().spread(solutionSet.writeObjectivesToMatrix(),
      trueParetoFront.writeObjectivesToMatrix(),
      problem.getNumberOfObjectives());
  }

  /**
   * Returns the epsilon indicator of solution set
   *
   * @param solutionSet Solution set
   * @return The value of the hypervolume indicator
   * @throws org.uma.jmetal.util.JMetalException
   */
  public double getEpsilon(SolutionSet solutionSet) {
    return new Epsilon().epsilon(solutionSet.writeObjectivesToMatrix(),
      trueParetoFront.writeObjectivesToMatrix(),
      problem.getNumberOfObjectives());
  }
}
