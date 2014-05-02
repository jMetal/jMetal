//  BLXAlphaCrossover.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2012 Antonio J. Nebro
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
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XReal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class allows to apply a SBX crossover operator using two parent
 * solutions.
 */
public class BLXAlphaCrossover extends Crossover {
	/**
	 * EPS defines the minimum difference allowed between real values
	 */
	private static final double DEFAULT_ALPHA = 0.5;

	private double alpha_ = DEFAULT_ALPHA ;
	private Double crossoverProbability_ = null;

	/**
	 * Valid solution types to apply this operator 
	 */
	private static final List VALID_TYPES = Arrays.asList(RealSolutionType.class,
			ArrayRealSolutionType.class) ;

	/** 
	 * Constructor
	 * Create a new SBX crossover operator whit a default
	 * index given by <code>DEFAULT_INDEX_CROSSOVER</code>
	 */
	public BLXAlphaCrossover(HashMap<String, Object> parameters) {
		super (parameters) ;

		if (parameters.get("probability") != null)
			crossoverProbability_ = (Double) parameters.get("probability") ;  		
		if (parameters.get("alpha") != null)
			alpha_  = (Double) parameters.get("alpha") ;  		
	} // SBXCrossover

	/**
	 * Perform the crossover operation. 
	 * @param probability Crossover probability
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @return An array containing the two offsprings
	 */
	public Solution[] doCrossover(double probability, 
			Solution parent1, 
			Solution parent2) throws JMException {

		Solution [] offSpring = new Solution[2];

		offSpring[0] = new Solution(parent1);
		offSpring[1] = new Solution(parent2);

		int i;
		double random;
		double valueY1 ;
		double valueY2 ;
		double valueX1 ;
		double valueX2 ;
		double upperValue ;
		double lowerValue ;

		XReal x1 = new XReal(parent1) ;		
		XReal x2 = new XReal(parent2) ;		
		XReal offs1 = new XReal(offSpring[0]) ;
		XReal offs2 = new XReal(offSpring[1]) ;

		int numberOfVariables = x1.getNumberOfDecisionVariables() ;

		if (PseudoRandom.randDouble() <= probability){
			for (i=0; i<numberOfVariables; i++){
				upperValue = x1.getUpperBound(i);
				lowerValue = x1.getLowerBound(i);
				valueX1 = x1.getValue(i) ;
				valueX2 = x2.getValue(i) ;

				double max ;
				double min ;
				double range ;

        if (valueX2 > valueX1) {
          max = valueX2 ;
          min = valueX1 ;
        } // if
        else {
          max = valueX1 ;
          min = valueX2 ;
        } // else

        range = max - min ;
        // Ranges of the new alleles ;
        double minRange ;
        double maxRange ;

        minRange = min - range*alpha_;
        maxRange = max + range*alpha_;

        random = PseudoRandom.randDouble();
        valueY1 =  minRange + random * (maxRange - minRange) ;

        random = PseudoRandom.randDouble() ;
        valueY2 =  minRange + random * (maxRange - minRange) ;

        if (valueY1 < lowerValue)
        	offs1.setValue(i, lowerValue) ;
        else if (valueY1 > upperValue)
        	offs1.setValue(i, upperValue);
        else
        	offs1.setValue(i, valueY1);

        if (valueY2 < lowerValue)
        	offs2.setValue(i, lowerValue) ;
        else if (valueY2 > upperValue)
        	offs2.setValue(i, upperValue) ;
        else
        	offs2.setValue(i, valueY2) ;
			} // if
		} // if

		return offSpring;                                                                                      
	} // doCrossover


	/**
	 * Executes the operation
	 * @param object An object containing an array of two parents
	 * @return An object containing the offSprings
	 */
	public Object execute(Object object) throws JMException {
		Solution [] parents = (Solution [])object;    	

		if (parents.length != 2) {
			Configuration.logger_.severe("BLXAlphaCrossover.execute: operator needs two " +
					"parents");
			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMException("Exception in " + name + ".execute()") ;      
		} // if

		if (!(VALID_TYPES.contains(parents[0].getType().getClass())  &&
				VALID_TYPES.contains(parents[1].getType().getClass())) ) {
			Configuration.logger_.severe("BLXAlphaCrossover.execute: the solutions " +
					"type " + parents[0].getType() + " is not allowed with this operator");

			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMException("Exception in " + name + ".execute()") ;
		} // if 

		Solution [] offSpring;
		offSpring = doCrossover(crossoverProbability_,
				parents[0],
				parents[1]);

		return offSpring;
	} // execute 
} // BLXAlphaCrossover
