package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

/**
 * iRVEA of Liu et al., CEC 2019, doi:10.1109/CEC.2019.8790214.
 *
 * <p>Combines one-at-a-time vector replacement, regional protection for two/three objectives, APD,
 * Pareto/strengthened dominance, angular clustering, and an epsilon-indicator archive. See {@code
 * docs/rvea-variants.md} for choices resolving underspecified steps in the paper.
 *
 * @param <S> solution type
 */
public class IRVEAEnvironmentalSelection<S extends Solution<?>>
    extends RVEAStarEnvironmentalSelection<S> {
  private final double[][] regionVectors;
  private List<S> archive = List.of();

  public IRVEAEnvironmentalSelection(
      int objectives, int maxGenerations, double alpha, double fr, int divisions) {
    this(
        objectives,
        maxGenerations,
        alpha,
        fr,
        ReferencePointGenerator.generateSingleLayer(objectives, divisions),
        40);
  }

  public IRVEAEnvironmentalSelection(
      int objectives, int maxGenerations, double alpha, double fr, List<double[]> vectors) {
    this(objectives, maxGenerations, alpha, fr, vectors, 40);
  }

  public IRVEAEnvironmentalSelection(
      int objectives,
      int maxGenerations,
      double alpha,
      double fr,
      List<double[]> vectors,
      int numberOfSubregions) {
    super(objectives, maxGenerations, alpha, fr, vectors);
    Check.that(numberOfSubregions > 0, "The number of subregions must be positive");
    regionVectors =
        objectives <= 3
            ? coarseVectors(objectives, Math.min(numberOfSubregions, populationSize()))
            : new double[0][];
  }

  @Override
  protected List<S> select(List<S> population) {
    replaceInactiveVector(population);
    List<S> front = nondominated(population);
    List<S> protectedSolutions = protectRegions(population, front);
    List<S> candidates = union(front, protectedSolutions);
    List<S> apd = selectByApd(candidates, allVectors());
    List<S> selected;
    if (apd.size() < populationSize() && currentGeneration() > 0.8 * maxGenerations) {
      List<S> remaining = candidates.stream().filter(solution -> !apd.contains(solution)).toList();
      selected =
          union(apd, EpsilonIndicatorSelection.select(remaining, populationSize() - apd.size()));
    } else if (apd.size() > populationSize()) {
      selected = reduceWithProtection(apd, protectedSolutions, true);
    } else {
      List<S> combined = union(apd, archive);
      selected = secondarySelection(combined);
    }
    return reduceWithProtection(selected, protectedSolutions, false);
  }

  List<S> secondarySelection(List<S> population) {
    return numberOfObjectives <= 6
        ? nondominated(population)
        : StrengthenedDominanceSelection.firstFront(population);
  }

  /** Replaces only an inactive adaptive vector, using the largest nearest-active-vector angle. */
  void replaceInactiveVector(List<S> population) {
    double[][] points = RVEAGeometry.translate(population);
    double[][] vectors = allVectors();
    boolean[] active = RVEAGeometry.active(points, vectors);
    int offset = populationSize();
    int[] inactive =
        IntStream.range(0, adaptiveVectors.length).filter(i -> !active[offset + i]).toArray();
    if (inactive.length == 0) {
      return;
    }
    double[][] activeVectors =
        IntStream.range(0, vectors.length)
            .filter(i -> active[i])
            .mapToObj(i -> vectors[i])
            .toArray(double[][]::new);
    double biggestAngle = -1.0;
    int leastSimilar = -1;
    for (int i = 0; i < points.length; i++) {
      if (RVEAGeometry.norm(points[i]) > 0.0) {
        int nearest = RVEAGeometry.nearest(points[i], activeVectors);
        double angle = RVEAGeometry.angle(points[i], activeVectors[nearest]);
        if (angle > biggestAngle) {
          biggestAngle = angle;
          leastSimilar = i;
        }
      }
    }
    if (leastSimilar >= 0 && biggestAngle > RVEAGeometry.MIN_ANGLE) {
      int index = inactive[JMetalRandom.getInstance().nextInt(0, inactive.length - 1)];
      adaptiveVectors[index] = RVEAGeometry.unit(points[leastSimilar]);
    }
  }

  List<S> protectRegions(List<S> population, List<S> front) {
    if (regionVectors.length == 0) {
      return List.of();
    }
    double[][] points = RVEAGeometry.normalize(RVEAGeometry.translate(population));
    int[] nearest = new int[regionVectors.length];
    Arrays.fill(nearest, -1);
    boolean[] represented = new boolean[regionVectors.length];
    for (int i = 0; i < points.length; i++) {
      int region = RVEAGeometry.nearest(points[i], regionVectors);
      represented[region] |= front.contains(population.get(i));
      if (nearest[region] < 0
          || RVEAGeometry.norm(points[i]) < RVEAGeometry.norm(points[nearest[region]])) {
        nearest[region] = i;
      }
    }
    return IntStream.range(0, nearest.length)
        .filter(i -> !represented[i] && nearest[i] >= 0)
        .mapToObj(i -> population.get(nearest[i]))
        .toList();
  }

  private List<S> reduceWithProtection(
      List<S> selected, List<S> protectedSolutions, boolean cluster) {
    List<S> protectedSubset = RVEATruncation.crowded(protectedSolutions, populationSize());
    List<S> remaining =
        selected.stream().filter(solution -> !protectedSubset.contains(solution)).toList();
    int capacity = populationSize() - protectedSubset.size();
    List<S> reduced =
        cluster
            ? RVEATruncation.cluster(remaining, capacity)
            : RVEATruncation.crowded(remaining, capacity);
    return union(protectedSubset, reduced);
  }

  @Override
  protected void afterSelection(List<S> survivors) {
    archive =
        EpsilonIndicatorSelection.select(union(archive, survivors), populationSize()).stream()
            .map(this::copySolution)
            .toList();
  }

  @Override
  protected List<S> finish(List<S> survivors) {
    return survivors;
  }

  @Override
  protected int survivorCapacity() {
    return populationSize();
  }

  @Override
  protected List<S> survivorsWithoutFeasibleCandidates() {
    return archive();
  }

  /** Returns copies so that callers cannot modify the archive used by future generations. */
  public List<S> archive() {
    return archive.stream().map(this::copySolution).toList();
  }

  @SuppressWarnings("unchecked")
  private S copySolution(S solution) {
    return (S) solution.copy();
  }

  private List<S> union(List<S> first, List<S> second) {
    return new ArrayList<>(Stream.concat(first.stream(), second.stream()).distinct().toList());
  }

  private static double[][] coarseVectors(int objectives, int requested) {
    int divisions = 1;
    while (ReferencePointGenerator.calculateNumberOfReferencePoints(objectives, divisions + 1)
        <= requested) {
      divisions++;
    }
    return RVEAGeometry.directions(
        ReferencePointGenerator.generateSingleLayer(objectives, divisions), objectives);
  }
}
