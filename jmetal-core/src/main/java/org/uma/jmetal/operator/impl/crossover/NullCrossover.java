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

package org.uma.jmetal.operator.impl.crossover;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines a null crossover operator: the parent solutions are returned without any
 * change. It can be useful when configuring a genetic algorithm and we want to use only mutation.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings({ "unchecked", "serial" })
public class NullCrossover<S extends Solution<?>>
    implements CrossoverOperator<S> {

  /** Execute() method */
  @Override public List<S> execute(List<S> source) {
    if (null == source) {
      throw new JMetalException("Null parameter") ;
    } else if (source.size() != 2) {
      throw new JMetalException("There must be two parents instead of " + source.size()) ;
    }

    List<S> list = new ArrayList<>() ;
    list.add((S) source.get(0).copy()) ;
    list.add((S) source.get(1).copy()) ;

    return list ;
  }

  /**
   * Two parents are required to apply this operator.
   * @return
   */
  public int getNumberOfParents() {
    return 2 ;
  }
}
