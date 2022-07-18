package org.uma.jmetal.util.solutionattribute.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;

@SuppressWarnings("serial")
public class PreferenceDistance<S extends Solution<?>> extends GenericSolutionAttribute<S, Double>
    implements DensityEstimator<S> {
  private List<Double> interestPoint;

  private @Nullable List<Double> weights = null;
  private double epsilon;

  public PreferenceDistance(List<Double> interestPoint, double epsilon) {
    this.epsilon = epsilon;
    this.interestPoint = interestPoint;
  }

  public void updatePointOfInterest(List<Double> newInterestPoint) {
    interestPoint = newInterestPoint;
  }

  public int getSize() {
    return this.weights.size();
  }

  @Override
  public void computeDensityEstimator(@NotNull List<S> solutionList) {
    var size = solutionList.size();

    if (size == 0) {
      return;
    }

    if (size == 1) {
      solutionList.get(0).attributes().put(getAttributeIdentifier(), Double.POSITIVE_INFINITY);
      return;
    }

    if (size == 2) {
      solutionList.get(0).attributes().put(getAttributeIdentifier(), Double.POSITIVE_INFINITY);
      solutionList.get(1).attributes().put(getAttributeIdentifier(), Double.POSITIVE_INFINITY);

      return;
    }

    // Use a new SolutionSet to avoid altering the original solutionSet
    List<S> front = new ArrayList<>(size);
    for (var solution : solutionList) {
      front.add(solution);
    }

    for (var i = 0; i < size; i++) {
      front.get(i).attributes().put(getAttributeIdentifier(), 0.0);
    }

    double objetiveMaxn;
    double objetiveMinn;
    double distance;

    var numberOfObjectives = solutionList.get(0).objectives().length;
    weights = new ArrayList<>();
    for (var i = 0; i < numberOfObjectives; i++) {
      weights.add(1.0d / numberOfObjectives);
    }

    for (var i = 0; i < front.size() - 1; i++) {
      var normalizeDiff = 0.0D;
      distance = 0.0D;
      for (var j = 0; j < numberOfObjectives; j++) {
        // Sort the population by Obj n
        Collections.sort(front, new ObjectiveComparator<S>(j));
        objetiveMinn = front.get(0).objectives()[j];
        objetiveMaxn = front.get(front.size() - 1).objectives()[j];
        normalizeDiff =
            (front.get(i).objectives()[j] - this.interestPoint.get(j))
                / (objetiveMaxn - objetiveMinn);
        distance += weights.get(j) * Math.pow(normalizeDiff, 2.0D);
      }
      distance = Math.sqrt(distance);
      front.get(i).attributes().put(getAttributeIdentifier(), distance);
    }

    // solutionList = epsilonClean(front);

  }

  public List<S> epsilonClean(List<S> solutionList) {
    List<S> preference = new ArrayList<>();
    @NotNull List<S> temporalList = new LinkedList<>(solutionList);
    var numerOfObjectives = solutionList.get(0).objectives().length;

    while (!temporalList.isEmpty()) {
      var indexRandom = JMetalRandom.getInstance().nextInt(0, temporalList.size() - 1); // 0

      var randomSolution = temporalList.get(indexRandom);

      preference.add(randomSolution);
      temporalList.remove(indexRandom);

      for (var indexOfSolution = 0; indexOfSolution < temporalList.size(); indexOfSolution++) {
        double sum = 0;

        for (var indexOfObjective = 0; indexOfObjective < numerOfObjectives; indexOfObjective++) {
          temporalList.sort(new ObjectiveComparator<S>(indexOfObjective));
          var objetiveMinn = temporalList.get(0).objectives()[indexOfObjective];
          var objetiveMaxn =
              temporalList.get(temporalList.size() - 1).objectives()[indexOfObjective];
          sum =
              sum
                  + ((Math.abs(
                          randomSolution.objectives()[indexOfObjective]
                              - temporalList.get(indexOfSolution).objectives()[indexOfObjective]))
                      / (objetiveMaxn - objetiveMinn));
        }

        if (sum < epsilon) {
          temporalList
              .get(indexOfSolution)
              .attributes().put(getAttributeIdentifier(), Double.MAX_VALUE);
          preference.add(temporalList.get(indexOfSolution));
          temporalList.remove(indexOfSolution);
        }
      }
    }
    return preference;
  }
}
