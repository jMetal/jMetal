package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

/**
 * RVEA* with an additional vector set and regeneration of its inactive directions.
 *
 * <p>Cheng et al. (2016), Section VI and Algorithm 4. Selection can retain up to 2N solutions;
 * angular truncation limits the final population to N. Also known as RVEAa in PlatEMO.
 *
 * @param <S> solution type
 */
public class RVEAStarEnvironmentalSelection<S extends Solution<?>>
    extends RVEAEnvironmentalSelection<S> {
  protected final double[][] adaptiveVectors;

  public RVEAStarEnvironmentalSelection(
      int objectives, int maxGenerations, double alpha, double fr, int divisions) {
    this(
        objectives,
        maxGenerations,
        alpha,
        fr,
        ReferencePointGenerator.generateSingleLayer(objectives, divisions));
  }

  public RVEAStarEnvironmentalSelection(
      int objectives, int maxGenerations, double alpha, double fr, List<double[]> vectors) {
    super(objectives, maxGenerations, alpha, fr, vectors);
    this.adaptiveVectors = new double[populationSize()][objectives];
    double[] ranges = new double[objectives];
    Arrays.fill(ranges, 1.0);
    for (int i = 0; i < adaptiveVectors.length; i++) {
      adaptiveVectors[i] = randomDirection(ranges, referenceVectors[i]);
    }
  }

  @Override
  protected List<S> select(List<S> population) {
    return selectByApd(nondominated(population), allVectors());
  }

  protected final double[][] allVectors() {
    return Stream.concat(Arrays.stream(referenceVectors), Arrays.stream(adaptiveVectors))
        .toArray(double[][]::new);
  }

  @Override
  protected double[][] selectionReferenceVectors() {
    return allVectors();
  }

  @Override
  protected int survivorCapacity() {
    return currentGeneration() + 1 >= maxGenerations ? populationSize() : 2 * populationSize();
  }

  @Override
  protected void afterSelection(List<S> survivors) {
    double[][] points = RVEAGeometry.translate(nondominated(survivors));
    boolean[] active = RVEAGeometry.active(points, adaptiveVectors);
    double[] ranges = RVEAGeometry.ranges(points);
    for (int i = 0; i < adaptiveVectors.length; i++) {
      if (!active[i]) {
        adaptiveVectors[i] = randomDirection(ranges, adaptiveVectors[i]);
      }
    }
  }

  private double[] randomDirection(double[] ranges, double[] fallback) {
    double[] vector = new double[numberOfObjectives];
    for (int j = 0; j < numberOfObjectives; j++) {
      vector[j] = JMetalRandom.getInstance().nextDouble() * ranges[j];
    }
    return RVEAGeometry.norm(vector) == 0.0 ? fallback.clone() : RVEAGeometry.unit(vector);
  }

  @Override
  protected List<S> finish(List<S> survivors) {
    return currentGeneration() + 1 >= maxGenerations
        ? RVEATruncation.crowded(survivors, populationSize())
        : survivors;
  }

  /** Returns a defensive copy of the additional, regenerating reference-vector set. */
  public final double[][] adaptiveReferenceVectors() {
    return RVEAGeometry.copy(adaptiveVectors);
  }
}
