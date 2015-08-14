package org.uma.jmetal.algorithm.multiobjective.mombi;

import java.util.List;

import org.uma.jmetal.solution.Solution;

public class ASFUtilityFunctionSet<S extends Solution<?>> extends AbstractUtilityFunctionsSet<S> {

	private final List<Double> referencePoint;
	
	public ASFUtilityFunctionSet(String file_path, List<Double> referencePoint) {
		super(file_path);
		this.referencePoint = referencePoint;
	}

	@Override
	public Double evaluate(S solution, int vector) {
		if ((vector < 0) || (vector >= this.getSize()))
			return Double.NaN; // ToDo: to properly address this case (maybe an exception?)
		
		double result = Double.NEGATIVE_INFINITY;
		List<Double> weightVector = this.getWeightVector(vector);
		for (int i = 0; i < weightVector.size(); i++) 
			result = Math.max(result, 
					  Math.abs(solution.getObjective(i) - this.referencePoint.get(i))/weightVector.get(i));
		
		return result;
		
	}

}
