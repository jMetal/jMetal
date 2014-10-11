//  FrontTest.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
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
import org.uma.jmetal45.qualityindicator.fasthypervolume.wfg.Front;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 26/07/13
 * Time: 12:05
 */
public class FrontTest {
  Front front_ ;

  @Before
  public void setUp() {
    List<double[]> list = new ArrayList<double[]>();
    list.add(new double[]{1, 2, 3}) ;
    list.add(new double[]{2, 2, 6}) ;
    list.add(new double[]{3, 1, 6}) ;

    front_ = new Front(list.size(), 2, list) ;
  }

  @Test
  public void sortMaximizingTest() {
    double epsilon = 0.0000000000001 ;
    //front_.printFront();
    front_.sort();
    //front_.printFront();
    assertEquals(6, front_.getPoint(0).getObjectives()[2], epsilon) ;
    assertEquals(2, front_.getPoint(0).getObjectives()[1], epsilon) ;
    assertEquals(2, front_.getPoint(0).getObjectives()[0], epsilon) ;
  }


  @Test
  public void sortMinimizingTest() {
    front_.setToMinimize();
    double epsilon = 0.0000000000001 ;
    //front_.printFront();
    front_.sort();
    //front_.printFront();
    assertEquals(3, front_.getPoint(0).getObjectives()[2], epsilon) ;
    assertEquals(2, front_.getPoint(0).getObjectives()[1], epsilon) ;
    assertEquals(1, front_.getPoint(0).getObjectives()[0], epsilon) ;
  }
}
