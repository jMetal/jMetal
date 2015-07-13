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

package org.uma.jmetal.qualityindicator2.impl;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;

import java.util.Comparator;
import java.util.List;

/**
 * TODO Add comments here
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class SetCoverage extends SimpleDescribedEntity implements
    org.uma.jmetal.qualityindicator2.QualityIndicator<Pair<List<Solution<?>>, List<Solution<?>>>, Pair<Double, Double>> {

  public SetCoverage() {
    super("SC", "Set coverage") ;
  }

  private Comparator<Solution<?>> dominance;

  @Override
  public Pair<Double, Double> evaluate(Pair<List<Solution<?>>, List<Solution<?>>> pairOfSolutionLists) {
    List<Solution<?>> front1 = pairOfSolutionLists.getLeft() ;
    List<Solution<?>> front2 = pairOfSolutionLists.getRight() ;

    Pair<Double, Double> result = new ImmutablePair<>(setCoverage(front1, front2), setCoverage(front2, front1)) ;

    return result ;
  }

  /**
   * Calculates the set coverage of set1 over set2
   * @param set1
   * @param set2
   * @return The value of the set coverage
   */
  public double setCoverage(List<? extends Solution<?>> set1, List<? extends Solution<?>> set2) {
    double result ;
    int sum = 0 ;

    if (set2.size()==0) {
      if (set1.size()==0) {
        result = 0.0 ;
      } else {
        result = 1.0 ;
      }
    } else {
      dominance = new DominanceComparator<Solution<?>>();

      for (int i = 0; i < set2.size(); i++) {
        if (SolutionListUtils.isSolutionDominatedBySolutionList(set2.get(i), set1)) {
          sum++;
        }
      }
      result = (double)sum/set2.size() ;
    }
    return result ;
  }

  @Override public String getName() {
    return super.getName() ;
  }
}
