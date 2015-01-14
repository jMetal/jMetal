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

package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.impl.PointSolution;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 30/05/14.
 */
public class SetCoverage implements QualityIndicator {
  private static final String NAME = "SC" ;

  private Comparator<Solution> dominance;

  /**
   * Calculates the set coverage of set1 over set2
   * @param set1
   * @param set2
   * @return The value of the set coverage
   */
  public double setCoverage(List<? extends Solution> set1, List<? extends Solution<?>> set2) {
    double result = 0.0 ;
    int sum = 0 ;

    if (set2.size()==0) {
      if (set1.size()==0) {
        result = 0.0 ;
      } else {
        result = 1.0 ;
      }
    } else {
      dominance = new DominanceComparator();

      for (int i = 0; i < set2.size(); i++) {
        if (solutionIsDominatedBySolutionSet(set2.get(i), (List<Solution>) set1)) {
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
  public double setCoverage(String file1, String file2)
      throws JMetalException, FileNotFoundException {
    Front front1 = new ArrayFront() ;
    front1.readFrontFromFile(file1);
    Front front2 = new ArrayFront() ;
    front2.readFrontFromFile(file2);

    List<PointSolution> solutionSet1 = transformArraysToSolutionSet(front1) ;
    List<PointSolution> solutionSet2 = transformArraysToSolutionSet(front2) ;

    return setCoverage(solutionSet1, solutionSet2) ;
  }

  private boolean solutionIsDominatedBySolutionSet(Solution solution, List<Solution> solutionSet) {
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

  public List<PointSolution> transformArraysToSolutionSet(Front front) throws JMetalException {
    int solutionSetSize = front.getNumberOfPoints() ;
    int numberOfObjectives = front.getPoint(0).getNumberOfDimensions() ;
    List<PointSolution> solutionSet = new ArrayList<>(solutionSetSize) ;

    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      PointSolution solution = new PointSolution(numberOfObjectives);
      for (int j = 0 ; j < numberOfObjectives; j++) {
        solution.setObjective(j, front.getPoint(i).getDimensionValue(j));
      }

      solutionSet.add(solution) ;
    }

    return solutionSet ;
  }

  @Override public double execute(Front set1, Front set2) {
    return this.setCoverage(transformArraysToSolutionSet(set1), transformArraysToSolutionSet(set2));
  }

  @Override public String getName() {
    return NAME;
  }
}
