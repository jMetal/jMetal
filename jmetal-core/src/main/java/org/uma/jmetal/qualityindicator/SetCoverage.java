//  SetCoverage.java
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
//

package org.uma.jmetal.qualityindicator;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.qualityindicator.util.MetricsUtil;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.Comparator;

/**
 * Created by Antonio J. Nebro on 30/05/14.
 */
public class SetCoverage {
  private Comparator<Solution> dominance;

  /**
   * Calculates the set coverage of set1 over set2
   * @param set1
   * @param set2
   * @return The value of the set coverage
   */
  public double setCoverage(SolutionSet set1, SolutionSet set2) {
    double result = 0.0 ;
    int sum = 0 ;

    if (set2.isEmtpy()) {
      if (set1.isEmtpy()) {
        result = 0.0 ;
      } else {
        result = 1.0 ;
      }
    } else {
      dominance = new DominanceComparator();

      for (int i = 0; i < set2.size(); i++) {
        if (solutionIsDominatedBySolutionSet(set2.get(i), set1)) {
          sum++;
        }
      }
      result = (double)sum/set2.size() ;
    }
    return result ;
  }

  /**
   * Calculates the set coverage of the front stored in file1 over the front stored in file2
   * @param file1
   * @param file2
   * @return The value of the set coverage
   * @throws org.uma.jmetal.util.JMetalException
   */
  public double setCoverage(String file1, String file2) throws JMetalException {
    MetricsUtil utils = new MetricsUtil();
    double[][]front1 = utils.readFront(file1) ;
    double[][]front2 = utils.readFront(file2) ;

    SolutionSet solutionSet1 = transformArraysToSolutionSet(front1) ;
    SolutionSet solutionSet2 = transformArraysToSolutionSet(front2) ;

    return setCoverage(solutionSet1, solutionSet2) ;
  }

  private boolean solutionIsDominatedBySolutionSet(Solution solution, SolutionSet solutionSet) {
    boolean result = false ;

    int i = 0 ;

    while (!result && (i < solutionSet.size())) {
      if (dominance.compare(solution, solutionSet.get(i)) == 1) {
        result = true ;
      }
      i++ ;
    }

    return result ;
  }

  public SolutionSet transformArraysToSolutionSet(double[][] array) throws JMetalException {
    int solutionSetSize = array.length ;
    int numberOfObjectives = array[0].length ;
    SolutionSet solutionSet = new SolutionSet(solutionSetSize) ;

    for (int i = 0; i < array.length; i++) {
      Solution solution = new Solution(numberOfObjectives) ;
      for (int j = 0 ; j < numberOfObjectives; j++) {
        solution.setObjective(j, array[i][j]);
      }

      solutionSet.add(solution) ;
    }

    return solutionSet ;
  }

  public static void main(String args[]) throws JMetalException {
    if (args.length < 2) {
      throw new JMetalException(
        "Error using Hypervolume. Usage: \n java org.uma.jmetal.qualityindicator.SetCoverage " +
          "<SolutionFrontFile1> " +
          "<SolutionFrontFile2> ");
    } else {
       double result = new SetCoverage().setCoverage(args[0], args[1]) ;
      JMetalLogger.logger.info("Set coverage: " + result) ;
    }
  }
}
