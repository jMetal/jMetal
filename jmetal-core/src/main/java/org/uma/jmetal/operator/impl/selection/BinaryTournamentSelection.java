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

package org.uma.jmetal.operator.impl.selection;

import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.SolutionUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.Comparator;
import java.util.List;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 * 
 * Applies a binary tournament selection to return the best solution between two that have been
 * chosen at random from a solution list.
 */
public class BinaryTournamentSelection implements SelectionOperator<List<Solution>,Solution> {
  private Comparator<Solution> comparator;

  /** Constructor */
  public BinaryTournamentSelection() {
    this(new DominanceComparator()) ;
  }

  /** Constructor */
  public BinaryTournamentSelection(Comparator<Solution> comparator) {
    this.comparator = comparator ;
  }

  @Override
  /** Execute() method */
  public Solution execute(List<Solution> solutionList) {
    if (null == solutionList) {
      throw new JMetalException("The solution list is null") ;
    } else if (solutionList.isEmpty()) {
      throw new JMetalException("The solution list is empty") ;
    }

    Solution result ;
    if (solutionList.size() == 1) {
      result = solutionList.get(0) ;
    } else {
      List<Solution> selectedSolutions = SolutionListUtils.selectNRandomDifferentSolutions(2, solutionList);
      result = SolutionUtils
          .getBestSolution(selectedSolutions.get(0), selectedSolutions.get(1), comparator) ;
    }

    return result;
  }
}
