package org.uma.jmetal.qualityindicator;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.SolutionComparator;

/**
 * Calculates the coverage for two given fronts.
 * The coverage indicator gives for a pair (A, B) of approximation sets
 * the fraction of solutions in B that are weakly dominated by one or
 * more solutions in A.
 * <br/>
 * <br/>
 * For more info, please refer to:
 * <br/>
 * <br/>
 * <a href="http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=1197687&isnumber=26949">
 * Zitzler, E.; Thiele, L.; Laumanns, M.; Fonseca, C.M.; da Fonseca, V.G., "Performance assessment of multiobjective optimizers: an analysis and review," Evolutionary Computation, IEEE Transactions on , vol.7, no.2, pp.117,132, April 2003</a>
 */
public class Coverage {

  private final DominanceComparator dominanceComparator;
  private final SolutionComparator equalSolutions;

  /**
   * Class constructor.
   */
  public Coverage() {
    dominanceComparator = new DominanceComparator();
    equalSolutions = new SolutionComparator();
  }

  /**
   * Calculates the coverage for two given fronts.
   * It calculates the percentage (interval [0, 1]) of solutions from
   * <i>frontB</i> which are dominated or equal to the solutions of <i>frontA</i>.
   *
   * @param frontA A solution set A;
   * @param frontB A solution set B;
   * @return the coverage percentage of solutions from frontB which are dominated
   * or equal to solutions from frontA. Interval of [0, 1].
   */
  public double coverage(SolutionSet frontA, SolutionSet frontB) {
    double coverage = 0;
    for (Solution solutionB : frontB.getSolutionsList()) {
      for (Solution solutionA : frontA.getSolutionsList()) {
        if (dominanceComparator.compare(solutionA, solutionB) == -1
          || equalSolutions.compare(solutionA, solutionB) == 0) {
          coverage++;
          break;
        }
      }
    }
    return coverage / frontB.size();
  }
}
