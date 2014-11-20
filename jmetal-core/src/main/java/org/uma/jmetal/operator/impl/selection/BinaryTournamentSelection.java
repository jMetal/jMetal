//  BinaryTournament.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro, Juan J. Durillo
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

package org.uma.jmetal.operator.impl.selection;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.Comparator;
import java.util.List;

public class BinaryTournamentSelection implements SelectionOperator<List<Solution>,Solution> {
  private Comparator<Solution> comparator;
  private JMetalRandom randomGenerator ;

  /** Constructor */
  public BinaryTournamentSelection() {
    randomGenerator = JMetalRandom.getInstance() ;
    comparator = new DominanceComparator() ;
  }

  /** Constructor */
  public BinaryTournamentSelection(Comparator<Solution> comparator) {
    this.comparator = comparator ;
  }

  @Override
  /** Execute() method */
  public Solution execute(List<Solution> solutions) {
    if (null == solutions) {
      throw new JMetalException("Parameter is null") ;
    } else if (solutions.size() == 0) {
      throw new JMetalException("Solution set size is 0") ;
    }

    int indexSolution1 = randomGenerator.nextInt(0, solutions.size() - 1) ;
    int indexSolution2 = randomGenerator.nextInt(0, solutions.size() - 1) ;

    if (solutions.size() >= 2) {
      while (indexSolution1 == indexSolution2) {
        indexSolution2 = randomGenerator.nextInt(0, solutions.size() - 1) ;
      }
    }

    Solution solution1 = solutions.get(indexSolution1) ;
    Solution solution2 = solutions.get(indexSolution2) ;

    Solution result ;

    int flag = comparator.compare(solution1, solution2);
    if (flag == -1) {
      result = solution1;
    } else if (flag == 1) {
      result = solution2;
    } else {
      if (randomGenerator.nextDouble() < 0.5) {
        result = solution1;
      } else {
        result = solution2;
      }
    }

    return result;
  }
}
