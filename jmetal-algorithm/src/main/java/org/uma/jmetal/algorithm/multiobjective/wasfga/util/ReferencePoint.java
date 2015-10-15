package org.uma.jmetal.algorithm.multiobjective.wasfga.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author Rubén Saborido Infantes
 * This class offers different methods to manipulate reference points.
 * A reference point is a vector containing a value for each component of an objective function.
 */
public class ReferencePoint {

	Vector<Double> referencePoint;

	public enum ReferencePointType {
		ACHIEVABLE, UNACHIEVABLE
	};

	JMetalRandom randomGenerator = JMetalRandom.getInstance() ;

	/**
	 * Construct a reference point reading it from a file
	 * @param referencePointFileName File containing a reference point	 
	 */
	public ReferencePoint(String referencePointFileName) throws IOException {
		// Open the aspiration file
		FileInputStream fis = new FileInputStream(referencePointFileName);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);

		referencePoint = new Vector<Double>();
		String aux = br.readLine();
		while (aux != null) {
			StringTokenizer st = new StringTokenizer(aux);

			while (st.hasMoreTokens()) {
				Double value = (new Double(st.nextToken()));
				referencePoint.add(value);
			}
			aux = br.readLine();
		}
		br.close();
	}

	/**
	 * Construct a reference point from a vector
	 * @param referencePoint Vector defining a reference point	 	 
	 */
	public ReferencePoint(double[] referencePoint) {
		this.referencePoint = new Vector<Double>();

		for (int indexOfComponent = 0; indexOfComponent < referencePoint.length; indexOfComponent++)
			this.referencePoint.add(Double
					.valueOf(referencePoint[indexOfComponent]));
	}

	/**
	 * Construct an empty reference point with a dimension given
	 * @param numberOfObjectives The number of components 
	 */
	public ReferencePoint(int numberOfObjectives) {
		this.referencePoint = new Vector<Double>(numberOfObjectives);
		referencePoint.setSize(numberOfObjectives);
	}

	/**
	 * Construct a random reference point from a Pareto front file
	 * @param type The type of the created reference point
	 * @param paretoFrontFileName A Pareto front in a file
	 */
	public ReferencePoint(ReferencePointType type, String paretoFrontFileName)
          throws JMetalException, FileNotFoundException {
		int randomIndexPoint;
		double[] minimumValues, maximumValues;
		Front front ;
		int index, numberOfObjectives;

		front = new ArrayFront(paretoFrontFileName) ;

		numberOfObjectives = front.getPointDimensions();

		minimumValues = FrontUtils.getMinimumValues(front);
		maximumValues = FrontUtils.getMaximumValues(front);

		randomIndexPoint = randomGenerator.nextInt(0, front.getNumberOfPoints());

		referencePoint = new Vector<Double>();

		switch (type) {
		case ACHIEVABLE:
			for (index = 0; index < numberOfObjectives; index++) {
				this.referencePoint.add(randomGenerator.nextDouble(
                front.getPoint(randomIndexPoint).getDimensionValue(index),
                maximumValues[index]));
			}
			break;

		case UNACHIEVABLE:
			for (index = 0; index < numberOfObjectives; index++) {
				this.referencePoint.add(randomGenerator.nextDouble(
                minimumValues[index], front.getPoint(randomIndexPoint).getDimensionValue(index)));
			}
			break;
		}
	}
	
	/**
	 * Get a component of the reference point
	 * @param indexOfObjective The index of the component 
	 * @return The value of the selected component
	 */
	public Double get(int indexOfObjective) {
		return this.referencePoint.get(indexOfObjective);
	}

	/**
	 * Set the value of a component of the reference point
	 * @param indexOfObjective The index of the component
	 * @param valueOfObjective The new value of the component 
	 * @return The value of the selected component
	 */
	public void set(int indexOfObjective, Double valueOfObjective) {
		this.referencePoint.set(indexOfObjective, valueOfObjective);
	}

	/**
	 * Get the size of the reference point
	 * @return The number of components of the reference point
	 */
	public int size() {
		return this.referencePoint.size();
	}

	/**
	 * Convert the reference point in a vector of double
	 * @return A vector of double containing the values of the reference point
	 */
	public double[] toDouble() {
		double[] result = new double[this.referencePoint.size()];
		for (int indexOfObjective = 0; indexOfObjective < this.referencePoint
				.size(); indexOfObjective++) {
			result[indexOfObjective] = referencePoint.get(indexOfObjective)
					.doubleValue();
		}

		return result;
	}

	/**
	 * Return the solutions Pareto-dominated by the reference point
	 * @param solutions A set of solutions	
	 * @return The solutions Pareto-dominated by the reference point
	 */
	public double[][] getDominatedSolutionsByMe(double[][] solutions) {
		double[][] result;
		ArrayList<Integer> indexsOfDominatedSolutions = new ArrayList<Integer>();

		for (int indexOfSolution = 0; indexOfSolution < solutions.length; indexOfSolution++) {
			if (ParetoDominance.checkParetoDominance(this.toDouble(),
					solutions[indexOfSolution]) == -1) {
				indexsOfDominatedSolutions
						.add(Integer.valueOf(indexOfSolution));
			}
		}

		result = new double[indexsOfDominatedSolutions.size()][referencePoint
				.size()];
		for (int indexOfSolution = 0; indexOfSolution < indexsOfDominatedSolutions
				.size(); indexOfSolution++) {
			result[indexOfSolution] = solutions[indexsOfDominatedSolutions
					.get(indexOfSolution)].clone();
		}

		return result;
	}

	/**
	 * Return the solutions which Pareto-dominate to the reference point
	 * @param solutions A set of solutions	
	 * @return The solutions which Pareto-dominate to the reference point
	 */
	public double[][] getDominantSolutions(double[][] solutions) {
		double[][] result;
		ArrayList<Integer> indexsOfDominatedSolutions = new ArrayList<Integer>();

		for (int indexOfSolution = 0; indexOfSolution < solutions.length; indexOfSolution++) {
			if (ParetoDominance.checkParetoDominance(this.toDouble(),
					solutions[indexOfSolution]) == 1) {
				indexsOfDominatedSolutions
						.add(Integer.valueOf(indexOfSolution));
			}
		}

		result = new double[indexsOfDominatedSolutions.size()][referencePoint
				.size()];
		for (int indexOfSolution = 0; indexOfSolution < indexsOfDominatedSolutions
				.size(); indexOfSolution++) {
			result[indexOfSolution] = solutions[indexsOfDominatedSolutions
					.get(indexOfSolution)].clone();
		}

		return result;
	}

	/**
	 * Return the solutions greater of equal than the reference point
	 * @param solutions A set of solutions	
	 * @return The solutions greater of equal than the reference point
	 */
	public double[][] getSolutionsGreaterOrEqualThanMe(double[][] solutions) {
		double[][] result;

		ArrayList<Integer> indexsOfSolutions = new ArrayList<Integer>();

		for (int indexOfSolution = 0; indexOfSolution < solutions.length; indexOfSolution++) {
			boolean isGreater = true;
			int indexOfObjective = 0;

			while (isGreater
					&& indexOfObjective < solutions[indexOfSolution].length) {
				isGreater = solutions[indexOfSolution][indexOfObjective] >= this.referencePoint
						.get(indexOfObjective);
				indexOfObjective++;
			}

			if (isGreater) {
				indexsOfSolutions.add(Integer.valueOf(indexOfSolution));
			}
		}

		result = new double[indexsOfSolutions.size()][referencePoint.size()];
		for (int indexOfSolution = 0; indexOfSolution < indexsOfSolutions
				.size(); indexOfSolution++) {
			result[indexOfSolution] = solutions[indexsOfSolutions
					.get(indexOfSolution)].clone();
		}

		return result;
	}

	/**
	 * Return the solutions greater of equal than the reference point
	 * @param solutions A set of solutions	
	 * @return The solutions greater of equal than the reference point
	 */
	public List getSolutionsGreaterOrEqualThanMe(List<Solution> solutions) {
		ArrayList<Integer> indexsOfSolutions = new ArrayList<Integer>();

		for (int indexOfSolution = 0; indexOfSolution < solutions.size(); indexOfSolution++) {
			boolean isGreater = true;
			int indexOfObjective = 0;

			while (isGreater
					&& indexOfObjective < solutions.get(indexOfSolution)
							.getNumberOfObjectives()) {
				isGreater = solutions.get(indexOfSolution).getObjective(
						indexOfObjective) >= this.referencePoint
						.get(indexOfObjective);
				indexOfObjective++;
			}

			if (isGreater) {
				indexsOfSolutions.add(Integer.valueOf(indexOfSolution));		
			}
		}

    List<Solution> result = new ArrayList<Solution>(indexsOfSolutions.size());
		for (int indexOfSolution = 0; indexOfSolution < indexsOfSolutions.size(); indexOfSolution++) {
			result.add(solutions.get(indexsOfSolutions.get(indexOfSolution)));
		}

		return result;
	}

	/**
	 * Return the solutions lower of equal than the reference point
	 * @param solutions A set of solutions	
	 * @return The solutions lower of equal than the reference point
	 */
	public double[][] getSolutionsLowerOrEqualThanMe(double[][] solutions) {
		double[][] result;

		ArrayList<Integer> indexsOfSolutions = new ArrayList<Integer>();

		for (int indexOfSolution = 0; indexOfSolution < solutions.length; indexOfSolution++) {
			boolean isLower = true;
			int indexOfObjective = 0;

			while (isLower
					&& indexOfObjective < solutions[indexOfSolution].length) {
				isLower = solutions[indexOfSolution][indexOfObjective] <= this.referencePoint
						.get(indexOfObjective);
				indexOfObjective++;
			}

			if (isLower) {
				indexsOfSolutions.add(Integer.valueOf(indexOfSolution));
			}
		}

		result = new double[indexsOfSolutions.size()][referencePoint.size()];
		for (int indexOfSolution = 0; indexOfSolution < indexsOfSolutions
				.size(); indexOfSolution++) {
			result[indexOfSolution] = solutions[indexsOfSolutions
					.get(indexOfSolution)].clone();
		}

		return result;
	}

	/**
	 * Return the solutions lower of equal than the reference point
	 * @param solutions A set of solutions	
	 * @return The solutions lower of equal than the reference point
	 */
	public List<Solution> getSolutionsLowerOrEqualThanMe(List<Solution> solutions) {
		ArrayList<Integer> indexsOfSolutions = new ArrayList<Integer>();
		
		for (int indexOfSolution = 0; indexOfSolution < solutions.size(); indexOfSolution++) {
			boolean isLower = true;
			int indexOfObjective = 0;

			while (isLower
					&& indexOfObjective < solutions.get(indexOfSolution).getNumberOfObjectives()) {
				isLower = solutions.get(indexOfSolution).getObjective(indexOfObjective) <= this.referencePoint
						.get(indexOfObjective);
				indexOfObjective++;
			}

			if (isLower) {
				indexsOfSolutions.add(Integer.valueOf(indexOfSolution));		
			}
		}

		List<Solution> result = new ArrayList<Solution>(indexsOfSolutions.size());
		for (int indexOfSolution = 0; indexOfSolution < indexsOfSolutions.size(); indexOfSolution++) {
			result.add(solutions.get(indexsOfSolutions.get(indexOfSolution)));
		}
		
		return result;
	}	
}
