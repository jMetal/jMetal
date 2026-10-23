package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.DoubleSupplier;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.constrainthandling.ConstraintHandlingTechnique;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * A replacement that, before replacing, updates a constraint-handling technique with the solutions
 * about to be ranked and the progress of the run, so that a technique that changes during the run
 * is updated once per generation. In a steady-state algorithm, which replaces once per offspring,
 * the technique is updated once every {@code period} replacements (the size of the population), so
 * that it changes at the same pace as in a generational one.
 *
 * @param <S> the type of the solutions
 */
public class ConstraintHandlingReplacement<S extends Solution<?>> implements Replacement<S> {
  private final ConstraintHandlingTechnique<S> technique;
  private final Replacement<S> replacement;
  private final DoubleSupplier progress;
  private final int period;
  private final BiFunction<List<S>, List<S>, List<S>> candidates;
  private long replacements;

  /**
   * Creates a replacement that updates the technique at every replacement, with the population and
   * the offspring.
   *
   * @param technique the constraint-handling technique
   * @param replacement the replacement of the algorithm
   * @param progress the progress of the run
   */
  public ConstraintHandlingReplacement(
      ConstraintHandlingTechnique<S> technique,
      Replacement<S> replacement,
      DoubleSupplier progress) {
    this(technique, replacement, progress, 1);
  }

  /**
   * Creates a replacement that updates the technique once every {@code period} replacements, with
   * the population and the offspring.
   *
   * @param technique the constraint-handling technique
   * @param replacement the replacement of the algorithm
   * @param progress the progress of the run
   * @param period the number of replacements between two updates of the technique
   */
  public ConstraintHandlingReplacement(
      ConstraintHandlingTechnique<S> technique,
      Replacement<S> replacement,
      DoubleSupplier progress,
      int period) {
    this(technique, replacement, progress, period, ConstraintHandlingReplacement::union);
  }

  /**
   * Creates a replacement that updates the technique once every {@code period} replacements, with
   * the solutions that a function chooses from the population and the offspring.
   *
   * @param technique the constraint-handling technique
   * @param replacement the replacement of the algorithm
   * @param progress the progress of the run
   * @param period the number of replacements between two updates of the technique
   * @param candidates the solutions the technique is updated with, given the population and the
   *     offspring
   */
  public ConstraintHandlingReplacement(
      ConstraintHandlingTechnique<S> technique,
      Replacement<S> replacement,
      DoubleSupplier progress,
      int period,
      BiFunction<List<S>, List<S>, List<S>> candidates) {
    Check.notNull(technique);
    Check.notNull(replacement);
    Check.notNull(progress);
    Check.notNull(candidates);
    Check.valueIsPositive(period, "period");
    this.technique = technique;
    this.replacement = replacement;
    this.progress = progress;
    this.period = period;
    this.candidates = candidates;
  }

  @Override
  public List<S> replace(List<S> population, List<S> offspringPopulation) {
    if (replacements % period == 0) {
      technique.update(candidates.apply(population, offspringPopulation), progress.getAsDouble());
    }
    replacements++;
    return replacement.replace(population, offspringPopulation);
  }

  /** The constraint-handling technique. */
  public ConstraintHandlingTechnique<S> technique() {
    return technique;
  }

  private static <S> List<S> union(List<S> population, List<S> offspringPopulation) {
    List<S> union = new ArrayList<>(population.size() + offspringPopulation.size());
    union.addAll(population);
    union.addAll(offspringPopulation);
    return union;
  }
}
