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

package org.uma.jmetal3.operator.selection.impl;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.operator.selection.SelectionOperator;
import org.uma.jmetal3.util.comparator.DominanceComparator;

import java.util.Comparator;
import java.util.List;

public class BinaryTournamentSelection implements SelectionOperator<List<Solution>,Solution> {
  private Comparator<Solution> comparator;

  /** Constructor */
  private BinaryTournamentSelection(Builder builder) {
    comparator = builder.comparator ;
  }

  /** Builder class */
  public static class Builder {
    Comparator<Solution> comparator ;

    public Builder() {
      comparator = new DominanceComparator() ;
    }

    public Builder setComparator(Comparator<Solution> comparator) {
      this.comparator = comparator ;

      return this ;
    }

    public BinaryTournamentSelection build() {
      return new BinaryTournamentSelection(this) ;
    }
  }

  @Override
  /** Execute() method */
  public Solution execute(List<Solution> solutions) {
    if (null == solutions) {
      throw new JMetalException("Parameter is null") ;
    } else if (solutions.size() == 0) {
      throw new JMetalException("Solution set size is 0") ;
    }

    Solution result ;

    int indexSolution1 = PseudoRandom.randInt(0, solutions.size() - 1) ;
    int indexSolution2 = PseudoRandom.randInt(0, solutions.size() - 1) ;

    if (solutions.size() >= 2) {
      while (indexSolution1 == indexSolution2) {
        indexSolution2 = PseudoRandom.randInt(0, solutions.size() - 1) ;
      }
    }

    Solution solution1 = solutions.get(indexSolution1) ;
    Solution solution2 = solutions.get(indexSolution2) ;

    int flag = comparator.compare(solution1, solution2);
    if (flag == -1) {
      result = solution1;
    } else if (flag == 1) {
      result = solution2;
    } else {
      if (PseudoRandom.randDouble() < 0.5) {
        result = solution1;
      } else {
        result = solution2;
      }
    }

    return result;
  }
}
