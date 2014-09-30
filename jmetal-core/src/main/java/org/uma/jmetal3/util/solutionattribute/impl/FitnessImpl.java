//  Ranking.java
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

package org.uma.jmetal3.util.solutionattribute.impl;

import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.util.solutionattribute.Fitness;

/**
 */
public class FitnessImpl implements Fitness {

  @Override
  public Double getFitness(Solution solution) {
    return (Double)solution.getAttribute(ATTRIBUTE.FITNESS);
  }

  @Override
  public void setFitness(Solution solution, Double fitness) {
     solution.setAttribute(ATTRIBUTE.FITNESS, fitness);
  }
}
