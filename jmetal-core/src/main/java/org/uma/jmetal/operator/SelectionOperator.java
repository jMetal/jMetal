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

/**
 * Interface representing selection operators
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @param <Source> Class of the source object (typically, a list of solutions)
 * @param <Result> Class of the result of applying the operator
 */
public interface SelectionOperator<Source, Result> extends Operator<Source,Result> {
}
