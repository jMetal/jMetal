package org.uma.jmetal.util.solutionattribute.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;

@SuppressWarnings("serial")
public class PreferenceDistance<S extends Solution<?>> extends GenericSolutionAttribute<S, Double>
    implements DensityEstimator<S> {
  private List<Double> interestPoint;

  private List<Double> weights = null;
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
  public void computeDensityEstimator(List<S> solutionList) {
    int size = solutionList.size();

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
    front.addAll(solutionList);

    for (int i = 0; i < size; i++) {
      front.get(i).attributes().put(getAttributeIdentifier(), 0.0);
    }

    double objetiveMaxn;
    double objetiveMinn;
    double distance;

    int numberOfObjectives = solutionList.get(0).objectives().length;
    weights = new ArrayList<>();
    for (int i = 0; i < numberOfObjectives; i++) {
      weights.add(1.0d / numberOfObjectives);
    }

    for (int i = 0; i < front.size() - 1; i++) {
      double normalizeDiff = 0.0D;
      distance = 0.0D;
      for (int j = 0; j < numberOfObjectives; j++) {
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
    List<S> temporalList = new LinkedList<>(solutionList);
    int numerOfObjectives = solutionList.get(0).objectives().length;

    while (!temporalList.isEmpty()) {
      int indexRandom = JMetalRandom.getInstance().nextInt(0, temporalList.size() - 1); // 0

      S randomSolution = temporalList.get(indexRandom);

      preference.add(randomSolution);
      temporalList.remove(indexRandom);

      for (int indexOfSolution = 0; indexOfSolution < temporalList.size(); indexOfSolution++) {
        double sum = 0;

        for (int indexOfObjective = 0; indexOfObjective < numerOfObjectives; indexOfObjective++) {
          temporalList.sort(new ObjectiveComparator<S>(indexOfObjective));
          double objetiveMinn = temporalList.get(0).objectives()[indexOfObjective];
          double objetiveMaxn =
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
