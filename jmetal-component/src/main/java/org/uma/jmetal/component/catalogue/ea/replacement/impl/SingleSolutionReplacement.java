package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

/**
 * Given an offspring population composed of a single solution, this solution is compared against a particular solution
 * of the population given by a {@link SequenceGenerator} object. If this last solution is replaced by the former if
 * it is worse according to a {@link Comparator}.
 *
 * Once the replacement is carried out, the next sequence number is generated.
 *
 * @param <S>
 */
public class SingleSolutionReplacement<S extends Solution<?>> implements Replacement<S> {
  private final SequenceGenerator<Integer> sequenceGenerator;
  private final Comparator<S> comparator ;

  public SingleSolutionReplacement(SequenceGenerator<Integer> sequenceGenerator, Comparator<S>  comparator) {
    this.sequenceGenerator = sequenceGenerator;
    this.comparator = comparator ;
  }

  @Override
  public List<S> replace(
      List<S> population, List<S> offspringPopulation) {
    S newSolution = offspringPopulation.get(0);

    if (comparator.compare(population.get(sequenceGenerator.getValue()), newSolution) > 0) {
      population.set(sequenceGenerator.getValue(), newSolution) ;
    }

    sequenceGenerator.generateNext();
    
    return population;
  }
}
