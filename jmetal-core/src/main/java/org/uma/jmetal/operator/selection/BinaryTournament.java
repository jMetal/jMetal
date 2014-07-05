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

package org.uma.jmetal.operator.selection;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Comparator;
import java.util.HashMap;

public class BinaryTournament extends Selection {
  private static final long serialVersionUID = 1727470902640158437L;

  private Comparator<Solution> comparator_;

  /** Constructor */
  @Deprecated
  public BinaryTournament(HashMap<String, Object> parameters) {
    super(parameters);
    if ((parameters != null) && (parameters.get("comparator") != null)) {
      comparator_ = (Comparator<Solution>) parameters.get("comparator");
    } else {
      comparator_ = new DominanceComparator();
    }
  }

  /** Constructor */
  private BinaryTournament(Builder builder) {
    super(new HashMap<String, Object>()) ;

    comparator_ = new DominanceComparator();
  }

  /** execute method */
  public Object execute(Object object) {
    SolutionSet solutionSet = (SolutionSet) object;
    Solution solution1, solution2;
    solution1 = solutionSet.get(PseudoRandom.randInt(0, solutionSet.size() - 1));
    solution2 = solutionSet.get(PseudoRandom.randInt(0, solutionSet.size() - 1));

    if (solutionSet.size() >= 2) {
      while (solution1.equals(solution2)) {
        solution2 = solutionSet.get(PseudoRandom.randInt(0, solutionSet.size() - 1));
      }
    }

    int flag = comparator_.compare(solution1, solution2);
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

  public static class Builder {
    Comparator comparator ;

    public Builder() {
      comparator = new DominanceComparator() ;
    }

    public Builder comparator(Comparator comparator) {
      this.comparator = comparator ;

      return this ;
    }

    public BinaryTournament build() {
      return new BinaryTournament(this) ;
    }
  }
}
