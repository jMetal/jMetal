//  PointComparatorTest.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013
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

package org.uma.test.qualityIndicator.fastHypervolume.wfg;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.qualityindicator.fasthypervolume.wfg.Point;
import org.uma.jmetal.qualityindicator.fasthypervolume.wfg.PointComparator;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 26/07/13
 * Time: 11:45
 */
public class PointComparatorTest {
  Point point1_ ;
  Point point2_ ;
  Comparator pointComparator_ ;

  @Before
  public void setUp() {
    point1_ = new Point(new double[]{1.0, 2.0}) ;
    point2_ = new Point(new double[]{1.5, 1.4}) ;
  }

  @Test
  public void compareTwoPointObjectsMaximizing() {
    boolean maximizing = true ;
    pointComparator_ = new PointComparator(maximizing) ;
    assertEquals("compareTwoPointObjects", -1, pointComparator_.compare(point1_, point2_));
    assertEquals("compareTwoPointObjects", 1, pointComparator_.compare(point2_, point1_));

    point1_ = new Point(new double[]{1.5, 1.4}) ;
    assertEquals("compareTwoPointObjects", 0, pointComparator_.compare(point1_, point2_));

  }

  @Test
  public void compareTwoPointObjectsMinimizing() {
    boolean maximizing = false ;
    pointComparator_ = new PointComparator(maximizing) ;
    assertEquals("compareTwoPointObjects", -1, pointComparator_.compare(point2_, point1_));
    assertEquals("compareTwoPointObjects", 1, pointComparator_.compare(point1_, point2_));

    point1_ = new Point(new double[]{1.5, 1.4}) ;
    assertEquals("compareTwoPointObjects", 0, pointComparator_.compare(point1_, point2_));
  }
}
