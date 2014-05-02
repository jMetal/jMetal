//  PointComparator.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro, Juan J. Durillo
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
//

//  CREDIT
//  This class is based on the code of the WFG group (http://www.wfg.csse.uwa.edu.au/hypervolume/)
//  Copyright (C) 2010 Lyndon While, Lucas Bradstreet.

package jmetal.qualityIndicator.fastHypervolume.wfg;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 26/07/13
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class PointComparator implements Comparator {
  boolean maximizing_ ;

  public PointComparator(boolean maximizing) {
    maximizing_ = maximizing ;
  }

  /**
   * Compares two POINT objects according to the last objectives
   * @param o1 An object that reference a Point
   * @param o2 An object that reference a Point
   * @return -1 if o1 < o1, 1 if o1 > o2 or 0 in other case.
   */
  public int compare(Object o1, Object o2) {
    //Cast to double [] o1 and o2.
    Point pointOne = (Point)o1;
    Point pointTwo = (Point)o2;

    for (int i = pointOne.getNumberOfObjectives() - 1 ; i >= 0 ; i--) {
       if (isBetter(pointOne.objectives_[i], pointTwo.objectives_[i]))
         return -1 ;
      else if (isBetter(pointTwo.objectives_[i], pointOne.objectives_[i]))
        return 1 ;
    }
    return 0 ;
  } // compare

  private boolean isBetter(double v1, double v2) {
    if (maximizing_)
      return (v1 > v2) ;
    else
      return (v2 > v1) ;
  }
}
