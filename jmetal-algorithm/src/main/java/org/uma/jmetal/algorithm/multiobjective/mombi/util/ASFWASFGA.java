package org.uma.jmetal.algorithm.multiobjective.mombi.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Juan J. Durillo
 * Modified by Antonio J. Nebro
 *
 * @param <S>
 */
@SuppressWarnings("serial")
public class ASFWASFGA<S extends Solution<?>> extends AbstractUtilityFunctionsSet<S> {

	private final List<Double> referencePoint; 	
	private double augmentationCoefficient = 0.001;
	private List<Double> utopia = null;
	private List<Double> nadir  = null;
	
	
	public ASFWASFGA(double [][] weights, List<Double> referencePoint) {
		super(weights);
		this.referencePoint = referencePoint;
	}
	
	public ASFWASFGA(double [][] weights) {
		super(weights);
		this.referencePoint = new ArrayList<>(this.getVectorSize());
		for (int i = 0; i < this.getVectorSize(); i++)
			this.referencePoint.add(0.0);
	}
	
	public ASFWASFGA(String file_path, List<Double> referencePoint) {
		super(file_path);
		this.referencePoint = referencePoint;
	}
	
	public ASFWASFGA(String file_path) {
		super(file_path);
		this.referencePoint = new ArrayList<>(this.getVectorSize());
		for (int i = 0; i < this.getVectorSize(); i++)
			this.referencePoint.add(0.0);
	}

	@Override
	public Double evaluate(S solution, int vector) {
		if ((vector < 0) || (vector >= this.getSize())) {
			throw new JMetalException("Vector value " + vector + " invalid") ;
		}
		

		
		List<Double> weightVector 	 =  this.getWeightVector(vector);
		List<Double> objectiveValues =  new ArrayList<>(solution.getNumberOfObjectives());
		for (int i = 0; i < solution.getNumberOfObjectives();i++) 			
				objectiveValues.add(solution.getObjective(i));
		
		double result = -1e10;
		double secondSum = 0.0;
		for (int i = 0; i < weightVector.size(); i++) {
									
			double temp = objectiveValues.get(i) - this.referencePoint.get(i);
						 
			
			if (nadir!=null && utopia!=null) {
				temp = temp / (this.nadir.get(i) - this.utopia.get(i));				
			}
			
			double temp_product = temp * weightVector.get(i);
			
			if (temp_product > result)
				result = temp_product;
			
			secondSum += temp_product;
		}

		return result + (secondSum * this.augmentationCoefficient);		
	}
	
	public void setNadir(List<Double> nadir) {
		this.nadir = nadir;
		
	}
	public void setUtopia(List<Double> utopia) {
		this.utopia = utopia;
	}
}
