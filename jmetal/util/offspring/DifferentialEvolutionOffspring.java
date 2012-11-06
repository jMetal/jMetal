/**
 * DifferentialEvolutionOffspring.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class returns a solution after applying DE
 */

package jmetal.util.offspring;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.core.*;
import jmetal.operators.crossover.DifferentialEvolutionCrossover;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

public class DifferentialEvolutionOffspring extends Offspring {
	double CR_ ;
	double F_  ;

	Operator crossover_ ;
	Operator selection_ ;
	Operator mutation_;

	public void DifferentialEvolutionOffspring() {

	}
	/**
	 * Constructor
	 * @param CR
	 * @param F
	 */
	public DifferentialEvolutionOffspring(double CR, double F)  {
		HashMap parameters = null ;
		CR_ = CR ;
		F_  = F  ;
		try {
			// Crossover operator
			parameters = new HashMap() ;
			parameters.put("CR", CR_) ;
			parameters.put("F", F_) ;      
			crossover_ = new DifferentialEvolutionCrossover(parameters) ;

			// Selecion operator
			parameters = null ;
			selection_ = SelectionFactory.getSelectionOperator("DifferentialEvolutionSelection", parameters);
		} catch (JMException ex) {
			Logger.getLogger(DifferentialEvolutionOffspring.class.getName()).log(Level.SEVERE, null, ex);
		}
		id_ = "DE" ;
	}

	public Solution getOffspring(SolutionSet solutionSet, int index) {
		Solution[] parents = new Solution[3] ;
		Solution offSpring = null ;

		try {
			int r1, r2 ;
			do {
				r1 = (int)(PseudoRandom.randInt(0,solutionSet.size()-1));
			} while( r1==index );
			do {
				r2 = (int)(PseudoRandom.randInt(0,solutionSet.size()-1));
			} while( r2==index || r2==r1);

			parents[0] = solutionSet.get(r1) ;
			parents[1] = solutionSet.get(r2) ;
			parents[2] = solutionSet.get(index) ;

			offSpring = (Solution) crossover_.execute(new Object[]{solutionSet.get(index), parents});
		} catch (JMException ex) {
			Logger.getLogger(DifferentialEvolutionOffspring.class.getName()).log(Level.SEVERE, null, ex);
		}

		//Create a new solution, using DE
		return offSpring ;
	} // getOffpring
	
} // DifferentialEvolutionOffspring

