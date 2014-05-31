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

package jmetal.qualityIndicator;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.comparator.DominanceComparator;

import java.util.Comparator;

/**
 * Created by Antonio J. Nebro on 30/05/14.
 */
public class SetCoverage {
  private Comparator<Solution> dominance_;

  public double setCoverage(SolutionSet a, SolutionSet b) {
    double result = 0.0 ;
    int sum = 0 ;

    if (b.size() > 0) {
      dominance_ = new DominanceComparator();

      for (int i = 0; i < b.size(); i++) {
        if (solutionIsDominatedBySolutionSet(b.get(i), a)) {
          sum++;
        }
      }
      result = (double)sum/b.size() ;
    }
    return result ;
  }

  private boolean solutionIsDominatedBySolutionSet(Solution solution, SolutionSet solutionSet) {
    boolean result = false ;

    int i = 0 ;

    while (!result && (i < solutionSet.size())) {
      if (dominance_.compare(solution, solutionSet.get(i)) == 1) {
        result = true ;
      }
      i++ ;
    }

    return result ;
  }
}
