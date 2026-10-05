package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.OverallConstraintViolationDegreeComparator;

/** Shared feasibility partition and stable violation fallback for RVEA-family selection. */
final class FeasibilityFirstSelection<S extends Solution<?>> {
  private final OverallConstraintViolationDegreeComparator<S> comparator =
      new OverallConstraintViolationDegreeComparator<>();
  private long candidateVisits;
  private long feasibleVisits;
  private long infeasibleVisits;
  private long affectedSelections;
  private long violationComparisons;
  private long infeasibleSurvivors;

  record Partition<S>(List<S> feasible, List<S> infeasible) {}

  Partition<S> partition(List<S> candidates) {
    List<S> feasible = new ArrayList<>();
    List<S> infeasible = new ArrayList<>();
    for (S solution : candidates) {
      (ConstraintHandling.isFeasible(solution) ? feasible : infeasible).add(solution);
    }
    candidateVisits += candidates.size();
    feasibleVisits += feasible.size();
    infeasibleVisits += infeasible.size();
    if (!infeasible.isEmpty()) {
      affectedSelections++;
    }
    return new Partition<>(feasible, infeasible);
  }

  List<S> supplement(Partition<S> partition, List<S> survivors, int occupiedCapacity) {
    // Do not replace feasible candidates discarded by the original objective-space selection.
    int count =
        Math.max(0, occupiedCapacity - Math.max(partition.feasible().size(), survivors.size()));
    count = Math.min(count, partition.infeasible().size());
    if (count == 0) {
      return survivors;
    }
    List<S> ordered = new ArrayList<>(partition.infeasible());
    // List.sort is stable: equal violations retain candidate encounter order, without an RNG draw.
    ordered.sort(
        (first, second) -> {
          violationComparisons++;
          return comparator.compare(first, second);
        });
    List<S> result = new ArrayList<>(survivors);
    result.addAll(ordered.subList(0, count));
    infeasibleSurvivors += count;
    return result;
  }

  RVEAEnvironmentalSelection.ConstraintStatistics statistics() {
    return new RVEAEnvironmentalSelection.ConstraintStatistics(
        candidateVisits,
        feasibleVisits,
        infeasibleVisits,
        affectedSelections,
        violationComparisons,
        infeasibleSurvivors);
  }
}
