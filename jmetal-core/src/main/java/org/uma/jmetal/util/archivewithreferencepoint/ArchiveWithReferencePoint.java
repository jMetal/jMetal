package org.uma.jmetal.util.archivewithreferencepoint;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.Comparator;
import java.util.List;

/**
 * This class defines a bounded archive that has associated a reference point as described in the paper
 * "Extending the Speed-constrained Multi-Objective PSO (SMPSO) With Reference Point Based Preference Articulation
 * Accepted in PPSN 2018.
 *
 * @param <S>
 */
public abstract class ArchiveWithReferencePoint <S extends Solution<?>> extends AbstractBoundedArchive<S> {
  protected List<Double> referencePoint ;
  protected S referencePointSolution ;
  protected Comparator<S> comparator ;

  public ArchiveWithReferencePoint(
      int maxSize,
      List<Double> referencePoint,
      Comparator<S> comparator) {
    super(maxSize);
    this.referencePoint = referencePoint ;
    this.comparator = comparator ;
    this.referencePointSolution = null ;
  }

  @Override
  public synchronized boolean add(S solution) {
    boolean result ;

    if (referencePointSolution == null) {
      referencePointSolution = (S) solution.copy();
      for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
        referencePointSolution.setObjective(i, this.referencePoint.get(i));
      }
    }

    S dominatedSolution = null ;

    if (dominanceTest(solution, referencePointSolution) == 0) {
      if (getSolutionList().size() == 0) {
        result = true ;
      } else {
        if (JMetalRandom.getInstance().nextDouble() < 0.05) {
          result = true ;
          dominatedSolution = solution ;
        } else {
          result = false;
        }
      }
    } else {
      result = true;
    }

    if (result) {
      result = super.add(solution);
    }

    if (result && (dominatedSolution != null) && (getSolutionList().size() > 1)) {
      getSolutionList().remove(dominatedSolution) ;
    }

    return result;
  }

  @Override
  public synchronized void prune() {
    if (getSolutionList().size() > getMaxSize()) {

      computeDensityEstimator();

      S worst = new SolutionListUtils().findWorstSolution(getSolutionList(), comparator);
      getSolutionList().remove(worst);
    }
  }

  public synchronized void changeReferencePoint(List<Double> newReferencePoint) {
    this.referencePoint = newReferencePoint ;
    for (int i = 0; i < referencePoint.size(); i++) {
      referencePointSolution.setObjective(i, this.referencePoint.get(i));
    }

    int i = 0 ;
    while (i < getSolutionList().size()) {
      if (dominanceTest(getSolutionList().get(i), referencePointSolution) == 0) {
        getSolutionList().remove(i) ;
      } else {
        i++;
      }
    }

    referencePointSolution = null ;
  }

  private int dominanceTest(S solution1, S solution2) {
    int bestIsOne = 0 ;
    int bestIsTwo = 0 ;
    int result ;
    for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
      double value1 = solution1.getObjective(i);
      double value2 = solution2.getObjective(i);
      if (value1 != value2) {
        if (value1 < value2) {
          bestIsOne = 1;
        }
        if (value2 < value1) {
          bestIsTwo = 1;
        }
      }
    }
    if (bestIsOne > bestIsTwo) {
      result = -1;
    } else if (bestIsTwo > bestIsOne) {
      result = 1;
    } else {
      result = 0;
    }
    return result ;
  }
}
