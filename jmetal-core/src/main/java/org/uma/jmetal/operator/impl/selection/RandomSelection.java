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
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

/**
 * This class implements a random selection operator used for selecting a N number of solutions from
 * a list
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class RandomSelection implements SelectionOperator<List<? extends Solution>,List<? extends Solution>> {
  private JMetalRandom randomGenerator ;
  private int numberOfSolutionsToBeReturned ;

  /** Constructor */
  public RandomSelection() {
    this(1) ;
  }

  /** Constructor */
  public RandomSelection(int numberOfSolutionsToBeReturned) {
    randomGenerator = JMetalRandom.getInstance() ;
    this.numberOfSolutionsToBeReturned = numberOfSolutionsToBeReturned ;
  }

  /** Execute() method */
  public List<? extends Solution> execute(List<? extends Solution> solutionList) {
    if (null == solutionList) {
      throw new JMetalException("The solution list is null") ;
    } else if (solutionList.isEmpty()) {
      throw new JMetalException("The solution list is empty") ;
    } else if (solutionList.size() < numberOfSolutionsToBeReturned) {
      throw new JMetalException("The solution list size (" + solutionList.size() +") is less than "
          + "the number of requested solutions ("+numberOfSolutionsToBeReturned+")") ;
    }

    return SolutionListUtils.selectNRandomDifferentSolutions(numberOfSolutionsToBeReturned, solutionList) ;
  /*
    List<Solution<?>> resultList = new ArrayList<>(numberOfSolutionsToBeReturned) ;
    if (solutionList.size() == 1) {
      resultList.add(solutionList.get(0)) ;
    } else {
      Collection<Integer> positions = new HashSet<>(numberOfSolutionsToBeReturned) ;
      while(positions.size() < numberOfSolutionsToBeReturned) {
        int nextPosition = randomGenerator.nextInt(0, solutionList.size() - 1) ;
        if (!positions.contains(nextPosition)) {
          positions.add(nextPosition) ;
          resultList.add(solutionList.get(nextPosition)) ;
        }
      }
    }

    return resultList;
    */
  }
}
