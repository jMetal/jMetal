package org.uma.jmetal.algorithm.multiobjective.spea2.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.densityestimator.impl.StrenghtRawFitnessDensityEstimator;
import org.uma.jmetal.util.solutionattribute.impl.LocationAttribute;

/**
 * @author Juanjo Durillo
 * @param <S>
 */
public class EnvironmentalSelection<S extends Solution<?>> implements SelectionOperator<List<S>,List<S>> {

  private int solutionsToSelect ;
  private @NotNull StrenghtRawFitnessDensityEstimator<S> densityEstimator = new StrenghtRawFitnessDensityEstimator<>(1) ;

  public EnvironmentalSelection(int solutionsToSelect) {
    this(solutionsToSelect, 1) ;
  }

  public EnvironmentalSelection(int solutionsToSelect, int k) {
    this.solutionsToSelect = solutionsToSelect ;
  }

  @Override
  public List<S> execute(List<S> source2) {
    int size;
    @NotNull List<S> source = new ArrayList<>(source2.size());
    source.addAll(source2);
    if (source2.size() < this.solutionsToSelect) {
      size = source.size();
    } else {
      size = this.solutionsToSelect;
    }

    List<S> aux = new ArrayList<>(source.size());

    var i = 0;
    while (i < source.size()){
      double fitness = densityEstimator.getValue(source.get(i)) ;
      if (fitness<1.0){
        aux.add(source.get(i));
        source.remove(i);
      } else {
        i++;
      }
    }

    if (aux.size() < size){
      var comparator = densityEstimator.getComparator() ;
      source.sort(comparator);
      var remain = size - aux.size();
      for (i = 0; i < remain; i++){
        aux.add(source.get(i));
      }
      return aux;
    } else if (aux.size() == size) {
      return aux;
    }

    var distance = SolutionListUtils.distanceMatrix(aux);
    @NotNull List<List<Pair<Integer, Double>> > distanceList = new ArrayList<>();
    var location = new LocationAttribute<S>(aux);
    for (var pos = 0; pos < aux.size(); pos++) {
      List<Pair<Integer, Double>> distanceNodeList = new ArrayList<>();
      for (var ref = 0; ref < aux.size(); ref++) {
        if (pos != ref) {
          distanceNodeList.add(Pair.of(ref, distance[pos][ref]));
        }
      }
      distanceList.add(distanceNodeList);
    }

    for (var pairs : distanceList) {
      pairs.sort(Comparator.comparing(Pair::getRight));
    }

    while (aux.size() > size) {
      var minDistance = Double.MAX_VALUE;
      var toRemove = 0;
      i = 0;
      for (@NotNull List<Pair<Integer, Double>> dist : distanceList) {
        if (dist.get(0).getRight() < minDistance) {
          toRemove = i;
          minDistance = dist.get(0).getRight();
          //i y toRemove have the same distance to the first solution
        } else if (dist.get(0).getRight().equals(minDistance)) {
          var k = 0;
          while ((dist.get(k).getRight().equals(
                  distanceList.get(toRemove).get(k).getRight())) &&
                  k < (distanceList.get(i).size() - 1)) {
            k++;
          }

          if (dist.get(k).getRight() <
                  distanceList.get(toRemove).get(k).getRight()) {
            toRemove = i;
          }
        }
        i++;
      }

      var tmp =  (int) location.getAttribute(aux.get(toRemove));
      aux.remove(toRemove);
      distanceList.remove(toRemove);

      for (@NotNull List<Pair<Integer, Double>> pairs : distanceList) {
        pairs.removeIf(integerDoublePair -> integerDoublePair.getLeft() == tmp);
      }
    }
    return aux;
  }

}
