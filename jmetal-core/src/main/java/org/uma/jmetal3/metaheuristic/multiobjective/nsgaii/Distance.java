//  Distance.java
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

package org.uma.jmetal3.metaheuristic.multiobjective.nsgaii;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.attributes.Attributes;
import org.uma.jmetal3.encoding.attributes.CrowdingDistance;
import org.uma.jmetal3.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements some utilities for calculating distances
 */
public class Distance {

  /**
   * Assigns crowding distances to all solutions in a <code>SolutionSet</code>.
   *
   * @param solutionSet The <code>SolutionSet</code>.
   * @throws org.uma.jmetal.util.JMetalException
   */

  public static void crowdingDistanceAssignment(List<? extends Solution> solutionSet) throws
          JMetalException {
    int size = solutionSet.size();
    CrowdingDistance attr ;

    if (size == 0) {
      return;
    }

    if (size == 1) {
      solutionSet.get(0).getAttributes().setAttribute("CrowdingDistance", Double.POSITIVE_INFINITY);
      return;
    }

    if (size == 2) {
      solutionSet.get(0).getAttributes().setAttribute("CrowdingDistance", Double.POSITIVE_INFINITY);
      solutionSet.get(1).getAttributes().setAttribute("CrowdingDistance", Double.POSITIVE_INFINITY);
      return;
    }

    //Use a new SolutionSet to avoid altering the original solutionSet
    List<Solution> front = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      front.add(solutionSet.get(i));
    }

    for (int i = 0; i < size; i++) {
      front.get(i).getAttributes().setAttribute("CrowdingDistance", 0.0);
    }

    double objetiveMaxn;
    double objetiveMinn;
    double distance;

    int numberOfObjectives = solutionSet.get(0).getNumberOfObjectives() ;

    for (int i = 0; i < numberOfObjectives; i++) {
      // Sort the population by Obj n            
      front.sort(new ObjectiveComparator(i));
      objetiveMinn = front.get(0).getObjective(i);
      objetiveMaxn = front.get(front.size() - 1).getObjective(i);

      //Set de crowding distance            
      front.get(0).getAttributes().setAttribute("CrowdingDistance", Double.POSITIVE_INFINITY);
      front.get(size - 1).getAttributes().setAttribute("CrowdingDistance", Double.POSITIVE_INFINITY);

      for (int j = 1; j < size - 1; j++) {
        distance = front.get(j + 1).getObjective(i) - front.get(j - 1).getObjective(i);
        distance = distance / (objetiveMaxn - objetiveMinn);
        distance += (Double)front.get(j).getAttributes().getAttribute("CrowdingDistance");
        front.get(j).getAttributes().setAttribute("CrowdingDistance", distance);
      }
    }
  }
}

