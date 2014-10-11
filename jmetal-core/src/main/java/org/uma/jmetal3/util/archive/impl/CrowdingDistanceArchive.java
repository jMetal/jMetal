package org.uma.jmetal3.util.archive.impl;

import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.util.SolutionListUtils;
import org.uma.jmetal3.util.archive.Archive;
import org.uma.jmetal3.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal3.util.comparator.DominanceComparator;
import org.uma.jmetal3.util.comparator.EqualSolutionsComparator;
import org.uma.jmetal3.util.solutionattribute.DensityEstimator;
import org.uma.jmetal3.util.solutionattribute.impl.CrowdingDistance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 */
public class CrowdingDistanceArchive implements Archive {
  private int maxSize ;
  private List<Solution<?>> solutionSet;
  private Comparator<Solution<?>> dominanceComparator;
  private Comparator<Solution<?>> equalsComparator;
  private Comparator<Solution<?>> crowdingDistanceComparator;
  private DensityEstimator crowdingDistance ;

  public CrowdingDistanceArchive(int maxSize) {
    this.maxSize = maxSize ;
    solutionSet = new ArrayList<>(maxSize + 1) ;
    dominanceComparator = new DominanceComparator();
    crowdingDistanceComparator = new CrowdingDistanceComparator() ;
    crowdingDistance = new CrowdingDistance() ;
    equalsComparator = new EqualSolutionsComparator() ;
  }

  @Override
  public boolean add(Solution<?> solution) {
    int flag ;
    int i = 0;
    Solution aux;
    while (i < solutionSet.size()) {
      aux = solutionSet.get(i);

      flag = dominanceComparator.compare(solution, aux);
      if (flag == 1) {
        return false;
      } else if (flag == -1) {
        solutionSet.remove(i);
      } else {
        if (equalsComparator.compare(aux, solution) == 0) {
          return false;
        }
      }
      i++;
    }

    solutionSet.add(solution);
    if (solutionSet.size() > maxSize) { // FIXME: check whether the removed solution is the inserted one
      crowdingDistance.computeDensityEstimator(solutionSet);
      int index = SolutionListUtils.findWorstSolution(solutionSet, crowdingDistanceComparator) ;
      solutionSet.remove(index);
    }
    return true;
  }

  @Override
  public List<Solution<?>> getSolutionList() {
    return solutionSet ;
  }

  @Override
  public int getMaxSize() {
    return maxSize;
  }

  public void computeDistance() {
    crowdingDistance.computeDensityEstimator(solutionSet);
  }
}
