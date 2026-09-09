package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

/**
 * RVEA selection using angle-penalized distance (APD) and periodic range adaptation.
 *
 * <p>Implements Cheng et al., IEEE TEVC (2016), doi:10.1109/TEVC.2016.2519378. Empty niches
 * contribute no survivor, so the population can shrink. This component is stateful; create a new
 * instance for each run. Objectives must be finite and minimized, without constraints.
 *
 * @param <S> solution type
 */
public class RVEAEnvironmentalSelection<S extends Solution<?>> {
  protected final int numberOfObjectives;
  protected final int maxGenerations;
  protected final double[][] initialReferenceVectors;
  protected double[][] referenceVectors;
  private final double alpha;
  private final int adaptationFrequency;
  private int currentGeneration = -1;

  public RVEAEnvironmentalSelection(
      int objectives, int maxGenerations, double alpha, double fr, int divisions) {
    this(
        objectives,
        maxGenerations,
        alpha,
        fr,
        ReferencePointGenerator.generateSingleLayer(objectives, divisions));
  }

  public RVEAEnvironmentalSelection(
      int objectives, int maxGenerations, double alpha, double fr, List<double[]> vectors) {
    Check.that(objectives >= 2, "At least two objectives are required");
    Check.that(maxGenerations > 0, "The maximum generation count must be positive");
    Check.that(Double.isFinite(alpha) && alpha >= 0.0, "Alpha must be finite and nonnegative");
    Check.that(Double.isFinite(fr) && fr > 0.0 && fr <= 1.0, "Frequency must be in (0, 1]");
    this.numberOfObjectives = objectives;
    this.maxGenerations = maxGenerations;
    this.alpha = alpha;
    this.adaptationFrequency = Math.max(1, (int) Math.ceil(maxGenerations * fr));
    this.referenceVectors = RVEAGeometry.directions(vectors, objectives);
    this.initialReferenceVectors = RVEAGeometry.copy(referenceVectors);
  }

  /**
   * Selects the next population. The size argument is the configured reference-vector count,
   * independent of the number of parents that survived the preceding generation.
   */
  public final List<S> execute(List<S> jointPopulation, int populationSize) {
    validatePopulation(jointPopulation);
    Check.that(
        populationSize == initialReferenceVectors.length,
        "Population size must match the number of predefined reference vectors");
    currentGeneration++;
    List<S> survivors = select(jointPopulation);
    if (currentGeneration % adaptationFrequency == 0) {
      adaptReferenceVectors(survivors);
    }
    afterSelection(survivors);
    return finish(survivors);
  }

  private void validatePopulation(List<S> population) {
    Check.notNull(population);
    Check.that(!population.isEmpty(), "The candidate population must not be empty");
    for (S solution : population) {
      Check.notNull(solution);
      Check.that(solution.constraints().length == 0, "RVEA supports unconstrained solutions only");
      Check.that(
          solution.objectives().length == numberOfObjectives,
          "Solution objective count must match the reference vectors");
      Check.that(
          Arrays.stream(solution.objectives()).allMatch(Double::isFinite),
          "Objective values must be finite");
    }
  }

  protected List<S> select(List<S> population) {
    return selectByApd(population, referenceVectors);
  }

  protected final List<S> selectByApd(List<S> population, double[][] vectors) {
    double[][] translated = RVEAGeometry.translate(population);
    double[] gamma = RVEAGeometry.gamma(vectors);
    double[] bestApd = new double[vectors.length];
    int[] best = new int[vectors.length];
    Arrays.fill(bestApd, Double.POSITIVE_INFINITY);
    Arrays.fill(best, -1);
    double progress = Math.pow(Math.min(1.0, (double) currentGeneration / maxGenerations), alpha);
    for (int i = 0; i < population.size(); i++) {
      int niche = RVEAGeometry.nearest(translated[i], vectors);
      double penalty =
          numberOfObjectives
              * progress
              * RVEAGeometry.angle(translated[i], vectors[niche])
              / gamma[niche];
      double apd = (1.0 + penalty) * RVEAGeometry.norm(translated[i]);
      if (best[niche] < 0 || apd < bestApd[niche]) {
        bestApd[niche] = apd;
        best[niche] = i;
      }
    }
    List<S> selected = new ArrayList<>();
    for (int index : best) {
      if (index >= 0) {
        selected.add(population.get(index));
      }
    }
    return selected;
  }

  protected final List<S> nondominated(List<S> population) {
    return new FastNonDominatedSortRanking<S>().compute(population).getSubFront(0);
  }

  private void adaptReferenceVectors(List<S> survivors) {
    double[] ranges = RVEAGeometry.ranges(RVEAGeometry.translate(survivors));
    for (int i = 0; i < referenceVectors.length; i++) {
      double[] scaled = new double[numberOfObjectives];
      for (int j = 0; j < numberOfObjectives; j++) {
        scaled[j] = initialReferenceVectors[i][j] * ranges[j];
      }
      // A collapsed objective range cannot define a direction: retain the preceding vector.
      if (RVEAGeometry.norm(scaled) > 0.0) {
        referenceVectors[i] = RVEAGeometry.unit(scaled);
      }
    }
  }

  protected void afterSelection(List<S> survivors) {}

  protected List<S> finish(List<S> survivors) {
    return survivors;
  }

  public final int populationSize() {
    return initialReferenceVectors.length;
  }

  /** Returns the zero-based index of the last completed selection, or -1 before the first call. */
  public final int currentGeneration() {
    return currentGeneration;
  }

  /** Returns a defensive copy of the current predefined vectors. */
  public final double[][] referenceVectors() {
    return RVEAGeometry.copy(referenceVectors);
  }
}
