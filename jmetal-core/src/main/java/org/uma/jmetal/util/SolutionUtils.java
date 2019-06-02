package org.uma.jmetal.util;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * Created by Antonio J. Nebro on 6/12/14.
 */
public class SolutionUtils {

  /**
   * Return the best solution between those passed as arguments. If they are equal or incomparable
   * one of them is chosen randomly.
   *
   * @return The best solution
   */
  public static <S extends Solution<?>> S getBestSolution(S solution1, S solution2, Comparator<S> comparator) {
    return getBestSolution(solution1, solution2, comparator, () -> JMetalRandom.getInstance().nextDouble());
  }

  /**
   * Return the best solution between those passed as arguments. If they are equal or incomparable
   * one of them is chosen randomly.
   *
   * @param randomGenerator {@link RandomGenerator} for the equality case
   * @return The best solution
   */
  public static <S extends Solution<?>> S getBestSolution(S solution1, S solution2, Comparator<S> comparator, RandomGenerator<Double> randomGenerator) {
    return getBestSolution(solution1, solution2, comparator, (a, b) -> randomGenerator.getRandomValue() < 0.5 ? a : b);
  }

  /**
   * Return the best solution between those passed as arguments. If they are equal or incomparable
   * one of them is chosen based on the given policy.
   *
   * @return The best solution
   */
  public static <S extends Solution<?>> S getBestSolution(S solution1, S solution2, Comparator<S> comparator, BinaryOperator<S> equalityPolicy) {
    S result;
    int flag = comparator.compare(solution1, solution2);
    if (flag == -1) {
      result = solution1;
    } else if (flag == 1) {
      result = solution2;
    } else {
      result = equalityPolicy.apply(solution1, solution2);
    }

    return result;
  }

  /**
   * Returns the euclidean distance between a pair of solutions in the objective space
   */
  public static <S extends Solution<?>> double distanceBetweenObjectives(S firstSolution, S secondSolution) {

    double diff;
    double distance = 0.0;

    //euclidean distance
    for (int nObj = 0; nObj < firstSolution.getNumberOfObjectives(); nObj++) {
      diff = firstSolution.getObjective(nObj) - secondSolution.getObjective(nObj);
      distance += Math.pow(diff, 2.0);
    }

    return Math.sqrt(distance);
  }

  static <S extends Solution<?>> double distanceBetweenObjectives_normalize(S firstSolution, S secondSolution,double maxs[],double mins[]) {

    double diff;
    double distance = 0.0;
    //euclidean distance
    for (int nObj = 0; nObj < firstSolution.getNumberOfObjectives();nObj++){
      diff = (firstSolution.getObjective(nObj)/(maxs[nObj]-mins[nObj])) - (secondSolution.getObjective(nObj)/(maxs[nObj]-mins[nObj]));
      distance += Math.pow(diff,2.0);
      //distance += Math.abs(diff);//<----- solo pa ver que carajos pasa
    } // for

    return Math.sqrt(distance);
  }

  /**
   * Returns the minimum distance from a <code>Solution</code> to a <code>SolutionSet according to
   * the encodings.variable values</code>.
   *
   * @param solution     The <code>Solution</code>.
   * @param solutionList The <code>List<Solution></></code>.
   * @return The minimum distance between solution and the set.
   */
  public static double distanceToSolutionListInSolutionSpace(DoubleSolution solution,
                                                             List<DoubleSolution> solutionList) {
    double distance = Double.MAX_VALUE;

    for (int i = 0; i < solutionList.size(); i++) {
      double aux = distanceBetweenSolutionsInObjectiveSpace(solution, solutionList.get(i));
      if (aux < distance)
        distance = aux;
    }

    return distance;
  }

  /**
   * Returns the distance between two solutions in the search space.
   *
   * @param solutionI The first <code>Solution</code>.
   * @param solutionJ The second <code>Solution</code>.
   * @return the distance between solutions.
   */
  public static double distanceBetweenSolutionsInObjectiveSpace(DoubleSolution solutionI, DoubleSolution solutionJ) {
    double distance = 0.0;

    double diff;
    for (int i = 0; i < solutionI.getNumberOfVariables(); i++) {
      diff = solutionI.getVariableValue(i) - solutionJ.getVariableValue(i);
      distance += Math.pow(diff, 2.0);
    }

    return Math.sqrt(distance);
  }

  /**
   * Returns the average euclidean distance of a solution to the solutions of a list.
   */
  public static <S extends Solution<?>> double averageDistanceToSolutionList(
          S solution,
          List<S> solutionList) {

    double sumOfDistances = 0.0;
    for (S sol : solutionList) {
      sumOfDistances += distanceBetweenObjectives(
              solution,
              sol);
    }

    return sumOfDistances / solutionList.size();
  }
  
  	/**
	 * It returns the normalized solution given the minimum and maximum values for
	 * each objective
	 * 
	 * @param solution  to be normalized
	 * @param minValues minimum values for each objective
	 * @param maxValues maximum value for each objective
	 * @return normalized solution
	 */
	public static Solution<?> normalize(Solution<?> solution, double[] minValues, double[] maxValues) {

		if (solution == null) {
			throw new JMetalException("The solution should not be null");
		}
		
		if (minValues == null || maxValues == null) {
			throw new JMetalException("The minValues and maxValues should not be null");
		}
		
		if (minValues.length == 0 || maxValues.length == 0) {
			throw new JMetalException("The minValues and maxValues should not be empty");
		}

		if (minValues.length != maxValues.length) {
			throw new JMetalException("The minValues and maxValues should have the same length");
		}

		if (solution.getNumberOfObjectives() != minValues.length) {
			throw new JMetalException("The number of objectives should be the same to min and max length");
		}

		Solution<?> copy = (Solution<?>) solution.copy();

		for (int i = 0; i < copy.getNumberOfObjectives(); i++) {
			copy.setObjective(i, NormalizeUtils.normalize(solution.getObjective(i), minValues[i], maxValues[i]));
		}

		return copy;
	}
}
