package org.uma.jmetal.algorithm.multiobjective.rnsgaii.util;



import org.uma.jmetal.solution.Solution;

import org.uma.jmetal.util.comparator.ObjectiveComparator;

import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.*;

public class RNSGAIIRanking <S extends Solution<?>> extends GenericSolutionAttribute<S, Integer>
        implements Ranking<S> {

    private PreferenceNSGAII<S> utilityFunctions;
    private List<List<S>> rankedSubpopulations;
    private int numberOfRanks = 0;
    private double epsilon ;

    public RNSGAIIRanking(PreferenceNSGAII<S> utilityFunctions, double epsilon) {
        this.utilityFunctions = utilityFunctions;
        this.epsilon = epsilon;
    }

    @Override
    public Ranking<S> computeRanking(List<S> population) {
        int size = population.size();
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
        //ordening the solution by weight euclidean distance
        SortedMap<Double,S> map = new TreeMap<>();
        for (S solution: temporalList) {
            double value = this.utilityFunctions.evaluate(solution).doubleValue();
            map.put(value,solution);
        }


        List<S> populationOrder = new ArrayList<>(map.values());
        this.numberOfRanks = populationOrder.size()+1;
        this.rankedSubpopulations = new ArrayList(this.numberOfRanks);
        for (int i=0; i<numberOfRanks-1;i++){
            this.rankedSubpopulations.add(new ArrayList<>());
        }
        int rank=1;
        int rankPreference=0;
        while(!populationOrder.isEmpty()){
            int indexRandom =0;// JMetalRandom.getInstance().nextInt(0,populationOrder.size()-1);
            S solutionRandom = populationOrder.get(indexRandom);
            this.setAttribute(solutionRandom, rankPreference);

            List<S> rankList= this.rankedSubpopulations.get(rankPreference);
            if(rankList==null){
                rankList= new ArrayList();
            }
            rankList.add(solutionRandom);
            populationOrder.remove(indexRandom);
            for(int i=0;i<populationOrder.size();i++){
                S solution = populationOrder.get(i);
                double sum = this.preference(solutionRandom,solution,upperBound,lowerBound);
                if(sum < epsilon){
                    this.setAttribute(solutionRandom, rank);

                    List<S> rankListAux= this.rankedSubpopulations.get(rank);
                    if(rankListAux==null){
                        rankListAux= new ArrayList();
                    }
                    rankListAux.add(solutionRandom);
                    populationOrder.remove(i);
                    rank++;
                }
            }
        }
        /*int rankGood =0;
        int rankBad=1;

        Set<Double> keys= map.keySet();
        List<Double> keyList = new ArrayList<>(keys);
        Collections.sort(keyList);
        int rank=0;
        for (Double key: keyList) {
            if(key<epsilon){
                rank=rankBad;
                rankBad++;
            }else {
                rank =rankGood;
            }
            S solution = map.get(key);
            this.setAttribute(solution, rank);

            List<S> rankList= this.rankedSubpopulations.get(rank);
            if(rankList==null){
                rankList= new ArrayList();
            }
            rankList.add(solution);
        }*/

        return this;
    }

    private double preference(S solution1, S solution2,List<Double> upperBounds,List<Double> lowerBounds){
        double result =0.0D;


            for (int indexOfObjective = 0; indexOfObjective < solution1.getNumberOfObjectives(); indexOfObjective++) {
                if (upperBounds != null && lowerBounds != null) {
                    result = result + ((Math.abs(solution1.getObjective(indexOfObjective) -
                            solution2.getObjective(indexOfObjective))) / (upperBounds.get(indexOfObjective) - lowerBounds.get(indexOfObjective)));
                } else {
                    result = result + Math.abs(solution1.getObjective(indexOfObjective) -
                            solution2.getObjective(indexOfObjective));
                }
            }

        return result;
        }

    public List<S> getSubfront(int rank) {
        return (List)this.rankedSubpopulations.get(rank);
    }

    public int getNumberOfSubfronts() {
        return this.rankedSubpopulations.size();
    }


}

