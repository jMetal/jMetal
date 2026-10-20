package org.uma.jmetal.util.archive.impl;

import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;

/**
 * @author Antonio J. Nebro
 * @param <S>
 */
public abstract class AbstractBoundedArchive<S extends Solution<?>> implements BoundedArchive<S> {
  protected NonDominatedSolutionListArchive<S> archive;
  protected int maxSize;

  protected AbstractBoundedArchive(int maxSize, DominanceComparator<S> dominanceComparator) {
    this.maxSize = maxSize;
    this.archive = new NonDominatedSolutionListArchive<>(dominanceComparator);
  }

  /**
   * Creates the archive with Deb's constrained-domination principle, as {@link
   * NonDominatedSolutionListArchive} does by default, so that an infeasible solution never replaces
   * a feasible one; on problems without constraints it is plain Pareto dominance.
   *
   * @param maxSize the maximum number of solutions of the archive
   */
  protected AbstractBoundedArchive(int maxSize) {
    this(maxSize, new DominanceWithConstraintsComparator<>()) ;
  }

  @Override
  public boolean add(S solution) {
    boolean success = archive.add(solution);
    if (success) {
      prune();
    }

    return success;
  }

  @Override
  public S get(int index) {
    return solutions().get(index);
  }

  @Override
  public List<S> solutions() {
    return archive.solutions();
  }

  @Override
  public int size() {
    return archive.size();
  }

  @Override
  public int maximumSize() {
    return maxSize;
  }

  public abstract void prune();

  public Archive<S> join(Archive<S> archive) {
    archive.solutions().forEach(this::add);

    return archive;
  }
}
