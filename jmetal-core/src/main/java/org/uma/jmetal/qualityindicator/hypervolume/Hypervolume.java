//  Hypervolume.java
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

package org.uma.jmetal.qualityindicator.hypervolume;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.front.Front;

/**
 * This class implements the hypervolume indicator. The code is the a Java version
 * of the original metric implementation by Eckart Zitzler.
 * Reference: E. Zitzler and L. Thiele
 * Multiobjective Evolutionary Algorithms: A Comparative Case Study and the Strength Pareto Approach,
 * IEEE Transactions on Evolutionary Computation, vol. 3, no. 4,
 * pp. 257-271, 1999.
 */
public interface Hypervolume extends QualityIndicator {
  double[] computeHypervolumeContribution(Front front) ;
}
