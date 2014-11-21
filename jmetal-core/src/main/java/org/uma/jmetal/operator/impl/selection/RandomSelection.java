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

package org.uma.jmetal.operator.impl.selection;

import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a random selection operator used for selecting two
 * random parents
 */
public class RandomSelection implements SelectionOperator<List<Solution>,List<Solution>> {
  private JMetalRandom randomGenerator ;

  /** Constructor */
  public RandomSelection() {
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /** Execute() method */
  public List<Solution> execute(List<Solution> solutions) {
    if (null == solutions) {
      throw new JMetalException("Parameter is null") ;
    } else if (solutions.size() == 0) {
      throw new JMetalException("Solution set size is 0") ;
    } else if (solutions.size() == 1) {
      throw new JMetalException("Solution set size is 1") ;
    }

    int pos1, pos2;
    pos1 = randomGenerator.nextInt(0, solutions.size() - 1);
    pos2 = randomGenerator.nextInt(0, solutions.size() - 1);
    while ((pos1 == pos2) && (solutions.size() > 1)) {
      pos2 = randomGenerator.nextInt(0, solutions.size() - 1);
    }

    List<Solution> parents = new ArrayList<>(2);
    parents.add(solutions.get(pos1));
    parents.add(solutions.get(pos2));

    return parents;
  }
}
