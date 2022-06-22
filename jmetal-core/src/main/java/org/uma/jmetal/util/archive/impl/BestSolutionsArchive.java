package org.uma.jmetal.util.archive.impl;

import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.Archive;

/**
 * Archive that select the best solutions of another archive by applying the
 * {@link SolutionListUtils#distanceBasedSubsetSelection(List, int)}} function.
 *
 * @param <S> Existing archive
 */
public class BestSolutionsArchive<S extends Solution<?>> implements Archive<S> {
  private Archive<S> archive ;
  private int numberOfSolutionsToSelect ;

  public BestSolutionsArchive(Archive<S> archive, int numberOfSolutionsToSelect) {
    this.archive = archive ;
    this.numberOfSolutionsToSelect = numberOfSolutionsToSelect ;
  }

  @Override
  public boolean add(S solution) {
    return archive.add(solution);
  }

  @Override
  public S get(int index) {
    return archive.get(index);
  }

  @Override
  public List<S> getSolutionList() {
    return SolutionListUtils.distanceBasedSubsetSelection(archive.getSolutionList(), numberOfSolutionsToSelect);
  }

  @Override
  public int size() {
    return archive.size();
  }
}
