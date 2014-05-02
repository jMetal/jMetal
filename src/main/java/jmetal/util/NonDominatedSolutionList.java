//  NonDominatedSolutionList.java
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

package jmetal.util;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.SolutionComparator;

import java.util.Comparator;
import java.util.Iterator;

/** 
 * This class implements an unbound list of non-dominated solutions
 */
public class NonDominatedSolutionList extends SolutionSet{

	/**
	 * Stores a <code>Comparator</code> for dominance checking
	 */
	private Comparator dominance_ = new DominanceComparator(); 

	/**
	 * Stores a <code>Comparator</code> for checking if two solutions are equal
	 */
	private static final Comparator equal_ = new SolutionComparator();     

	/** 
	 * Constructor.
	 * The objects of this class are lists of non-dominated solutions according to
	 * a Pareto dominance comparator. 
	 */
	public NonDominatedSolutionList() {
		super();
	} // NonDominatedList

	/**
	 * Constructor.
	 * This constructor creates a list of non-dominated individuals using a
	 * comparator object.
	 * @param dominance The comparator for dominance checking.
	 */
	public NonDominatedSolutionList(Comparator dominance) {
		super();
		dominance_ = dominance;
	} // NonDominatedList

	/** Inserts a solution in the list
	 * @param solution The solution to be inserted.
	 * @return true if the operation success, and false if the solution is 
	 * dominated or if an identical individual exists.
	 * The decision variables can be null if the solution is read from a file; in
	 * that case, the domination tests are omitted
	 */
	public boolean add(Solution solution){
		if (solutionsList_.size() == 0) {
			solutionsList_.add(solution);    
			return true ;
		}
		else {
			Iterator<Solution> iterator = solutionsList_.iterator();

			//if (solution.getDecisionVariables() != null) {
			while (iterator.hasNext()){
				Solution listIndividual = iterator.next();
				int flag = dominance_.compare(solution,listIndividual);

				if (flag == -1) {  // A solution in the list is dominated by the new one
					iterator.remove();
				} else if (flag == 0) { // Non-dominated solutions
					//flag = equal_.compare(solution,listIndividual);
					//if (flag == 0) {
					//	return false;   // The new solution is in the list  
					//}
				} else if (flag == 1) { // The new solution is dominated
					return false;
				}
			} // while 
			//} // if

			//At this point, the solution is inserted into the list
			solutionsList_.add(solution);                

			return true;        
		}
	} // add                   
} // NonDominatedList
