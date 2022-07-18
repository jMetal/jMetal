package org.uma.jmetal.util.densityestimator.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements the a density estimator based on the distance to the k-th nearest solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SpatialSpreadDeviationDensityEstimator<S extends Solution<?>>
    implements DensityEstimator<S> {
  private final String attributeId = getClass().getName();

  /**
   * Assigns the KNN distance to all the solutions in a list
   *
   * @param solutionList
   */
  @Override
  public void compute(List<S> solutionList) {
    int size = solutionList.size();

    if (size <= solutionList.get(0).objectives().length) {
      for (int x = 0; x < size; x++) {
        solutionList.get(x).attributes().put(attributeId, Double.POSITIVE_INFINITY);
      }
      return;
    }

    // Use a new SolutionSet to avoid altering the original solutionSet
    List<S> front = new ArrayList<>(size);
    for (S solution : solutionList) {
      front.add(solution);
    }

    for (int i = 0; i < size; i++) {
      front.get(i).attributes().put(attributeId, 0.0);
    }

    int numberOfObjectives = solutionList.get(0).objectives().length;

    double objetiveMaxn[] = new double[numberOfObjectives];
    double objetiveMinn[] = new double[numberOfObjectives];

    for (int i = 0; i < numberOfObjectives; i++) {
      // Sort the population by Obj n
      Collections.sort(front, new ObjectiveComparator<S>(i));
      objetiveMinn[i] = front.get(0).objectives()[i];
      objetiveMaxn[i] = front.get(front.size() - 1).objectives()[i];

      // Set de crowding distance Los extremos si infinitos
      front.get(0).attributes().put(attributeId, Double.POSITIVE_INFINITY);
      front.get(size - 1).attributes().put(attributeId, Double.POSITIVE_INFINITY);
    }
    double[] @NotNull [] distance =
            SolutionListUtils.normalizedDistanceMatrix(front, objetiveMaxn, objetiveMinn);

    double dminn, dmaxx;
    dminn = Double.MAX_VALUE;
    dmaxx = 0.0;
    for (int i = 0; i < distance.length; i++)
      for (int j = 0; j < distance.length; j++) {
        if (i != j) {
          if (distance[i][j] < dminn) {
            dminn = distance[i][j];
          }
          if (distance[i][j] > dmaxx) {
            dmaxx = distance[i][j];
          }
        }
      }

    for (int i = 0; i < front.size(); i++) {
      double temp = 0.0;
      for (int j = 0; j < distance.length; j++) {
        if (i != j) {
          temp += Math.pow(distance[i][j] - (dmaxx - dminn), 2);
        }
      }
      temp /= distance.length - 1;
      temp = Math.sqrt(temp);
      temp *= -1;
      temp += (double) front.get(i).attributes().get(attributeId);
      // if((double) front.get(i).getAttribute(getAttributeID())!=Double.POSITIVE_INFINITY)
      front.get(i).attributes().put(attributeId, temp);
    }

    // int k = numberOfObjectives la solucion 0 es ella misma
    for (int i = 0; i < distance.length; i++) {
      Arrays.sort(distance[i]);
      double kDistance = 0.0;
      for (int k = numberOfObjectives; k > 0; k--) {
        // kDistance += (dmaxx-dminn) / (distance[i][k]);//me gusta mas este
        // kDistance += (dmaxx-dminn) / (distance[i][k]+dminn);//original
        kDistance += (dmaxx - dminn) / distance[i][k];
      }
      double temp = (double) front.get(i).attributes().get(attributeId);
      // if(temp!=Double.POSITIVE_INFINITY)
      // kDistance=kDistance/numberOfObjectives-1;
      temp -= kDistance;
      front.get(i).attributes().put(attributeId, temp);
    }
  }

  @Override
  public Double getValue(@NotNull S solution) {
    Check.notNull(solution);

    Double result = 0.0;
    if (solution.attributes().get(attributeId) != null) {
      result = (Double) solution.attributes().get(attributeId);
    }
    return result;
  }

  @Override
  public Comparator<S> getComparator() {
    return Comparator.comparing(this::getValue);
  }
}
