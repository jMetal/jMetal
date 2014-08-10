//  IntSolutionTypeTemplate.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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

package org.uma.jmetal.encoding.solutiontype;

import org.uma.jmetal.core.Solution;

/** Template for solution types including an integer encoding */
public interface GenericIntSolutionType {
  public int getIntValue(Solution solution, int index) ;
  public void setIntValue(Solution solution, int index, int value) ;
  public int getNumberOfIntVariables(Solution solution_);
  public int getIntUpperBound(Solution solution, int index) ;
  public int getIntLowerBound(Solution solution, int index) ;
}
