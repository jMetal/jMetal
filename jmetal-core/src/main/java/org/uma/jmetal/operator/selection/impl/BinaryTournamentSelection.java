package org.uma.jmetal.operator.selection.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.Comparator;

/**
 * Applies a binary tournament selection to return the best solution between two that have been
 * chosen at random from a solution list.
 * Modified by Juanjo in 13.03.2015. A binary tournament is now a TournamenteSelection with 2 
 * tournaments
 *
 * @author Antonio J. Nebro, Juan J. Durillo
 */
@SuppressWarnings("serial")
public class BinaryTournamentSelection<S extends Solution<?>> extends NaryTournamentSelection<S> {
    /** Constructor */
  public BinaryTournamentSelection() {
    super(2, new DominanceComparator<S>()) ;
  }

  /** Constructor */
  public BinaryTournamentSelection(Comparator<S> comparator) {
    super(2, comparator);
  }
}
