//  PolynomialBitFlipMutation.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
// 
//  Copyright (c) 2011 Antonio J. Nebro
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

package jmetal.operators.mutation;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayRealAndBinarySolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XReal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PolynomialBitFlipMutation extends Mutation {
	private static final double ETA_M_DEFAULT_ = 20.0;
	private final double eta_m_=ETA_M_DEFAULT_;
	
  private Double realMutationProbability_ = null ;
  private Double binaryMutationProbability_ = null ;
  private double distributionIndex_ = eta_m_;

  /**
   * Valid solution types to apply this operator 
   */
	private static final List VALID_TYPES = Arrays.asList(ArrayRealAndBinarySolutionType.class) ;

  /**
   * Constructor
   */
  public PolynomialBitFlipMutation(HashMap<String, Object> parameters) {
		super(parameters) ;
  	if (parameters.get("realMutationProbability") != null)
  		realMutationProbability_ = (Double) parameters.get("realMutationProbability") ;  		
  	if (parameters.get("binaryMutationProbability") != null)
  		binaryMutationProbability_ = (Double) parameters.get("binaryMutationProbability") ;  		
  	if (parameters.get("distributionIndex") != null)
  		distributionIndex_ = (Double) parameters.get("distributionIndex") ;  		
	} // PolynomialBitFlipMutation
	
	@Override
  public Object execute(Object object) throws JMException {
		Solution solution = (Solution)object;

		if (!VALID_TYPES.contains(solution.getType().getClass())) {
			Configuration.logger_.severe("PolynomialBitFlipMutation.execute: the solution " +
					"type " + solution.getType() + " is not allowed with this operator");

			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMException("Exception in " + name + ".execute()") ;
		} // if 

		doMutation(realMutationProbability_, binaryMutationProbability_,solution);
		return solution;
  } // execute

	/**
	 * doMutation method
	 * @param realProbability
	 * @param binaryProbability
	 * @param solution
	 * @throws JMException
	 */
	public void doMutation(Double realProbability, Double binaryProbability, Solution solution) throws JMException {   
		double rnd, delta1, delta2, mut_pow, deltaq;
		double y, yl, yu, val, xy;
		
		XReal x = new XReal(solution) ;
		
		Binary binaryVariable = (Binary)solution.getDecisionVariables()[1] ;
		
		// Polynomial mutation applied to the array real
		for (int var=0; var < x.size(); var++) {	
			if (PseudoRandom.randDouble() <= realProbability) {
				y      = x.getValue(var);
				yl     = x.getLowerBound(var);                
				yu     = x.getUpperBound(var);
				delta1 = (y-yl)/(yu-yl);
				delta2 = (yu-y)/(yu-yl);
				rnd = PseudoRandom.randDouble();
				mut_pow = 1.0/(eta_m_+1.0);
				if (rnd <= 0.5)
				{
					xy     = 1.0-delta1;
					val    = 2.0*rnd+(1.0-2.0*rnd)*(Math.pow(xy,(distributionIndex_+1.0)));
					deltaq =  java.lang.Math.pow(val,mut_pow) - 1.0;
				}
				else
				{
					xy = 1.0-delta2;
					val = 2.0*(1.0-rnd)+2.0*(rnd-0.5)*(java.lang.Math.pow(xy,(distributionIndex_+1.0)));
					deltaq = 1.0 - (java.lang.Math.pow(val,mut_pow));
				}
				y = y + deltaq*(yu-yl);
				if (y<yl)
					y = yl;
				if (y>yu)
					y = yu;
				x.setValue(var, y);                           
			} // if		
		} // for

		// BitFlip mutation applied to the binary part
		for (int i = 0; i < binaryVariable.getNumberOfBits(); i++)
			if (PseudoRandom.randDouble() < binaryProbability) 
				binaryVariable.bits_.flip(i) ;
	} // doMutation
} // PolynomialBitFlipMutation

