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

import java.io.Serializable;

/**
 * Interface representing an operator
 * @author Antonio J. Nebro
 * @version 0.1
 * @param <S> Source
 * @param <R> Result
 */
public interface Operator<S, R> extends Serializable {
  /**
   * @param source the data to process
   */
  public R execute(S source) ;
}
