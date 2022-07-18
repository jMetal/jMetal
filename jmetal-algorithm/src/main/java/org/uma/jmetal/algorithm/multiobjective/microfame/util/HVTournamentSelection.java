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

package org.uma.jmetal.algorithm.multiobjective.microfame.util;

import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.SolutionUtils;
import org.uma.jmetal.util.comparator.HypervolumeContributionComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * @author Alex
 * @version 1.0
 *     <p>Applies a hv tournament selection to return a solution from a list.
 */
public class HVTournamentSelection<S extends Solution<?>> implements SelectionOperator<List<S>, S> {
  private Comparator<S> comparator;

  private final int numberOfTournaments;

  /** Constructor */
  public HVTournamentSelection(int numberOfTournaments) {
    this(new HypervolumeContributionComparator<S>(), numberOfTournaments);
  }

  /** Constructor */
  public HVTournamentSelection(Comparator<S> comparator, int numberOfTournaments) {
    this.numberOfTournaments = numberOfTournaments;
    this.comparator = comparator;
  }

  @Override
  /** Execute() method */
  public S execute(@Nullable List<S> solutionList) {
    if (null == solutionList) {
      throw new JMetalException("The solution list is null");
    } else if (solutionList.isEmpty()) {
      throw new JMetalException("The solution list is empty");
    }

    S result;
    if (solutionList.size() == 1) {
      result = solutionList.get(0);
    } else {
      result = SolutionListUtils.selectNRandomDifferentSolutions(1, solutionList).get(0);
      int count = 1; // at least 2 solutions are compared
      do {
        S candidate = SolutionListUtils.selectNRandomDifferentSolutions(1, solutionList).get(0);
        result = SolutionUtils.getBestSolution(result, candidate, comparator);
      } while (++count < this.numberOfTournaments);
    }

    return result;
  }
}
