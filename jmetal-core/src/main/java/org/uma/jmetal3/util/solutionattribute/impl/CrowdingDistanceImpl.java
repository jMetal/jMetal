//  CrowdingDistanceImpl.java
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

package org.uma.jmetal3.util.solutionattribute.impl;

import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.util.solutionattribute.CrowdingDistance;
import org.uma.jmetal3.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class implements some utilities for calculating distances
 */
public class CrowdingDistanceImpl implements CrowdingDistance {

  /**
   * Assigns crowding distances to all solutions in a <code>SolutionSet</code>.
   *
   * @param solutionSet The <code>SolutionSet</code>.
   * @throws org.uma.jmetal.util.JMetalException
   */

  @Override
  public void computeCrowdingDistance(List<Solution> solutionSet) {
    int size = solutionSet.size();

    if (size == 0) {
      return;
    }

    if (size == 1) {
      //RankingAndCrowdingAttr.getAttributes(solutionSet.get(0)).setCrowdingDistance(Double.POSITIVE_INFINITY);
      //solutionSet.get(0).setAttribute(ATTRIBUTE.CROWDNG, Double.POSITIVE_INFINITY);
      solutionSet.get(0).setAttribute(ATTRIBUTE.CROWDNG, Double.POSITIVE_INFINITY);
      return;
    }

    if (size == 2) {
      //RankingAndCrowdingAttr.getAttributes(solutionSet.get(0)).setCrowdingDistance(Double.POSITIVE_INFINITY);
      //RankingAndCrowdingAttr.getAttributes(solutionSet.get(1)).setCrowdingDistance(Double.POSITIVE_INFINITY);
      solutionSet.get(0).setAttribute(ATTRIBUTE.CROWDNG, Double.POSITIVE_INFINITY);
      solutionSet.get(1).setAttribute(ATTRIBUTE.CROWDNG, Double.POSITIVE_INFINITY);

      return;
    }

    //Use a new SolutionSet to avoid altering the original solutionSet
    List<Solution> front = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      front.add(solutionSet.get(i));
    }

    for (int i = 0; i < size; i++) {
      //RankingAndCrowdingAttr.getAttributes(front.get(i)).setCrowdingDistance(0.0);
      front.get(i).setAttribute(ATTRIBUTE.CROWDNG, 0.0);
    }

    double objetiveMaxn;
    double objetiveMinn;
    double distance;

    int numberOfObjectives = solutionSet.get(0).getNumberOfObjectives() ;

    for (int i = 0; i < numberOfObjectives; i++) {
      // Sort the population by Obj n
      //front.sort(new ObjectiveComparator(i));
      Collections.sort(front, new ObjectiveComparator(i)) ;
      objetiveMinn = front.get(0).getObjective(i);
      objetiveMaxn = front.get(front.size() - 1).getObjective(i);

      //Set de crowding distance
      //RankingAndCrowdingAttr.getAttributes(front.get(0)).setCrowdingDistance(Double.POSITIVE_INFINITY);
      //RankingAndCrowdingAttr.getAttributes(front.get(size - 1)).setCrowdingDistance(Double.POSITIVE_INFINITY);
      front.get(0).setAttribute(ATTRIBUTE.CROWDNG, Double.POSITIVE_INFINITY);
      front.get(size - 1).setAttribute(ATTRIBUTE.CROWDNG, Double.POSITIVE_INFINITY);

      for (int j = 1; j < size - 1; j++) {
        distance = front.get(j + 1).getObjective(i) - front.get(j - 1).getObjective(i);
        distance = distance / (objetiveMaxn - objetiveMinn);
        //distance += RankingAndCrowdingAttr.getAttributes(front.get(j)).getCrowdingDistance();
        distance += (double)front.get(j).getAttribute(ATTRIBUTE.CROWDNG);
        //RankingAndCrowdingAttr.getAttributes(front.get(j)).setCrowdingDistance(distance);
        front.get(j).setAttribute(ATTRIBUTE.CROWDNG, distance);
      }
    }
  }
}

