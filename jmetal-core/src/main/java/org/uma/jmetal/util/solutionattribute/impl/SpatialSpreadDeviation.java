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

package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class implements the Spatial Spread Deviation density estimator
 *
 * @author Alejandro Santiago <aurelio.santiago@upalt.edu.mx>
 */
public class SpatialSpreadDeviation<S extends Solution<?>>
        extends GenericSolutionAttribute<S, Double>
        implements DensityEstimator<S> {

  /**
   * Assigns crowding distances to all solutions in a <code>SolutionSet</code>.
   *
   * @param solutionList The <code>SolutionSet</code>.
   * @throws org.uma.jmetal.util.JMetalException
   */
  @Override
  public void computeDensityEstimator(List<S> solutionList) {
    int size = solutionList.size();

    if (size <= solutionList.get(0).getNumberOfObjectives()) {
      for (int x = 0; x < size; x++) {
        solutionList.get(x).setAttribute(getAttributeID(), Double.POSITIVE_INFINITY);
      }
      return;
    }

    // Use a new SolutionSet to avoid altering the original solutionSet
    List<S> front = new ArrayList<>(size);
    for (S solution : solutionList) {
      front.add(solution);
    }

    for (int i = 0; i < size; i++) {
      front.get(i).setAttribute(getAttributeID(), 0.0);
    }

    int numberOfObjectives = solutionList.get(0).getNumberOfObjectives();

    double objetiveMaxn[] = new double[numberOfObjectives];
    double objetiveMinn[] = new double[numberOfObjectives];
    double distan;

    for (int i = 0; i < numberOfObjectives; i++) {
      // Sort the population by Obj n
      Collections.sort(front, new ObjectiveComparator<S>(i));
      objetiveMinn[i] = front.get(0).getObjective(i);
      objetiveMaxn[i] = front.get(front.size() - 1).getObjective(i);

      // Set de crowding distance Los extremos si infinitos
      front.get(0).setAttribute(getAttributeID(), Double.POSITIVE_INFINITY);
      front.get(size - 1).setAttribute(getAttributeID(), Double.POSITIVE_INFINITY);
    }
    double[][] distance =
        SolutionListUtils.distanceMatrix_normalize(front, objetiveMaxn, objetiveMinn);

    double dminn, dmaxx;
    dminn = Double.MAX_VALUE;
    dmaxx = 0.0;
    for (int i = 0; i < distance.length; i++)
      for (int j = 0; j < distance.length; j++) {
        if (i != j) {
          if (distance[i][j] < dminn) {
            dminn = distance[i][j];
          }
          if (distance[i][j] > dmaxx) {
            dmaxx = distance[i][j];
          }
        }
      }

    for (int i = 0; i < front.size(); i++) {
      double temp = 0.0;
      for (int j = 0; j < distance.length; j++) {
        if (i != j) {
          temp += Math.pow(distance[i][j] - (dmaxx - dminn), 2);
        }
      }
      temp /= distance.length - 1;
      temp = Math.sqrt(temp);
      temp *= -1;
      temp += (double) front.get(i).getAttribute(getAttributeID());
      // if((double) front.get(i).getAttribute(getAttributeID())!=Double.POSITIVE_INFINITY)
      front.get(i).setAttribute(getAttributeID(), temp);
    }

    // int k = numberOfObjectives la solucion 0 es ella misma
    for (int i = 0; i < distance.length; i++) {
      Arrays.sort(distance[i]);
      double kDistance = 0.0;
      for (int k = numberOfObjectives; k > 0; k--) {
        // kDistance += (dmaxx-dminn) / (distance[i][k]);//me gusta mas este
        // kDistance += (dmaxx-dminn) / (distance[i][k]+dminn);//original
        kDistance += (dmaxx - dminn) / distance[i][k];
      }
      double temp = (double) front.get(i).getAttribute(getAttributeID());
      // if(temp!=Double.POSITIVE_INFINITY)
      // kDistance=kDistance/numberOfObjectives-1;
      temp -= kDistance;
      front.get(i).setAttribute(getAttributeID(), temp);
    }
  }

  public Object getAttributeID() {
    return this.getClass();
  }
}
