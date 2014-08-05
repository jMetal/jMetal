//  StrenghtRawFitnessArchive.java
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

package org.uma.jmetal.util.archive;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.util.Spea2Fitness;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.EqualSolutions;
import org.uma.jmetal.util.comparator.FitnessComparator;

import java.util.Comparator;

/**
 * This class implements a bounded archive based on strength raw fitness (as defined in SPEA2).
 */
public class StrengthRawFitnessArchive extends Archive {
  private static final long serialVersionUID = 7432108704079114025L;

  private int maxSize;

  private Comparator<Solution> dominanceComparator;
  private Comparator<Solution> fitnessComparator;
  private Comparator<Solution> equalsComparator;

  /** Constructor */
  public StrengthRawFitnessArchive(int maxSize) {
    super(maxSize);
    this.maxSize = maxSize;
    dominanceComparator = new DominanceComparator();
    equalsComparator = new EqualSolutions();
    fitnessComparator = new FitnessComparator();
  }

  /**
   * Adds a Solution to the archive. If the <code>Solution</code>
   * is dominated by any member of the archive then it is discarded. If the
   * <code>Solution</code> dominates some members of the archive, these are
   * removed. If the archive is full and the <code>Solution</code> has to be
   * inserted, all the solutions are ordered by his strengthRawFitness value and
   * the one having the worst value is removed.
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   */
  public boolean add(Solution solution) {
    int flag = 0;
    int i = 0;
    Solution aux;
    while (i < solutionsList.size()) {
      aux = solutionsList.get(i);
      flag = dominanceComparator.compare(solution, aux);
      if (flag == 1) {
        return false;
      } else if (flag == -1) {
        solutionsList.remove(i);
      } else {
        if (equalsComparator.compare(aux, solution) == 0) {
          return false;
        }
        i++;
      }
    }
    solutionsList.add(solution);

    if (size() > maxSize) {
      (new Spea2Fitness(this)).fitnessAssign();
      remove(indexWorst(fitnessComparator));
    }
    return true;
  }
}
