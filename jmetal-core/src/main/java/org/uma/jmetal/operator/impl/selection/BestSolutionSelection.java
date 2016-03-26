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

import java.util.Comparator;
import java.util.List;

/**
 * This class implements a selection operator used for selecting the best solution
 * in a list according to a given comparator.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class BestSolutionSelection<S extends Solution<?>> implements SelectionOperator<List<S>, S> {
private Comparator<S> comparator ;

  public BestSolutionSelection(Comparator<S> comparator) {
    this.comparator = comparator ;
  }

  /** Execute() method */
  public S execute(List<S> solutionList) {
    if (null == solutionList) {
      throw new JMetalException("The solution list is null") ;
    } else if (solutionList.isEmpty()) {
      throw new JMetalException("The solution list is empty") ;
    }

    int bestSolution = 0 ;
    for (int i = 1; i < solutionList.size(); i++) {
      if (comparator.compare(solutionList.get(i), solutionList.get(bestSolution)) < 0) {
        bestSolution = i;
      }
    }

    return solutionList.get(bestSolution) ;
  }
}
