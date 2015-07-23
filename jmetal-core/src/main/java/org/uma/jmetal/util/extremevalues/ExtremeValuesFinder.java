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

package org.uma.jmetal.util.extremevalues;

/**
 * Interface representing classes aimed at finding the extreme values of Source objects (e.g., lists)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 *
 * @param <Source>
 * @param <Result>
 */
public interface ExtremeValuesFinder<Source, Result> {
  Result findLowestValues(Source source) ;
  Result findHighestValues(Source source) ;
}
