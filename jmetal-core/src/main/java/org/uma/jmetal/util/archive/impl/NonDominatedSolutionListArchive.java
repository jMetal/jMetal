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

package org.uma.jmetal.util.archive.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.EqualSolutionsComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This class implements an archive containing non-dominated solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class NonDominatedSolutionListArchive<S extends Solution<?>> implements Archive<S> {
  private List<S> solutionList;
  private Comparator<S> dominanceComparator;
  private Comparator<S> equalSolutions = new EqualSolutionsComparator<S>();

  /** Constructor */
  public NonDominatedSolutionListArchive() {
    this(new DominanceComparator<S>()) ;
  }

  /** Constructor */
  public NonDominatedSolutionListArchive(DominanceComparator<S> comparator) {
    dominanceComparator = comparator ;
   
    solutionList = new ArrayList<>() ;
  }

  /**
   * Inserts a solution in the list
   *
   * @param solution The solution to be inserted.
   * @return true if the operation success, and false if the solution is
   * dominated or if an identical individual exists.
   * The decision variables can be null if the solution is read from a file; in
   * that case, the domination tests are omitted
   */
  public boolean add(S solution) {
    boolean solutionInserted = false ;
    if (solutionList.size() == 0) {
      solutionList.add(solution) ;
      solutionInserted = true ;
    } else {
      Iterator<S> iterator = solutionList.iterator();
      boolean isDominated = false;
      
      boolean isContained = false;
      while (((!isDominated) && (!isContained)) && (iterator.hasNext())) {
        S listIndividual = iterator.next();
        int flag = dominanceComparator.compare(solution, listIndividual);
        if (flag == -1) {
          iterator.remove();
        }  else if (flag == 1) {
          isDominated = true; // dominated by one in the list
        } else if (flag == 0) {
        	int equalflag = equalSolutions.compare(solution, listIndividual);
        	if (equalflag==0) // solutions are equals
        		isContained = true;
        }
        	
      }
      
      if (!isDominated && !isContained) {
    	  solutionList.add(solution);
    	  solutionInserted = true;
      }
      
      return solutionInserted;
    }

    return solutionInserted ;
  }

  @Override
  public List<S> getSolutionList() {
    return solutionList;
  }

  @Override public int size() {
    return solutionList.size();
  }

  @Override public S get(int index) {
    return solutionList.get(index);
  }
}
