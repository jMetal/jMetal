package org.uma.jmetal.util.archive.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AdaptiveGrid;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.GridDensityEstimator;

import java.util.Comparator;
import java.util.Iterator;

/**
 * This class implements an archive (solution list) based on an adaptive grid used in PAES
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class AdaptiveGridArchiveV2<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private Comparator<S> comparator;

  private DensityEstimator<S> densityEstimator;

  /**
   * Constructor.
   *
   * @param maxSize    The maximum size of the setArchive
   * @param bisections The maximum number of bi-divisions for the adaptive
   *                   grid.
   * @param objectives The number of objectives.
   */
  public AdaptiveGridArchiveV2(int maxSize, int bisections, int objectives) {
    super(maxSize);
    densityEstimator = new GridDensityEstimator<S>(bisections, objectives);
    comparator = Comparator.comparing(densityEstimator::getValue);
  }

  /**
   * Adds a <code>Solution</code> to the setArchive. If the <code>Solution</code>
   * is dominated by any member of the setArchive then it is discarded. If the
   * <code>Solution</code> dominates some members of the setArchive, these are
   * removed. If the setArchive is full and the <code>Solution</code> has to be
   * inserted, one <code>Solution</code> of the most populated hypercube of the
   * adaptive grid is removed.
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   */
  @Override
  public boolean add(S solution) {
    boolean added = super.add(solution) ;
    if (added) {
      computeDensityEstimator();
    }

    return added;
  }

  public void prune() {
    if (getSolutionList().size() > getMaxSize()) {
      computeDensityEstimator() ;
      S worst = new SolutionListUtils().findWorstSolution(getSolutionList(), comparator) ;
      getSolutionList().remove(worst);
    }
  }

  @Override
  public Comparator<S> getComparator() {
    return comparator ;
  }

  @Override
  public void computeDensityEstimator() {
    densityEstimator.compute(archive.getSolutionList());
  }
}
