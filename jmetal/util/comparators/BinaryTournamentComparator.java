//  BinaryTournamentComparator.java
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

package jmetal.util.comparators;

import java.util.*;

import jmetal.core.Solution;

/**
 * This class implements a <code>Comparator</code> for <code>Solution</code>
 */
public class BinaryTournamentComparator implements Comparator{
  
  /**
   * stores a dominance comparator
   */
  private static final Comparator dominance_ = new DominanceComparator();
  
  /**
   * Compares two solutions.
   * A <code>Solution</code> a is less than b for this <code>Comparator</code>.
   * if the crowding distance of a if greater than the crowding distance of b.
   * @param o1 Object representing a <code>Solution</code>.
   * @param o2 Object representing a <code>Solution</code>.
   * @return -1, or 0, or 1 if o1 is less than, equals, or greater than o2,
   * respectively.
   */
  public int compare(Object o1, Object o2) {
    int flag = dominance_.compare(o1,o2);
    if (flag!=0) {
      return flag;
    }
    
    double crowding1, crowding2;
    crowding1 = ((Solution)o1).getCrowdingDistance();
    crowding2 = ((Solution)o2).getCrowdingDistance();
    
    if (crowding1 > crowding2) {
      return -1;
    } else if (crowding2 > crowding1) {
      return 1;
    } else {
      return 0;
    }
  } // compare
} // BinaryTournamentComparator.
