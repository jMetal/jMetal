package org.uma.jmetal.auto.component.replacement.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.auto.component.replacement.Replacement;
import org.uma.jmetal.auto.component.selection.impl.RankingAndDensityEstimatorMatingPoolSelection;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.auto.util.ranking.impl.StrengthRanking;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.StrengthFitnessComparator;
import org.uma.jmetal.util.solutionattribute.impl.LocationAttribute;

import java.util.*;

public class RankingAndDensityEstimatorReplacementV2<S extends Solution<?>> implements Replacement<S> {
  public Ranking<S> ranking ;
  public DensityEstimator<S> densityEstimator ;

  public RankingAndDensityEstimatorReplacementV2(Ranking<S> ranking, DensityEstimator<S> densityEstimator) {
    this.ranking = ranking ;
    this.densityEstimator = densityEstimator ;
  }

  public List<S> replace(List<S> solutionList, List<S> offspringList) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(solutionList);
    jointPopulation.addAll(offspringList);

    //RankingAndDensityEstimatorMatingPoolSelection<S> selection ;
    //selection = new RankingAndDensityEstimatorMatingPoolSelection<S>(solutionList.size(), ranking, densityEstimator);

    //return selection.select(jointPopulation) ;
    ranking.computeRanking(jointPopulation) ;
    for (int i = 0; i < ranking.getNumberOfSubFronts(); i++) {
      densityEstimator.computeDensityEstimator(ranking.getSubFront(i));
    }

    List<S> resultSolutions = truncate(jointPopulation, ranking, solutionList.size()) ;
    ranking.computeRanking(resultSolutions) ;
    for (int i = 0; i < ranking.getNumberOfSubFronts(); i++) {
      densityEstimator.computeDensityEstimator(ranking.getSubFront(i));
    }
    return resultSolutions ;
  }

  private List<S> truncate(List<S> source2, Ranking<S> ranking, int size2) {
    int size;
    List<S> source = new ArrayList<>(source2.size());
    source.addAll(source2);
    if (source2.size() < size2) {
      size = source.size();
    } else {
      size = size2;
    }

    List<S> aux = new ArrayList<>(source.size());

    int i = 0;
    while (i < source.size()){
      double fitness = 0 ; //(double) this.strengthRawFitness.getAttribute(source.get(i));
      if (fitness<1.0){
        aux.add(source.get(i));
        source.remove(i);
      } else {
        i++;
      }
    }


    if (aux.size() < size){
      //StrengthFitnessComparator<S> comparator = new StrengthFitnessComparator<S>();
      Comparator<S> comparator = new StrengthRanking<S>().getSolutionComparator() ;
      Collections.sort(source,comparator );
      int remain = size - aux.size();
      for (i = 0; i < remain; i++){
        aux.add(source.get(i));
      }
      return aux;
    } else if (aux.size() == size) {
      return aux;
    }



    double [][] distance = SolutionListUtils.distanceMatrix(aux);
    List<List<Pair<Integer, Double>> > distanceList = new ArrayList<>();
    LocationAttribute<S> location = new LocationAttribute<S>(aux);
    for (int pos = 0; pos < aux.size(); pos++) {
      List<Pair<Integer, Double>> distanceNodeList = new ArrayList<>();
      for (int ref = 0; ref < aux.size(); ref++) {
        if (pos != ref) {
          distanceNodeList.add(Pair.of(ref, distance[pos][ref]));
        }
      }
      distanceList.add(distanceNodeList);
    }

    for (int q = 0; q < distanceList.size(); q++){
      Collections.sort(distanceList.get(q), (pair1, pair2) -> {
        if (pair1.getRight()  < pair2.getRight()) {
          return -1;
        } else if (pair1.getRight()  > pair2.getRight()) {
          return 1;
        } else {
          return 0;
        }
      });
    }

    while (aux.size() > size) {
      double minDistance = Double.MAX_VALUE;
      int toRemove = 0;
      i = 0;
      Iterator<List<Pair<Integer, Double>>> iterator = distanceList.iterator();
      while (iterator.hasNext()){
        List<Pair<Integer, Double>> dn = iterator.next();
        if (dn.get(0).getRight() < minDistance){
          toRemove = i;
          minDistance = dn.get(0).getRight();
          //i y toRemove have the same distance to the first solution
        } else if (dn.get(0).getRight().equals(minDistance)) {
          int k = 0;
          while ((dn.get(k).getRight().equals(
              distanceList.get(toRemove).get(k).getRight())) &&
              k < (distanceList.get(i).size()-1)) {
            k++;
          }

          if (dn.get(k).getRight() <
              distanceList.get(toRemove).get(k).getRight()) {
            toRemove = i;
          }
        }
        i++;
      }

      int tmp =  (int) location.getAttribute(aux.get(toRemove));
      aux.remove(toRemove);
      distanceList.remove(toRemove);

      Iterator<List<Pair<Integer, Double>>> externIterator = distanceList.iterator();
      while (externIterator.hasNext()){
        Iterator<Pair<Integer, Double>> interIterator = externIterator.next().iterator();
        while (interIterator.hasNext()){
          if (interIterator.next().getLeft() == tmp){
            interIterator.remove();
            continue;
          }
        }
      }
    }
    return aux;
  }


}
