package org.uma.jmetal.algorithm.multiobjective.mombi;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.Solution;

/** 
 * This class implements a set of utility functions based on the Tchebycheff aggregation approach
 * @author Juan J. Durillo
 * 
 * ToDo List: 
 * + check the size of nadir and reference points are the correct ones
 * + check that the function that needs to be evaluated is the correct one
 *
 * @param <S>
 */

public class TchebycheffUtilityFunctionsSet<S extends Solution<?>> extends AbstractUtilityFunctionsSet<S> {
	private final List<Double> referencePoint;

	
	
	public TchebycheffUtilityFunctionsSet(String file_path, 
										  List<Double> referencePoint) {
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
					  weightVector.get(i) *
					  Math.abs(solution.getObjective(i) - this.referencePoint.get(i)));
		
		return result;
	}

}
