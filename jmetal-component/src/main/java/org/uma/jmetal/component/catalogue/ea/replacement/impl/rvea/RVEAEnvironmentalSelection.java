package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

/**
 * RVEA selection using angle-penalized distance (APD) and periodic range adaptation.
 *
 * <p>Implements Cheng et al., IEEE TEVC (2016), doi:10.1109/TEVC.2016.2519378. Empty niches
 * contribute no survivor, so the population can shrink. This component is stateful; create a new
 * instance for each run. Objectives must be minimized. Candidates with a non-finite objective value
 * (for instance, an infinite constraint violation used as an objective) are discarded before the
 * selection, since the angles and distances of the APD are not defined for them and they are worse
 * than any finite candidate in that objective; at least one candidate must be finite.
 *
 * <p>Constraints are handled as in C-RVEA (Cheng et al., 2016, Section VII, Algorithm 5), which is
 * also the RVEA of PlatEMO: in each niche, the feasible candidate with the lowest APD survives or,
 * if the niche has no feasible candidate, the one with the lowest overall constraint violation. The
 * translation and the association to the vectors use every candidate. Without constraints, or when
 * every candidate is feasible, this is the original selection.
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
    List<S> finite = jointPopulation.stream().filter(RVEAEnvironmentalSelection::hasFiniteObjectives).toList();
    Check.that(!finite.isEmpty(), "At least one candidate must have finite objective values");
    currentGeneration++;
    return selectNextPopulation(finite.size() == jointPopulation.size() ? jointPopulation : finite);
  }

  private static boolean hasFiniteObjectives(Solution<?> solution) {
    return Arrays.stream(solution.objectives()).allMatch(Double::isFinite);
  }

  /** Selects the survivors of a generation; subclasses may add their own constraint handling. */
  protected List<S> selectNextPopulation(List<S> population) {
    return selectAndUpdate(population);
  }

  /** Selects the survivors and updates the vectors and the state of the variant. */
  protected final List<S> selectAndUpdate(List<S> population) {
    List<S> survivors = select(population);
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
      Check.that(
          Arrays.stream(solution.constraints()).allMatch(Double::isFinite),
          "Constraint values must be finite");
      Check.that(
          solution.objectives().length == numberOfObjectives,
          "Solution objective count must match the reference vectors");
    }
  }

  protected List<S> select(List<S> population) {
    return selectByApd(population, referenceVectors);
  }

  /**
   * Keeps one candidate per niche: the feasible one with the lowest APD or, if the niche has no
   * feasible candidate, the one with the lowest overall constraint violation (C-RVEA). Ties keep
   * the first candidate.
   */
  protected final List<S> selectByApd(List<S> population, double[][] vectors) {
    double[][] translated = RVEAGeometry.translate(population);
    double[] gamma = RVEAGeometry.gamma(vectors);
    double[] bestApd = new double[vectors.length];
    int[] bestFeasible = new int[vectors.length];
    double[] bestViolation = new double[vectors.length];
    int[] bestInfeasible = new int[vectors.length];
    Arrays.fill(bestApd, Double.POSITIVE_INFINITY);
    Arrays.fill(bestFeasible, -1);
    Arrays.fill(bestViolation, Double.NEGATIVE_INFINITY);
    Arrays.fill(bestInfeasible, -1);
    double progress = Math.pow(Math.min(1.0, (double) currentGeneration / maxGenerations), alpha);
    for (int i = 0; i < population.size(); i++) {
      int niche = RVEAGeometry.nearest(translated[i], vectors);
      S solution = population.get(i);
      if (ConstraintHandling.isFeasible(solution)) {
        double penalty =
            numberOfObjectives
                * progress
                * RVEAGeometry.angle(translated[i], vectors[niche])
                / gamma[niche];
        double apd = (1.0 + penalty) * RVEAGeometry.norm(translated[i]);
        if (bestFeasible[niche] < 0 || apd < bestApd[niche]) {
          bestApd[niche] = apd;
          bestFeasible[niche] = i;
        }
      } else {
        // The overall constraint violation degree is negative: the higher, the better
        double violation = ConstraintHandling.overallConstraintViolationDegree(solution);
        if (bestInfeasible[niche] < 0 || violation > bestViolation[niche]) {
          bestViolation[niche] = violation;
          bestInfeasible[niche] = i;
        }
      }
    }
    List<S> selected = new ArrayList<>();
    for (int niche = 0; niche < vectors.length; niche++) {
      int index = bestFeasible[niche] >= 0 ? bestFeasible[niche] : bestInfeasible[niche];
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
