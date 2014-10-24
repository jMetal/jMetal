//  BestSolutionSelection.java
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

package org.uma.jmetal.operator.selection;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.JMetalException;

import java.util.Comparator;

/**
 * This class implements a selection operator used for selecting the best
 * solution in a SolutionSet according to a given comparator
 */
public class BestSolutionSelection extends Selection {
  private static final long serialVersionUID = 7515153213699830920L;

  private Comparator<Solution> comparator;
  
  /** Constructor */
  private BestSolutionSelection(Builder builder) {
  	this.comparator = builder.comparator ;
  }
  
  /** Builder class */
  public static class Builder {
    private Comparator<Solution> comparator;
    
    public Builder(Comparator<Solution> comparator) {
	  	this.comparator = comparator ;
	  }
	  
	  public BestSolutionSelection build() {
	  	 return new BestSolutionSelection(this) ;
	  }
  }
  
  /* Getter */
  public Comparator getComparator() {
  	return comparator ;
  }

  /** Execute() method */
  public Object execute(Object object) {
  	if (null == object) {
  		throw new JMetalException("Null parameter") ;
  	} else if (!(object instanceof SolutionSet)) {
  		throw new JMetalException("Invalid parameter class") ;
  	} else if (((SolutionSet)object).size() == 0) {
  		throw new JMetalException("Solution set size is zero") ;
  	}
  	
  	SolutionSet solutionSet = (SolutionSet) object;
    int bestSolution = 0 ;

    for (int i = 1; i < solutionSet.size(); i++) {
      if (comparator.compare(solutionSet.get(i), solutionSet.get(bestSolution)) < 0) {
        bestSolution = i;
      }
    }

    return bestSolution;
  }
}
