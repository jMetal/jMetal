package org.uma.jmetal.algorithm.multiobjective.mombi;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.Solution;

public class ASFUtilityFunctionSet<S extends Solution<?>> extends AbstractUtilityFunctionsSet<S> {

	private final List<Double> referencePoint;
	private Normalizer			normalizer = null;
	
	public ASFUtilityFunctionSet(String file_path, List<Double> referencePoint) {
		super(file_path);
		this.referencePoint = referencePoint;
	}

	@Override
	public Double evaluate(S solution, int vector) {
		if ((vector < 0) || (vector >= this.getSize()))
			return Double.NaN; // ToDo: to properly address this case (maybe an exception?)
		
		double result = Double.NEGATIVE_INFINITY;
		List<Double> weightVector 	 =  this.getWeightVector(vector);
		List<Double> objectiveValues =  new ArrayList<>(solution.getNumberOfObjectives());
		for (int i = 0; i < solution.getNumberOfObjectives();i++) 
			if (normalizer==null)
				objectiveValues.add(solution.getObjective(i));
			else
				objectiveValues.add(this.normalizer.normalize(solution.getObjective(i),i));
				
		
		for (int i = 0; i < weightVector.size(); i++) {
			//System.out.println(objectiveValues.get(i)+"\t"+this.referencePoint.get(i));
			result = Math.max(result, Math.abs(objectiveValues.get(i) - 0.0)/weightVector.get(i));
		}
		return result;
		
	}
	
	public void setNormalizer(Normalizer normalizer) {
		this.normalizer = normalizer;
	}
	


}
