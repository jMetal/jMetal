//  TwoPointsCrossover.java
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

package jmetal.operators.crossover;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.variable.Permutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class allows to apply a two points crossover operator using two parent
 * solutions. 
 * NOTE: the type of the solutions must be Permutation..
 */
public class TwoPointsCrossover extends Crossover {

  /**
   * Valid solution types to apply this operator 
   */
  private static final List VALID_TYPES = Arrays.asList(PermutationSolutionType.class) ;

  private Double crossoverProbability_ = null;

	/**
	 * Constructor
	 * Creates a new intance of the two point crossover operator
	 */
	public TwoPointsCrossover(HashMap<String, Object> parameters) {
		super(parameters) ;
		
  	if (parameters.get("probability") != null)
  		crossoverProbability_ = (Double) parameters.get("probability") ;  		
	} // TwoPointsCrossover


	/**
	 * Constructor
	 * @param A properties containing the Operator parameters
	 * Creates a new intance of the two point crossover operator
	 */
	//public TwoPointsCrossover(Properties properties) {
	//	this();
	//}


	/**
	 * Perform the crossover operation
	 * @param probability Crossover probability
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @return Two offspring solutions
	 * @throws JMException 
	 */
	public Solution[] doCrossover(double   probability, 
			Solution parent1, 
			Solution parent2) throws JMException {

		Solution [] offspring = new Solution[2];

		offspring[0] = new Solution(parent1);
		offspring[1] = new Solution(parent2);

		if (parent1.getType().getClass() == PermutationSolutionType.class) {
				if (PseudoRandom.randDouble() < probability) {
					int crosspoint1        ;
					int crosspoint2        ;
					int permutationLength  ;
					int parent1Vector[]    ;
					int parent2Vector[]    ;
					int offspring1Vector[] ;
					int offspring2Vector[] ;

					permutationLength = ((Permutation)parent1.getDecisionVariables()[0]).getLength() ;
					parent1Vector     = ((Permutation)parent1.getDecisionVariables()[0]).vector_ ;
					parent2Vector    = ((Permutation)parent2.getDecisionVariables()[0]).vector_ ;    
					offspring1Vector = ((Permutation)offspring[0].getDecisionVariables()[0]).vector_ ;
					offspring2Vector = ((Permutation)offspring[1].getDecisionVariables()[0]).vector_ ;

					// STEP 1: Get two cutting points
					crosspoint1 = PseudoRandom.randInt(0,permutationLength-1) ;
					crosspoint2 = PseudoRandom.randInt(0,permutationLength-1) ;

					while (crosspoint2 == crosspoint1)  
						crosspoint2 = PseudoRandom.randInt(0,permutationLength-1) ;

					if (crosspoint1 > crosspoint2) {
						int swap ;
						swap        = crosspoint1 ;
						crosspoint1 = crosspoint2 ;
						crosspoint2 = swap          ;
					} // if

					// STEP 2: Obtain the first child
					int m = 0;
					for(int j = 0; j < permutationLength; j++) {
						boolean exist = false;
						int temp = parent2Vector[j];
						for(int k = crosspoint1; k <= crosspoint2; k++) {
							if (temp == offspring1Vector[k]) {
								exist = true;
								break;
							} // if
						} // for
						if (!exist) {
							if (m == crosspoint1)
								m = crosspoint2 + 1;
							offspring1Vector[m++] = temp;
						} // if
					} // for

					// STEP 3: Obtain the second child
					m = 0;
					for(int j = 0; j < permutationLength; j++) {
						boolean exist = false;
						int temp = parent1Vector[j];
						for(int k = crosspoint1; k <= crosspoint2; k++) {
							if (temp == offspring2Vector[k]) {
								exist = true;
								break;
							} // if
						} // for
						if(!exist) {
							if (m == crosspoint1)
								m = crosspoint2 + 1;
							offspring2Vector[m++] = temp;
						} // if
					} // for
				} // if 
			} // if
			else
			{
				Configuration.logger_.severe("TwoPointsCrossover.doCrossover: invalid " +
						"type" + 
						parent1.getDecisionVariables()[0].getVariableType());
				Class cls = java.lang.String.class;
				String name = cls.getName(); 
				throw new JMException("Exception in " + name + ".doCrossover()") ; 
			}

		return offspring;                                                                                      
	} // makeCrossover

	/**
	 * Executes the operation
	 * @param object An object containing an array of two solutions 
	 * @return An object containing an array with the offSprings
	 * @throws JMException 
	 */
	public Object execute(Object object) throws JMException {
		Solution [] parents = (Solution [])object;
		Double crossoverProbability ;

    if (!(VALID_TYPES.contains(parents[0].getType().getClass())  &&
        VALID_TYPES.contains(parents[1].getType().getClass())) ) {

			Configuration.logger_.severe("TwoPointsCrossover.execute: the solutions " +
					"are not of the right type. The type should be 'Permutation', but " +
					parents[0].getType() + " and " + 
					parents[1].getType() + " are obtained");
		} // if 

		crossoverProbability = (Double)getParameter("probability");

		if (parents.length < 2)
		{
			Configuration.logger_.severe("TwoPointsCrossover.execute: operator needs two " +
			"parents");
			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMException("Exception in " + name + ".execute()") ;      
		}

		Solution [] offspring = doCrossover(crossoverProbability_,
				parents[0],
				parents[1]);

		return offspring; 
	} // execute

} // TwoPointsCrossover
