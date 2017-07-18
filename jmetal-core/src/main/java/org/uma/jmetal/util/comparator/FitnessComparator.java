package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.impl.Fitness;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on the fitness value returned by the
 * method <code>getFitness</code>.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class FitnessComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
  private Fitness<S> solutionFitness = new Fitness<S>() ;

  /**
   * Compares two solutions.
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
   * respectively.
   */
  @Override
  public int compare(S solution1, S solution2) {
    if (solution1 == null) {
      return 1;
    } else if (solution2 == null) {
      return -1;
    }

    double fitness1 = solutionFitness.getAttribute(solution1);
    double fitness2 = solutionFitness.getAttribute(solution2);
    if (fitness1 < fitness2) {
      return -1;
    }

    if (fitness1 > fitness2) {
      return 1;
    }

    return 0;
  }
}
