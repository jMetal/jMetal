//  Ranking.java
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

package org.uma.jmetal.util.solutionattribute;

import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Ranks a list of solutions according to the dominance relationship
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Ranking<S extends Solution<?>> extends SolutionAttribute<S, Integer>{
  public Ranking<S> computeRanking(List<S> solutionList) ;
  public List<S> getSubfront(int rank) ;
  public int getNumberOfSubfronts() ;
}
