//  DifferentialEvolutionBitFlipMutation.java
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

package org.uma.jmetal.operator.mutation;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.encoding.variable.Binary;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.random.PseudoRandom;

/**
* This class implements a mutation operator to be applied to 
* ArrayRealAndBinarySolutionType objects. The mutation combines DE and bitflip mutation
*/
public class DifferentialEvolutionBitFlipMutation extends Mutation {
	public double cr = 0.5;
	public double f = 0.5;

  /**
   * ARRAY_REAL_AND_BINARY_SOLUTION represents class jmetal.base.solutionType.ArrayRealAndBinarySolutionType
   */
  private static Class ARRAY_REAL_AND_BINARY_SOLUTION ; 

  /**
   * Constructor
   */
  public DifferentialEvolutionBitFlipMutation() {
    try {
    	ARRAY_REAL_AND_BINARY_SOLUTION = Class.forName("jmetal.problems.bci5.ArrayRealAndBinarySolutionType") ;
    } catch (ClassNotFoundException e) {
	    throw new JMetalException(e) ;
    } 
  } 
	
	@Override
  public Object execute(Object object) {
		Object[] parameters = (Object[])object ;
		Solution current   = (Solution) parameters[0];
		Solution [] parent = (Solution [])parameters[1];

		Solution child ;
		
		if (((parent[0].getType().getClass() != ARRAY_REAL_AND_BINARY_SOLUTION) &&
				(parent[1].getType().getClass() != ARRAY_REAL_AND_BINARY_SOLUTION) &&
				(parent[2].getType().getClass() != ARRAY_REAL_AND_BINARY_SOLUTION)) && 
				((parent[0].getType().getClass() != ARRAY_REAL_AND_BINARY_SOLUTION) &&
				(parent[1].getType().getClass() != ARRAY_REAL_AND_BINARY_SOLUTION)&&
				(parent[2].getType().getClass() != ARRAY_REAL_AND_BINARY_SOLUTION))) {

			JMetalLogger.logger.severe("DifferentialEvolutionBitFlipMutation.execute: " +
					" the solutions " +
					"are not of the right type. The type should be 'Real' or 'ArrayReal', but " +
					parent[0].getType() + " and " + 
					parent[1].getType() + " and " + 
					parent[2].getType() + " are obtained");

			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMetalException("Exception in " + name + ".execute()") ;
		}

		Double CR = (Double)getParameter("CR");       
		if (CR == null){
      cr = CR ;
		}

		Double F = (Double)getParameter("F");       
		if (F == null){
      f = F;
    }
		
		Double binaryProbability = (Double)getParameter("binaryProbability");       
		if (binaryProbability == null){
			JMetalLogger.logger.severe("PolynomialBitFlipMutation.execute: probability of the binary component" +
			"not specified");
			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMetalException("Exception in " + name + ".execute()") ;  
		}

		// DE part
		int jrand ;

		child = new Solution(current) ;
		
		XReal xParent0 = new XReal(parent[0]) ;
		XReal xParent1 = new XReal(parent[1]) ;
		XReal xParent2 = new XReal(parent[2]) ;
		XReal xCurrent = new XReal(current) ;
		XReal xChild   = new XReal(child) ;

		int numberOfVariables = xParent0.size() ;
		jrand = (int)(PseudoRandom.randInt(0, numberOfVariables - 1)) ;

		String DE_Variant_= "rand/1/bin" ;
		
		// STEP 4. Checking the DE variant
		if ((DE_Variant_.compareTo("rand/1/bin") == 0) || 
				(DE_Variant_.compareTo("best/1/bin") == 0)) { 
			for (int j=0; j < numberOfVariables; j++) {
				if (PseudoRandom.randDouble(0, 1) < cr || j == jrand) {
					double value ;
					value = xParent2.getValue(j)  + f * (xParent0.getValue(j) -
							                                  xParent1.getValue(j)) ;
					if (value < xChild.getLowerBound(j))
						value =  xChild.getLowerBound(j) ;
					if (value > xChild.getUpperBound(j))
						value = xChild.getUpperBound(j) ;

					xChild.setValue(j, value) ;
				}
				else {
					double value ;
					value = xCurrent.getValue(j);
					xChild.setValue(j, value) ;
				} 
			} 
		} 
	
		// Binary part: first, the bits are selected randomly from the parents 
		Binary binaryChild = (Binary)child.getDecisionVariables()[1] ;
		Binary [] binaryParent = new Binary[3] ;
		
		binaryParent[0] = (Binary)parent[0].getDecisionVariables()[1] ;
		binaryParent[1] = (Binary)parent[1].getDecisionVariables()[1] ;
		binaryParent[2] = (Binary)parent[2].getDecisionVariables()[1] ;

		for (int i = 0; i < binaryChild.getNumberOfBits(); i++) {
			int chosen = PseudoRandom.randInt(0, 2) ;
			  binaryChild.getBits().set(i, binaryParent[chosen].getBits().get(i)) ;
		}
		
		// Binary part: second, bitflip mutation is applied 
		int countingOnes = 0 ;
		for (int i = 0; i < binaryChild.getNumberOfBits(); i++) {
			if (PseudoRandom.randDouble() < binaryProbability) 
				binaryChild.getBits().flip(i) ;
			if (binaryChild.getBits().get(i) == true)
				countingOnes ++ ;
		} 
		// if the number of bits reach to zero, generate a '1's string
	  if (countingOnes == 0) {		
	  	System.out.println("Reached to 0 attributes") ;
			for (int i = 0; i < binaryChild.getNumberOfBits(); i++) {
				binaryChild.getBits().set(i, true) ;
			} 
	  } 
			
		return child ;
	} 
}
