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

package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.impl.SpatialSpreadDeviation;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares two solutions according to the Spatial Spread Deviation attribute. T
 *
 * @author Alejandro Santiago <aurelio.santiago@upalt.edu.mx>
 */
public class SpatialSpreadDeviationComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
  private final SpatialSpreadDeviation<S> NewcrowdingDistance = new SpatialSpreadDeviation<S>() ;

  /**
   * Compare two solutions.
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if solution1 is has greater, equal, or less distance value than solution2,
   * respectively.
   */
  @Override
  public int compare(S solution1, S solution2) {
    int result =0;
    
    
      double distance1 = Double.MIN_VALUE ;
      double distance2 = Double.MIN_VALUE ;

      distance1 = (double) NewcrowdingDistance.getAttribute(solution1);
      distance2 = (double) NewcrowdingDistance.getAttribute(solution2);
      /*
      if (NewcrowdingDistance.getAttribute(solution1) != null) {
        distance1 = (double) NewcrowdingDistance.getAttribute(solution1);
        System.out.println("Distancia 1"+distance1);
      }

      if (NewcrowdingDistance.getAttribute(solution2) != null) {
        distance2 = (double) NewcrowdingDistance.getAttribute(solution2);
        System.out.println("Distancia 2"+distance2);
      }*/
      
      //System.out.println("Valores de distancias "+distance1+" "+distance2);
      
      if(distance1 > distance2)
      {
          return -1;
      }
      if(distance2 > distance1)
      {
          return 1;
      } 
      /*
      if (distance1 > distance2) {
        result = -1;
      } else  if (distance1 < distance2) {
        result = 1;
      } else {
        result = 0;
      }
      */
      return 0;
    
    }
    
    //return result ;
  }

