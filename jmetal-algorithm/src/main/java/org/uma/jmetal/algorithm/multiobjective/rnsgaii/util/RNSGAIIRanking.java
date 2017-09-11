package org.uma.jmetal.algorithm.multiobjective.rnsgaii.util;

import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.util.ReferencePoint;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.comparator.PreferenceDistanceComparator;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.*;

public class RNSGAIIRanking <S extends Solution<?>> extends GenericSolutionAttribute<S, Integer>
        implements Ranking<S> {

    private PreferenceNSGAII<S> utilityFunctions;
    private List<List<S>> rankedSubpopulations;
    private int numberOfRanks = 0;

    public RNSGAIIRanking(PreferenceNSGAII<S> utilityFunctions) {
        this.utilityFunctions = utilityFunctions;
    }

    @Override
    public Ranking<S> computeRanking(List<S> population) {
        this.numberOfRanks = (population.size() + 1) / this.utilityFunctions.getSize();
        this.rankedSubpopulations = new ArrayList(this.numberOfRanks);

        for(int i = 0; i < this.numberOfRanks; ++i) {
            this.rankedSubpopulations.add(new ArrayList());
        }
        List<Double> upperBound = new ArrayList<>();
        List<Double> lowerBound = new ArrayList<>();
        for (int i = 0; i < population.get(0).getNumberOfObjectives(); i++) {
            // Sort the population by Obj n
            Collections.sort(population, new ObjectiveComparator<S>(i)) ;
            double objetiveMinn = population.get(0).getObjective(i);
            double objetiveMaxn = population.get(population.size() - 1).getObjective(i);
            upperBound.add(objetiveMaxn);
            lowerBound.add(objetiveMinn);
        }
        this.utilityFunctions.setLowerBounds(lowerBound);
        this.utilityFunctions.setUpperBounds(upperBound);
        List<S> temporalList = new LinkedList();
        temporalList.addAll(population);

        for(int idx = 0; idx < this.numberOfRanks; ++idx) {
                int toRemoveIdx = 0;
                double minimumValue = this.utilityFunctions.evaluate(temporalList.get(0)).doubleValue();
                for(int solutionIdx = 1; solutionIdx < temporalList.size(); ++solutionIdx) {
                    double value = this.utilityFunctions.evaluate(temporalList.get(solutionIdx)).doubleValue();
                    if (value < minimumValue) {
                        minimumValue = value;
                        toRemoveIdx = solutionIdx;
                    }
                }
                S solutionToInsert = temporalList.remove(toRemoveIdx);
                this.setAttribute(solutionToInsert, idx);
                ((List)this.rankedSubpopulations.get(idx)).add(solutionToInsert);

        }

        return this;
    }

    public List<S> getSubfront(int rank) {
        return (List)this.rankedSubpopulations.get(rank);
    }

    public int getNumberOfSubfronts() {
        return this.rankedSubpopulations.size();
    }


}
