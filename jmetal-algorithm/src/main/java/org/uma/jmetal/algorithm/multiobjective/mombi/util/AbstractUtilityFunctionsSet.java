package org.uma.jmetal.algorithm.multiobjective.mombi.util;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;



/**
 * @author Juan J. Durillo
 * Modified by Antonio J. Nebro
 *
 * @param <S>
 */
@SuppressWarnings("serial")
public abstract class AbstractUtilityFunctionsSet<S extends Solution<?>> implements Serializable {

	private List<List<Double>> weightVectors;
	private int vectorSize;
	
	public AbstractUtilityFunctionsSet(double [][] weights) {
		this.weightVectors = new ArrayList<>();
		for (int i = 0; i < weights.length; i++) {
			this.weightVectors.add(new ArrayList<Double>());
			for (int j = 0; j < weights[i].length;j++) {
				this.weightVectors.get(i).add(weights[i][j]);
			}
		}
		if (this.weightVectors.size() > 0) {
			this.vectorSize = this.weightVectors.get(0).size();
		}
				
	}
	
	public AbstractUtilityFunctionsSet(String file_path) {
		loadWeightsFromFile(file_path);
	}

	/**
	 * Returns the number of utility functions stored in this set
	 * @return The number of vectors
	 */
	public int getSize() {
		return this.weightVectors.size();
	}
	
	/**
	 * Returns the number of components for all weight vectors
	 */
	public int getVectorSize() {
		return this.vectorSize;
	}
	
	/**
	 * Returns a given weight vector
	 */
	public List<Double> getWeightVector(int index) {
		if ((index < 0) && (index >= weightVectors.size())) {
			throw new JMetalException("getWeightVector: index " + index + " invalid ") ;
		}
		return this.weightVectors.get(index);
	}
	
	/**
	 * Evaluates a solution using all the utility functions stored in this set
	 * @param solution
	 * @return
	 */
	public List<Double> evaluate(S solution) {
		List<Double> result = IntStream.range(0, this.getSize()).mapToObj(i -> evaluate(solution, i)).collect(Collectors.toCollection(() -> new ArrayList<>(this.getSize())));
        return result;
	}
	
	/**
	 * Evaluates a solution using the i-th utility function stored in this set
	 * @param solution
	 * @param vector
	 * @return
	 */
	public abstract Double evaluate(S solution, int vector);

	/**
	 * Reads a set of weight vectors from a file.
	 * The expected format for the file is as follows.
	 * The first line should start with at least the following three tokens
	 * # <number_of_vectors> <number_of_obectives>
	 * Any other token on this line will be ignored. 
	 * <number_of_vectors> indicates how many weight vectors are included in this file
	 * <number_of_objectives> indicates how many component has each included vector
	 *
	 * Each of the following lines of the file represents a weight vector of at least
	 * <number_of_objectives> components 
	 * If more components are provided, they will be ignored by the program
	 *
	 * @param filePath The path in the file system of the file containing the weight vectors
	 */
	public void loadWeightsFromFile(String filePath)  {
		InputStream in = getClass().getClassLoader().getResourceAsStream(filePath);
		if (in == null) {
			try {
				in = new FileInputStream(filePath) ;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader buffer = new BufferedReader(isr);

		// First line format: # <num_weights> <num_objectives>
		String line = null;
		try {
			line = buffer.readLine();


			StringTokenizer st = new StringTokenizer(line);
			st.nextToken(); // reading the #

			// reading the number of weights (only used as estimator
			// of the number of them)
			int number_of_weight_vectors = parseInt(st.nextToken());
			this.weightVectors = new ArrayList<>(number_of_weight_vectors);

			// reading the number of objectives
			int number_of_objectives     = parseInt(st.nextToken());
			this.vectorSize 			 = number_of_objectives;

			while ((line = buffer.readLine())!=null) {
				st = new StringTokenizer(line);
				List<Double> new_vector = new ArrayList<>(number_of_objectives);
				for (int i = 0; i < number_of_objectives; i++)
					new_vector.add(parseDouble(st.nextToken()));
				this.weightVectors.add(new_vector);
			}
		} catch (IOException e) {
			throw new JMetalException("loadWeightsFromFile: failed when reading for file: "
							+ "/" + filePath) ;
    }
	}
}
