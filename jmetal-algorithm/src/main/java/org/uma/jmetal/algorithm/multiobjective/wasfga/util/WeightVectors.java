package org.uma.jmetal.algorithm.multiobjective.wasfga.util;

import org.uma.jmetal.util.JMetalException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author Rubén Saborido Infantes
 * This class offers different methods to manipulate weight vectors.
 */
public class WeightVectors {
	/**
	 * Validate if the number of components of all weight vectors has the expected dimensionality.
	 *
	 * @param weights Weight vectors to validate
	 * @param numberOfComponents Number of components each weight vector must have
	 * @return True if the weight vectors are correct, False if the weight vectors are incorrect
	 */
	public static boolean validate (double[][] weights, int numberOfComponents) {
		int i;
		boolean correct;
		correct = (weights != null && weights.length > 0);

		i = 0;
		while (correct && i < weights.length)
		{
			correct = (weights[i].length == numberOfComponents);
			i++;
		}

		return correct;
	}

	/**
	 * Generate uniform weight vectors in two dimension
	 *
	 * @param epsilon         Distance between each component of the weight vector
	 * @param numberOfWeights Number of weight vectors to generate
	 * @return A set of weight vectors
	 */
	public static double[][] initializeUniformlyInTwoDimensions(double epsilon, int numberOfWeights) {
		double[][] weights = new double[numberOfWeights][2];
		
		int indexOfWeight;
		double w, jump;
		
		jump = (1 - (2 * epsilon)) / (numberOfWeights - 1);
		indexOfWeight = 0;
		
		w = epsilon;
		
		//while(w <= (1-epsilon))
		while (indexOfWeight < numberOfWeights) {
			weights[indexOfWeight][0] = w;
			weights[indexOfWeight][1] = 1 - w;
			
			w = w + jump;
			
			indexOfWeight = indexOfWeight + 1;
		}
		
		return weights;
	}

	/**
	 * Read a set of weight vector from a file in the resources folder in jMetal
	 *
	 * @param filePath The name of file in the resources folder of jMetal
	 * @return A set of weight vectors
	 */
	public static double[][] readFromResourcesInJMetal(String filePath) {
		double[][] weights;

		Vector<double[]> listOfWeights = new Vector<>();

		try {
			InputStream in = WeightVectors.class.getResourceAsStream("/" + filePath);
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);

			int numberOfObjectives = 0;
			int j;
			String aux = br.readLine();
			while (aux != null) {
				StringTokenizer st = new StringTokenizer(aux);
				j = 0;
				numberOfObjectives = st.countTokens();
				double[] weight = new double[numberOfObjectives];

				while (st.hasMoreTokens()) {
					weight[j] = new Double(st.nextToken());
					j++;
				}

				listOfWeights.add(weight);
				aux = br.readLine();
			}
			br.close();

			weights = new double[listOfWeights.size()][numberOfObjectives];
			for (int indexWeight = 0; indexWeight < listOfWeights.size(); indexWeight++) {
				System.arraycopy(listOfWeights.get(indexWeight), 0, weights[indexWeight], 0, numberOfObjectives);
			}
		} catch (Exception e) {
			throw new JMetalException("readFromResourcesInJMetal: failed when reading for file: " + filePath + "", e);
		}

		return weights;
	}
	
	/**
	 * Read a set of weight vector from a file
	 *
	 * @param filePath A file containing the weight vectors
	 * @return A set of weight vectors
	 */
	public static double[][] readFromFile(String filePath) {
		double[][] weights;
		
		Vector<double[]> listOfWeights = new Vector<>();
		
		try {
			// Open the file
			FileInputStream fis = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			
			int numberOfObjectives = 0;
			int j;
			String aux = br.readLine();
			while (aux != null) {
				StringTokenizer st = new StringTokenizer(aux);
				j = 0;
				numberOfObjectives = st.countTokens();
				double[] weight = new double[numberOfObjectives];
				
				while (st.hasMoreTokens()) {
					weight[j] = new Double(st.nextToken());
					j++;
				}
				
				listOfWeights.add(weight);
				aux = br.readLine();
			}
			br.close();
			
			weights = new double[listOfWeights.size()][numberOfObjectives];
			for (int indexWeight = 0; indexWeight < listOfWeights.size(); indexWeight++) {
				System.arraycopy(listOfWeights.get(indexWeight), 0, weights[indexWeight], 0, numberOfObjectives);
			}
		} catch (Exception e) {
			throw new JMetalException("readFromFile: failed when reading for file: " + filePath + "", e);
		}
		
		return weights;
	}
	
	/**
	 * Calculate the inverse of a set of weight vectors
	 *
	 * @param weights A set of weight vectors
	 * @param normalize True if the weights should be normalize by the sum of the components
	 * @return A set of weight vectors
	 */
	public static double[][] invert(double[][] weights, boolean normalize) {
		double[][] result = new double[weights.length][weights[0].length];
		
		for (int indexOfWeight = 0; indexOfWeight < weights.length; indexOfWeight++) {
			if (normalize) {
				double sum = 0;
				
				for (int indexOfComponent = 0; indexOfComponent < weights[indexOfWeight].length; indexOfComponent++) {
					sum = sum + (1.0 / weights[indexOfWeight][indexOfComponent]);
				}
				
				for (int indexOfComponent = 0; indexOfComponent < weights[indexOfWeight].length; indexOfComponent++) {
					result[indexOfWeight][indexOfComponent] = (1.0 / weights[indexOfWeight][indexOfComponent]) / sum;
				}
			} else {
				for (int indexOfComponent = 0; indexOfComponent < weights[indexOfWeight].length; indexOfComponent++)
					result[indexOfWeight][indexOfComponent] = 1.0 / weights[indexOfWeight][indexOfComponent];
			}
		}
		
		return result;
	}
}
