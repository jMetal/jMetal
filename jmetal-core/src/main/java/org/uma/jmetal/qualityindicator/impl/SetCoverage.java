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
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.FrontUtils;

import java.util.Comparator;
import java.util.List;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class SetCoverage implements QualityIndicator {
  private static final String NAME = "SC" ;

  private Comparator<Solution> dominance;

  @Override public double execute(Front frontA, Front frontB) {
    if (frontA == null) {
      throw new JMetalException("The front A is null") ;
    } else if (frontB == null) {
      throw new JMetalException("The front B is null");
    }

    return this.setCoverage(FrontUtils.convertFrontToSolutionList(frontA),
        FrontUtils.convertFrontToSolutionList(frontB));
  }

  @Override
  public double execute(List<? extends Solution> solutionListA,
      List<? extends Solution> solutionListB) {

    if (solutionListA == null) {
      throw new JMetalException("The list A is null") ;
    } else if (solutionListB == null) {
      throw new JMetalException("The list B is null");
    }

    return this.setCoverage(solutionListA, solutionListB) ;
  }

  /**
   * Calculates the set coverage of set1 over set2
   * @param set1
   * @param set2
   * @return The value of the set coverage
   */
  public double setCoverage(List<? extends Solution> set1, List<? extends Solution> set2) {
    double result ;
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
        if (SolutionListUtils.isSolutionDominatedBySolutionList(set2.get(i), (List<Solution>) set1)) {
          sum++;
        }
      }
      result = (double)sum/set2.size() ;
    }
    return result ;
  }

  @Override public String getName() {
    return NAME;
  }
}
