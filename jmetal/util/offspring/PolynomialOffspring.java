/*
 * PolynomialOffspring.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class returns a solution after applying SBX and Polynomial mutation
 */
package jmetal.util.offspring;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.core.*;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;

public class PolynomialOffspring extends Offspring {

	Operator mutation_;
	Operator selection_;

	public PolynomialOffspring(double mutationProbability,
		                         double distributionIndexForMutation
		                         ) throws JMException {     
		HashMap  parameters ; // Operator parameters
		parameters = new HashMap() ;
		parameters.put("probability", mutationProbability) ;
		parameters.put("distributionIndex", distributionIndexForMutation) ;
		mutation_ = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

		selection_ = SelectionFactory.getSelectionOperator("BinaryTournament", null);
		id_ = "Polynomial";
	}

	public Solution getOffspring(Solution solution) {
		Solution res = new Solution(solution);
		try {
			mutation_.execute(res);
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
} // PolynomialOffspring


