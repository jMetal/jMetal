package org.uma.jmetal.util;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 6/12/14.
 */
public class SolutionUtils {
  private static JMetalRandom randomGenerator = JMetalRandom.getInstance() ;

  /**
   * Return the best solution between those passed as arguments. If they are equal or incomparable
   * one of them is chosen randomly.
   * @param solution1
   * @param solution2
   * @return The best solution
   */
  public static <S extends Solution<?>> S getBestSolution(S solution1, S solution2, Comparator<S> comparator) {
    S result ;
    int flag = comparator.compare(solution1, solution2);
    if (flag == -1) {
      result = solution1;
    } else if (flag == 1) {
      result = solution2;
    } else {
      if (randomGenerator.nextDouble() < 0.5) {
        result = solution1;
      } else {
        result = solution2;
      }
    }

    return result ;
  }
  
  
  /**
   * Returns the euclidean distance between a pair of solutions in the objective space
   * @param firstSolution
   * @param secondSolution
   * @return
   */
   static <S extends Solution<?>> double distanceBetweenObjectives(S firstSolution, S secondSolution) {
   
	    double diff;  
	    double distance = 0.0;
	    //euclidean distance
	    for (int nObj = 0; nObj < firstSolution.getNumberOfObjectives();nObj++){
	      diff = firstSolution.getObjective(nObj) - secondSolution.getObjective(nObj);
	      distance += Math.pow(diff,2.0);           
	    } // for   
	        
	    return Math.sqrt(distance);
  }

    /** Returns the minimum distance from a <code>Solution</code> to a
     * <code>SolutionSet according to the encodings.variable values</code>.
     * @param solution The <code>Solution</code>.
     * @param solutionList The <code>List<Solution></></code>.
     * @return The minimum distance between solution and the set.
     */
    public static double distanceToSolutionListInSolutionSpace(DoubleSolution solution,
                                                       List<DoubleSolution> solutionList){
        //At start point the distance is the max
        double distance = Double.MAX_VALUE;

        // found the min distance respect to population
        for (int i = 0; i < solutionList.size();i++){
            double aux = distanceBetweenSolutions(solution,solutionList.get(i));
            if (aux < distance)
                distance = aux;
        } // for

        //->Return the best distance
        return distance;
    } // distanceToSolutionSetInSolutionSpace

    /** Returns the distance between two solutions in the search space.
     *  @param solutionI The first <code>Solution</code>.
     *  @param solutionJ The second <code>Solution</code>.
     *  @return the distance between solutions.
     */
    public static double distanceBetweenSolutions(DoubleSolution solutionI, DoubleSolution solutionJ) {
        double distance = 0.0;

        double diff;    //Auxiliar var
        //-> Calculate the Euclidean distance
        for (int i = 0; i < solutionI.getNumberOfVariables() ; i++){
            diff = solutionI.getVariableValue(i) - solutionJ.getVariableValue(i);
            distance += Math.pow(diff,2.0);
        } // for
        //-> Return the euclidean distance
        return Math.sqrt(distance);
    } // distanceBetweenSolutions


}
