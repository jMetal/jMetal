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
package org.uma.jmetal.util.archive;

import org.uma.jmetal.solution.Solution;

import java.io.Serializable;
import java.util.List;

/**
 * Interface representing an archive of solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Archive<S extends Solution<?>> extends Serializable{
  public boolean add(S solution) ;
  public S get(int index) ;
  public List<S> getSolutionList() ;
  public int size() ;
}
