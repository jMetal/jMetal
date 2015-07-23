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

package org.uma.jmetal.operator;

import org.uma.jmetal.solution.Solution;

/**
 * Interface representing a local search operator
 *
 * Created by cbarba on 5/3/15.
 */
public interface LocalSearchOperator <Source extends Solution<?>> extends Operator<Source, Source> {
  int getEvaluations();
  int getNumberOfImprovements() ;
  int getNumberOfNonComparableSolutions() ;
}
