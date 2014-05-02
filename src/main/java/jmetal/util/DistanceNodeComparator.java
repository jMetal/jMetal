//  DistanceNodeComparator.java
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

package jmetal.util;

import java.util.Comparator;

/**
 * This class implements a <code>Comparator</code> to compare instances of
 * <code>DistanceNode</code>.
 */
public class DistanceNodeComparator implements Comparator{
    
  /**
   * Compares two <code>DistanceNode</code>.
   * @param o1 Object representing a DistanceNode
   * @param o2 Object representing a DistanceNode
   * @return -1 if the distance of o1 is smaller than the distance of o2,
   *          0 if the distance of both are equals, and
   *          1 if the distance of o1 is bigger than the distance of o2
   */
  public int compare(Object o1, Object o2){
    DistanceNode node1 = (DistanceNode) o1;
    DistanceNode node2 = (DistanceNode) o2;
        
    double distance1,distance2;
    distance1 = node1.getDistance();
    distance2 = node2.getDistance();
        
    if (distance1 < distance2)
      return -1;
    else if (distance1 > distance2)
      return 1;
    else
      return 0;
  } // DistanceNodeComparator 
} // DistanceNodeComparator
