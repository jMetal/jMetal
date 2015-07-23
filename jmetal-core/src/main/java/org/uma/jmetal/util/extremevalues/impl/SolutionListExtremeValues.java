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

package org.uma.jmetal.util.extremevalues.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.extremevalues.ExtremeValuesFinder;
import org.uma.jmetal.util.front.imp.ArrayFront;

import java.util.List;

/**
 * Class for finding the extreme values of a list of objects
 *
 * @author Antonio J. Nebro
 */
public class SolutionListExtremeValues implements ExtremeValuesFinder <List<Solution<?>>, List<Double>> {

  @Override public List<Double> findLowestValues(List<Solution<?>> solutionList) {
	  return new FrontExtremeValues().findLowestValues(new ArrayFront(solutionList));
  }

  @Override public List<Double> findHighestValues(List<Solution<?>> solutionList) {
	  return new FrontExtremeValues().findHighestValues(new ArrayFront(solutionList));
  }
}
