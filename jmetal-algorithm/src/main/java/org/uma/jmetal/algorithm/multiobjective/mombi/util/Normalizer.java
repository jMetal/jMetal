package org.uma.jmetal.algorithm.multiobjective.mombi.util;

import java.util.List;

public class Normalizer {
	private final List<Double> min;
	private final List<Double> max;
	
	public Normalizer(List<Double> min, List<Double> max) {
		this.min = min;
		this.max = max;
	}
	
	public Double normalize(Double input, int index) {						 			
		Double diff		= max.get(index) - min.get(index);
		Double output 	= (input-min.get(index))/diff; 
		
		return output;
	}
}