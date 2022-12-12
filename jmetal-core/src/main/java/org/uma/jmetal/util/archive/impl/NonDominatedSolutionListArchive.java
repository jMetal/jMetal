package org.uma.jmetal.util.archive.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.EqualSolutionsComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;

/**
 * This class implements an archive containing non-dominated solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class NonDominatedSolutionListArchive<S extends Solution<?>> implements Archive<S> {

  private List<S> solutionList;
  private Comparator<S> dominanceComparator;
  private Comparator<S> equalSolutions = new EqualSolutionsComparator<S>();

  /**
   * Constructor
   */
  public NonDominatedSolutionListArchive() {
    this(new DominanceWithConstraintsComparator<S>());
  }

  /**
   * Constructor
   */
  public NonDominatedSolutionListArchive(Comparator<S> comparator) {
    dominanceComparator = comparator;

    solutionList = new ArrayList<>();
  }

  /**
   * Inserts a solution in the list
   *
   * @param solution The solution to be inserted.
   * @return true if the operation success, and false if the solution is dominated or if an
   * identical individual exists. The decision variables can be null if the solution is read from a
   * file; in that case, the domination tests are omitted
   */
  @Override
  public boolean add(S solution) {
    boolean isSolutionInserted = false;
    if (solutionList.isEmpty()) {
      solutionList.add(solution);
      isSolutionInserted = true;
    } else {
      isSolutionInserted = insertSolutionIfNonDominatedAndIsNotInTheArchive(solution,
          isSolutionInserted);
    }

    return isSolutionInserted;
  }

  private boolean insertSolutionIfNonDominatedAndIsNotInTheArchive(S solution,
      boolean solutionInserted) {
    boolean isDominated = false;
    boolean isContained = false;
    Iterator<S> iterator = solutionList.iterator();
    while (((!isDominated) && (!isContained)) && (iterator.hasNext())) {
      S listIndividual = iterator.next();
      int flag = dominanceComparator.compare(solution, listIndividual);
      if (flag == -1) {
        iterator.remove();
      } else if (flag == 1) {
        isDominated = true; // dominated by one in the list
      } else if (equalSolutions.compare(solution, listIndividual) == 0) {// solutions are equals
        isContained = true;
      }
    }

    if (!isDominated && !isContained) {
      solutionList.add(solution);
      solutionInserted = true;
    }
    return solutionInserted;
  }

  public Archive<S> join(Archive<S> archive) {
    return this.addAll(archive.solutions());
  }

  public Archive<S> addAll(List<S> list) {
    for (S solution : list) {
      this.add(solution);
    }

    return this;
  }


  @Override
  public List<S> solutions() {
    return solutionList;
  }

  @Override
  public int size() {
    return solutionList.size();
  }

  @Override
  public S get(int index) {
    return solutionList.get(index);
  }
}
