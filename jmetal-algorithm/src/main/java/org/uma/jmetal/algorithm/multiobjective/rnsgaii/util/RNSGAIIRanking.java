package org.uma.jmetal.algorithm.multiobjective.rnsgaii.util;

import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RNSGAIIRanking <S extends Solution<?>> extends GenericSolutionAttribute<S, Integer>
        implements Ranking<S> {
    private AbstractUtilityFunctionsSet<S> utilityFunctions;
    private List<List<S>> rankedSubpopulations;
    private int numberOfRanks = 0;
    private double epsilon;
    public RNSGAIIRanking(AbstractUtilityFunctionsSet<S> utilityFunctions, double epsilon) {
        this.utilityFunctions = utilityFunctions;
        this.epsilon = epsilon;
    }
    @Override
    public Ranking<S> computeRanking(List<S> population) {
        this.numberOfRanks = (population.size() + 1) / this.utilityFunctions.getSize();
        this.rankedSubpopulations = new ArrayList(this.numberOfRanks);

        for(int i = 0; i < this.numberOfRanks; ++i) {
            this.rankedSubpopulations.add(new ArrayList());
        }

        List<S> temporalList = new LinkedList();
        temporalList.addAll(population);

        for(int idx = 0; idx < this.numberOfRanks; ++idx) {
            for(int weigth = 0; weigth < this.utilityFunctions.getSize(); ++weigth) {
                int toRemoveIdx = 0;
                //double minimumValue = this.utilityFunctions.evaluate(temporalList.get(0), weigth).doubleValue();
                for(int solutionIdx = 1; solutionIdx < temporalList.size(); ++solutionIdx) {
                    double value = this.utilityFunctions.evaluate(temporalList.get(solutionIdx), weigth).doubleValue();
                    if (value < epsilon) {

                        toRemoveIdx = solutionIdx;
                    }
                }
                S solutionToInsert = temporalList.remove(toRemoveIdx);
                this.setAttribute(solutionToInsert, idx);
                ((List)this.rankedSubpopulations.get(idx)).add(solutionToInsert);
            }
        }

        return this;    }

    @Override
    public List<S> getSubfront(int rank) {
        if (rank >= rankedSubpopulations.size()) {
            throw new JMetalException("Invalid rank: " + rank + ". Max rank = " + (rankedSubpopulations.size() -1)) ;
        }
        return (List)this.rankedSubpopulations.get(rank);
    }


    @Override
    public int getNumberOfSubfronts() {
        return this.rankedSubpopulations.size();
    }

}
