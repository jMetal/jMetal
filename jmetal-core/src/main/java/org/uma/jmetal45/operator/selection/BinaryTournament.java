//  BinaryTournament.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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

package org.uma.jmetal45.operator.selection;

import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.core.SolutionSet;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.comparator.DominanceComparator;
import org.uma.jmetal45.util.random.PseudoRandom;

import java.util.Comparator;

public class BinaryTournament extends Selection {
  private static final long serialVersionUID = 1727470902640158437L;

  private Comparator<Solution> comparator;

  /** Constructor */
  private BinaryTournament(Builder builder) {
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

    public BinaryTournament build() {
      return new BinaryTournament(this) ;
    }
  }

  /** Execute() method */
  public Object execute(Object object) {
    if (null == object) {
      throw new JMetalException("Parameter is null") ;
    } else if (!(object instanceof SolutionSet)) {
      throw new JMetalException("Invalid parameter class") ;
    } else if (((SolutionSet)object).size() == 0) {
      throw new JMetalException("Solution set size is 0") ;
    }

    SolutionSet solutionSet = (SolutionSet) object;

    int indexSolution1 = PseudoRandom.randInt(0, solutionSet.size() - 1) ;
    int indexSolution2 = PseudoRandom.randInt(0, solutionSet.size() - 1) ;

    if (solutionSet.size() >= 2) {
      while (indexSolution1 == indexSolution2) {
        indexSolution2 = PseudoRandom.randInt(0, solutionSet.size() - 1) ;
      }
    }

    Solution solution1 = solutionSet.get(indexSolution1) ;
    Solution solution2 = solutionSet.get(indexSolution2) ;

    int flag = comparator.compare(solution1, solution2);
    if (flag == -1) {
      return solution1;
    } else if (flag == 1) {
      return solution2;
    } else {
      if (PseudoRandom.randDouble() < 0.5) {
        return solution1;
      } else {
        return solution2;
      }
    }
  }
}
