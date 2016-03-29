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
public class ASFUtilityFunctionSet<S extends Solution<?>> extends AbstractUtilityFunctionsSet<S> {

	private final List<Double> referencePoint; 
	private Normalizer normalizer = null;
	
	public ASFUtilityFunctionSet(double [][] weights, List<Double> referencePoint) {
		super(weights);
		this.referencePoint = referencePoint;
	}
	
	public ASFUtilityFunctionSet(double [][] weights) {
		super(weights);
		this.referencePoint = new ArrayList<>(this.getVectorSize());
		for (int i = 0; i < this.getVectorSize(); i++)
			this.referencePoint.add(0.0);
	}
	
	public ASFUtilityFunctionSet(String file_path, List<Double> referencePoint) {
		super(file_path);
		this.referencePoint = referencePoint;
	}
	
	public ASFUtilityFunctionSet(String file_path) {
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

		double result = Double.NEGATIVE_INFINITY;
		List<Double> weightVector 	 =  this.getWeightVector(vector);
		List<Double> objectiveValues =  new ArrayList<>(solution.getNumberOfObjectives());
		for (int i = 0; i < solution.getNumberOfObjectives();i++) 
			if (normalizer==null) {
        objectiveValues.add(solution.getObjective(i));
      }
			else {
        objectiveValues.add(this.normalizer.normalize(solution.getObjective(i), i));
      }
		
		for (int i = 0; i < weightVector.size(); i++) {
			result = Math.max(result, 
							  Math.abs(objectiveValues.get(i) - this.referencePoint.get(i))
							  /(weightVector.get(i) > 0.0 ? weightVector.get(i):1e-2));
		}
		return result;
		
	}
	
	public void setNormalizer(Normalizer normalizer) {
		this.normalizer = normalizer;
	}
}
