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
    private List<Double> referencePoint;
    private List<List<S>> rankedSubpopulations;
    private int numberOfRanks = 0;
    private double epsilon ;

    public RNSGAIIRanking(PreferenceNSGAII<S> utilityFunctions, double epsilon,List<Double> interestPoint) {
        this.utilityFunctions = utilityFunctions;
        this.epsilon = epsilon;
        referencePoint = interestPoint;
    }

    @Override
    public Ranking<S> computeRanking(List<S> population) {
        int size = population.size();
        List<Double> upperBound = new ArrayList<>();
        List<Double> lowerBound = new ArrayList<>();
        int numberObjectives = population.get(0).getNumberOfObjectives();
        //get bounds
        for (int i = 0; i < population.get(0).getNumberOfObjectives(); i++) {
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


        //number of  reference points
        int numberOfPoint = this.referencePoint.size() / numberObjectives;
        int indexReference =0;
        this.numberOfRanks = population.size()+1;
        this.rankedSubpopulations = new ArrayList(this.numberOfRanks);
        for (int i=0; i<numberOfRanks-1;i++){
            this.rankedSubpopulations.add(new ArrayList<>());
        }
        for (int i = 0; i < numberOfPoint ; i++) {
            //for each reference point, it calculates the euclidean distance
            List<Double> interestPoint = nextInterestPoint(indexReference,numberObjectives);
            indexReference += numberObjectives;
            this.utilityFunctions.updatePointOfInterest(interestPoint);
            SortedMap<Double,S> map = new TreeMap<>();
            for (S solution: temporalList) {
                double value = this.utilityFunctions.evaluate(solution).doubleValue();
                map.put(value,solution);
            }
            int rank=0;
            List<S> populationOrder = new ArrayList<>(map.values());
            for (S solution:
                    populationOrder) {
                this.setAttribute(solution, rank);
                this.rankedSubpopulations.get(rank).add(solution);
                rank++;
            }
        }
        while(!temporalList.isEmpty()){
            int indexRandom =JMetalRandom.getInstance().nextInt(0,temporalList.size()-1);//0
            S solutionRandom = temporalList.get(indexRandom);
            temporalList.remove(indexRandom);
            for(int i=0;i<temporalList.size();i++){
                S solution = temporalList.get(i);
                double sum = this.preference(solutionRandom,solution,upperBound,lowerBound);
                if(sum < epsilon){
                    removeRank(solution);
                    //assign the last rank
                    this.setAttribute(solution, this.rankedSubpopulations.size()-1);

                    List<S> rankListAux= this.rankedSubpopulations.get(this.rankedSubpopulations.size()-1 );
                    if(rankListAux==null){
                        rankListAux= new ArrayList();
                    }
                    rankListAux.add(solution);
                    temporalList.remove(i);
                }
            }

        }

        return this;
    }

    private List<Double> nextInterestPoint(int index, int size){
        List<Double> result= null;
        if(index<this.referencePoint.size()){
            result = new ArrayList<>(size);
            for(int i=0;i<size;i++){
                result.add(this.referencePoint.get(index));
                index++;
            }
        }
        return  result;
    }

    private void removeRank(S solution){
        boolean enc=false;
        int i=0;
        while(i< this.rankedSubpopulations.size()){
            enc= this.rankedSubpopulations.get(i).contains(solution);
            if(enc){
                this.rankedSubpopulations.get(i).remove(solution);
            }
            i++;
        }
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

