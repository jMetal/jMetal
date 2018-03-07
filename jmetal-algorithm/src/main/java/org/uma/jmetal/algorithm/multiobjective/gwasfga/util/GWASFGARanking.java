package org.uma.jmetal.algorithm.multiobjective.gwasfga.util;

import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class GWASFGARanking<S extends Solution<?>> extends GenericSolutionAttribute<S, Integer>
		implements Ranking<S> {

  private AbstractUtilityFunctionsSet<S> utilityFunctionsUtopia;
  private AbstractUtilityFunctionsSet<S> utilityFunctionsNadir;
  private List<List<S>> rankedSubpopulations;
  private int numberOfRanks = 0;

  public GWASFGARanking(AbstractUtilityFunctionsSet<S> utilityFunctionsUtopia, AbstractUtilityFunctionsSet<S> utilityFunctionsNadir) {
    this.utilityFunctionsUtopia = utilityFunctionsUtopia;
    this.utilityFunctionsNadir  = utilityFunctionsNadir;
  }

  @Override
  public Ranking<S> computeRanking(List<S> population) {
      int toRemoveIdx, numberOfWeights;
      double minimumValue, value;
      S solutionToInsert;
      List<S> temporalList;

      this.numberOfRanks = (population.size() + 1) / (this.utilityFunctionsUtopia.getSize() + this.utilityFunctionsNadir.getSize());

      this.rankedSubpopulations = new ArrayList<>(this.numberOfRanks);

      for (int i = 0; i < this.numberOfRanks; i++) {
          this.rankedSubpopulations.add(new ArrayList<S>());
      }
      temporalList = new LinkedList<>();
      temporalList.addAll(population);

      numberOfWeights = this.utilityFunctionsUtopia.getSize() + this.utilityFunctionsNadir.getSize();
      for (int idx = 0; idx < this.numberOfRanks; idx++) {
          for (int weight = 0; weight < numberOfWeights/2; weight++) {
              toRemoveIdx = 0;
              minimumValue = this.utilityFunctionsUtopia.evaluate(temporalList.get(0), weight);
              for (int solutionIdx = 1; solutionIdx < temporalList.size(); solutionIdx++) {
                  value = this.utilityFunctionsUtopia.evaluate(temporalList.get(solutionIdx), weight);

                  if (value < minimumValue) {
                      minimumValue = value;
                      toRemoveIdx = solutionIdx;
                  }
              }
              solutionToInsert = temporalList.remove(toRemoveIdx);
              setAttribute(solutionToInsert, idx);
              this.rankedSubpopulations.get(idx).add(solutionToInsert);

              toRemoveIdx = 0;
              minimumValue = this.utilityFunctionsNadir.evaluate(temporalList.get(0), weight);
              for (int solutionIdx = 1; solutionIdx < temporalList.size(); solutionIdx++) {
                  value = this.utilityFunctionsNadir.evaluate(temporalList.get(solutionIdx), weight);

                  if (value < minimumValue) {
                      minimumValue = value;
                      toRemoveIdx = solutionIdx;
                  }
              }
              solutionToInsert = temporalList.remove(toRemoveIdx);
              setAttribute(solutionToInsert, idx);
              this.rankedSubpopulations.get(idx).add(solutionToInsert);
          }
      }
      return this;
  }

  @Override
  public List<S> getSubfront(int rank) {
    return this.rankedSubpopulations.get(rank);
  }

  @Override
  public int getNumberOfSubfronts() {
    return this.rankedSubpopulations.size();
  }
  
  /*public AbstractUtilityFunctionsSet<S> getUtilityFunctions() {
    return this.utilityFunctions;
  }*/
  
  
  
}
