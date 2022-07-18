package org.uma.jmetal.algorithm.multiobjective.mombi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * @author Juan J. Durillo
 * Modified by Antonio J. Nebro
 *
 * @param <S>
 */
@SuppressWarnings("serial")
public class ASFWASFGA<S extends Solution<?>> extends AbstractUtilityFunctionsSet<S> {

	private final List<Double> interestPoint;
	private double augmentationCoefficient = 0.001;
	private List<Double> utopia = null;
	private List<Double> nadir  = null;
	
	
	public ASFWASFGA(double [][] weights, List<Double> interestPoint) {
		super(weights);
		this.interestPoint = interestPoint;
	}
	
	public ASFWASFGA(double [][] weights) {
		super(weights);
		this.interestPoint = new ArrayList<>(this.getVectorSize());
		for (int i = 0; i < this.getVectorSize(); i++)
			this.interestPoint.add(0.0);
	}
	
	public ASFWASFGA(String file_path, List<Double> interestPoint) {
		super(file_path);
		this.interestPoint = interestPoint;
	}
	
	public ASFWASFGA(String file_path) {
		super(file_path);
		this.interestPoint = new ArrayList<>(this.getVectorSize());
		for (int i = 0; i < this.getVectorSize(); i++)
			this.interestPoint.add(0.0);
	}

	public void updatePointOfInterest(List<Double> newInterestPoint ) {
		if (this.interestPoint.size()!=newInterestPoint.size())
			throw new JMetalException("Wrong dimension of the interest point vector");

		for (int i = 0; i < newInterestPoint.size(); i++) {
            this.interestPoint.set(i,newInterestPoint.get(i));
		}
	}


	@Override
	public Double evaluate(S solution, int vector) {
		if ((vector < 0) || (vector >= this.getSize())) {
			throw new JMetalException("Vector value " + vector + " invalid") ;
		}
		

		
		List<Double> weightVector 	 =  this.getWeightVector(vector);
		List<Double> objectiveValues = Arrays.stream(solution.objectives()).boxed().collect(Collectors.toCollection(() -> new ArrayList<>(solution.objectives().length)));

        double result = -1e10;
		double secondSum = 0.0;
		for (int i = 0; i < weightVector.size(); i++) {
									
			double temp = objectiveValues.get(i) - this.interestPoint.get(i);
						 
			
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
