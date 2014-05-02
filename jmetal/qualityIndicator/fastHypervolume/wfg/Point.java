//  Point.java
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

import jmetal.core.Solution;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 25/07/13
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
public class Point {
  public double[] objectives_ ;
  //AvlTree<Integer> node_ ;

  public Point(int dimension) {
    objectives_ = new double[dimension] ;

    for (int i = 0 ; i < dimension ; i++) {
      objectives_[i] = 0.0 ;
    }
  }

  public Point (Solution solution) {
    int dimension = solution.getNumberOfObjectives() ;
    objectives_ = new double[dimension] ;

    for (int i = 0 ; i < dimension ; i++) {
      objectives_[i] = solution.getObjective(i) ;
    }
  }

  public Point(double [] points) {
    objectives_ = new double[points.length] ;
    System.arraycopy(points, 0, objectives_, 0, points.length) ;
  }

  public int getNumberOfObjectives() {
    return objectives_.length ;
  }

  public double[] getObjectives() {
    return objectives_ ;
  }

  public String toString() {
    String result = "" ;
    for (int i = 0; i < objectives_.length; i++)
      result += objectives_[i] + " " ;

    return result ;
  }
}
