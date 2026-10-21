package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

/**
 * Deterministic candidate streams for the RVEA-family selections and a signature of the state of
 * a selection after each generation: survivors, reference vectors and the iRVEA archive.
 */
public final class RveaTrajectoryFixtures {
  static final int GENERATIONS = 24;

  private RveaTrajectoryFixtures() {}

  static RVEAEnvironmentalSelection<TaggedSolution> selection(String variant, int objectives) {
    var vectors = ReferencePointGenerator.generateSingleLayer(objectives, 2);
    return switch (variant) {
      case "RVEA" -> new RVEAEnvironmentalSelection<>(objectives, GENERATIONS, 2, 0.2, vectors);
      case "RVEAStar" ->
          new RVEAStarEnvironmentalSelection<>(objectives, GENERATIONS, 2, 0.2, vectors);
      case "IRVEA" ->
          new IRVEAEnvironmentalSelection<>(objectives, GENERATIONS, 2, 0.2, vectors, 7);
      default -> throw new IllegalArgumentException(variant);
    };
  }

  static List<String> trajectory(
      String variant, int objectives, String shape, long seed, int constraints) {
    return trajectory(variant, objectives, shape, seed, constraints, false);
  }

  static List<String> trajectory(
      String variant,
      int objectives,
      String shape,
      long seed,
      int constraints,
      boolean addInfeasible) {
    JMetalRandom.getInstance().setSeed(seed);
    var selection = selection(variant, objectives);
    List<TaggedSolution> survivors = List.of();
    List<String> signatures = new ArrayList<>();
    for (int generation = 0; generation < GENERATIONS; generation++) {
      List<TaggedSolution> candidates = new ArrayList<>(survivors);
      for (int i = 0; i < 48; i++) {
        int id = generation * 1000 + i;
        double[] values = new double[objectives];
        for (int j = 0; j < objectives; j++) {
          double coordinate = Math.abs(Math.sin((id + 1) * (j + 1) * 0.719));
          values[j] =
              switch (shape) {
                case "curved" -> 0.05 + coordinate + (j + 1.0) / (generation + 8);
                case "disconnected" -> 0.2 + Math.round(coordinate * 4) / 4.0 + j * 0.003;
                case "collapsed" -> j == 0 ? coordinate : 1.0;
                default -> throw new IllegalArgumentException(shape);
              };
        }
        var solution = new TaggedSolution(id, values, new double[constraints]);
        Arrays.fill(solution.constraints(), 1.0);
        candidates.add(solution);
      }
      if (addInfeasible) {
        double[] misleadingIdeal = new double[objectives];
        Arrays.fill(misleadingIdeal, -1000000.0);
        candidates.add(new TaggedSolution(-generation - 1, misleadingIdeal, new double[] {-0.1}));
      }
      survivors =
          selection.execute(candidates, selection.populationSize()).stream()
              .filter(ConstraintHandling::isFeasible)
              .toList();
      String signature = signature(selection, survivors);
      if (generation == GENERATIONS - 1) {
        signature += ":rng=" + Double.toHexString(JMetalRandom.getInstance().nextDouble());
      }
      signatures.add(signature);
    }
    return signatures;
  }

  static String signature(
      RVEAEnvironmentalSelection<TaggedSolution> selection, List<TaggedSolution> survivors) {
    StringBuilder state = new StringBuilder("generation=" + selection.currentGeneration());
    appendSolutions(state, survivors);
    appendVectors(state, selection.referenceVectors());
    if (selection instanceof RVEAStarEnvironmentalSelection<?> star) {
      appendVectors(state, star.adaptiveReferenceVectors());
    }
    if (selection instanceof IRVEAEnvironmentalSelection<?> improved) {
      appendSolutions(state, improved.archive());
    }
    return state.toString();
  }

  private static void appendSolutions(StringBuilder state, List<? extends Solution<?>> solutions) {
    state.append(";solutions=").append(solutions.size());
    for (var solution : solutions) {
      state.append(";id=").append(solution.variables());
      appendVectors(state, new double[][] {solution.objectives()});
    }
  }

  private static void appendVectors(StringBuilder state, double[][] vectors) {
    state.append(";vectors=").append(vectors.length);
    for (double[] vector : vectors) {
      state.append('[');
      for (double value : vector) {
        state.append(Double.toHexString(value)).append(',');
      }
      state.append(']');
    }
  }

  /** Identity is the decision ID, independently of the number of exposed constraints. */
  static final class TaggedSolution implements Solution<Integer> {
    private final List<Integer> variables;
    private final double[] objectives;
    private final double[] constraints;
    private final Map<Object, Object> attributes;

    TaggedSolution(int id, double[] objectives, double[] constraints) {
      this.variables = List.of(id);
      this.objectives = objectives.clone();
      this.constraints = constraints.clone();
      this.attributes = new HashMap<>();
    }

    @Override
    public List<Integer> variables() {
      return variables;
    }

    @Override
    public double[] objectives() {
      return objectives;
    }

    @Override
    public double[] constraints() {
      return constraints;
    }

    @Override
    public Map<Object, Object> attributes() {
      return attributes;
    }

    @Override
    public TaggedSolution copy() {
      var copy = new TaggedSolution(variables.getFirst(), objectives, constraints);
      copy.attributes.putAll(attributes);
      return copy;
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof TaggedSolution solution && variables.equals(solution.variables);
    }

    @Override
    public int hashCode() {
      return variables.hashCode();
    }
  }
}
