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

package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;

import java.util.List;

/**
 * Class for calculating the Euclidean distance between a {@link Solution} object a list of {@link Solution}
 * objects in objective space.
 *
 * @author <antonio@lcc.uma.es>
 */
public class EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace<S extends Solution<Double>, L extends List<S>>
    implements Distance<S, L> {

  private EuclideanDistanceBetweenSolutionsInObjectiveSpace<S> distance ;

  public EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace() {
    distance = new EuclideanDistanceBetweenSolutionsInObjectiveSpace<S>() ;
  }

  @Override
  public double getDistance(S solution, L solutionList) {
    double bestDistance = Double.MAX_VALUE;

    for (int i = 0; i < solutionList.size();i++){
      double aux = distance.getDistance(solution, solutionList.get(i));
      if (aux < bestDistance)
        bestDistance = aux;
    }

    return bestDistance;
  }
}
