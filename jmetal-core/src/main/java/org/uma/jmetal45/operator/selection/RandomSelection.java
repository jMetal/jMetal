//  RandomSelection.java
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
import org.uma.jmetal45.util.random.PseudoRandom;

/**
 * This class implements a random selection operator used for selecting two
 * random parents
 */
public class RandomSelection extends Selection {
  private static final long serialVersionUID = -3975079865177120226L;

  /** Constructor */
  private RandomSelection(Builder builder) {
  }

  /** Execute() method */
  public Object execute(Object object) {
    if (null == object) {
      throw new JMetalException("Parameter is null") ;
    } else if (!(object instanceof SolutionSet)) {
      throw new JMetalException("Invalid parameter class") ;
    } else if (((SolutionSet)object).size() == 0) {
      throw new JMetalException("Solution set size is 0") ;
    } else if (((SolutionSet)object).size() == 1) {
      throw new JMetalException("Solution set size is 1") ;
    }

    SolutionSet population = (SolutionSet) object;

    int pos1, pos2;
    pos1 = PseudoRandom.randInt(0, population.size() - 1);
    pos2 = PseudoRandom.randInt(0, population.size() - 1);
    while ((pos1 == pos2) && (population.size() > 1)) {
      pos2 = PseudoRandom.randInt(0, population.size() - 1);
    }

    Solution[] parents = new Solution[2];
    parents[0] = population.get(pos1);
    parents[1] = population.get(pos2);

    return parents;
  }

  /** Builder class*/
  static public class Builder {

    public RandomSelection build() {
      return new RandomSelection(this) ;
    }
  }
}
