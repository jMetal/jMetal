//  DifferentialEvolutionSelection.java
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
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing the selection operator used in DE: three different solutions
 * are returned from a population.
 */
public class DifferentialEvolutionSelection implements SelectionOperator<List<DoubleSolution>,List<DoubleSolution>> {
  private int solutionListIndex = Integer.MIN_VALUE ;
  private JMetalRandom randomGenerator ;

  /** Constructor */
  public DifferentialEvolutionSelection() {
    randomGenerator = JMetalRandom.getInstance() ;
  }

  public void setIndex(int index) {
    this.solutionListIndex = index ;
  }

  /** Execute() method  */
  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> solutionSet) {
    if (null == solutionSet) {
      throw new JMetalException("Parameter is null") ;
    } else if (solutionSet.size() < 4) {
      throw new JMetalException(
              "DifferentialEvolutionSelection: the population has less than four solutions");
    } else if ((solutionListIndex < 0) || (solutionListIndex > solutionSet.size())) {
      throw new JMetalException(
              "DifferentialEvolutionSelection: index value invalid: " + solutionListIndex );
    }

    List<DoubleSolution> parents = new ArrayList<>(3);
    int r1, r2, r3;

    do {
      r1 = randomGenerator.nextInt(0, solutionSet.size() - 1);
    } while (r1 == solutionListIndex);
    do {
      r2 = randomGenerator.nextInt(0, solutionSet.size() - 1);
    } while (r2 == solutionListIndex || r2 == r1);
    do {
      r3 = randomGenerator.nextInt(0, solutionSet.size() - 1);
    } while (r3 == solutionListIndex || r3 == r1 || r3 == r2);

    parents.add(solutionSet.get(r1));
    parents.add(solutionSet.get(r2));
    parents.add(solutionSet.get(r3));

    return parents;
  }
}
