package org.uma.jmetal.algorithm.multiobjective.rnsgaii.util;


import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;

import java.util.*;

public class RNSGAIIRanking <S extends Solution<?>> extends GenericSolutionAttribute<S, Integer>
        implements Ranking<S> {

    private PreferenceNSGAII<S> utilityFunctions;
    private List<Double> referencePoint;
    private List<List<S>> rankedSubpopulations;
    private int numberOfRanks = 0;
    private double epsilon ;
    private NumberOfViolatedConstraints<S> numberOfViolatedConstraints ;

    public RNSGAIIRanking(PreferenceNSGAII<S> utilityFunctions, double epsilon,List<Double> interestPoint) {
        this.utilityFunctions = utilityFunctions;
        this.epsilon = epsilon;
        referencePoint = interestPoint;
        this.numberOfViolatedConstraints = new NumberOfViolatedConstraints<S>() ;

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
        int indexReference =0;
        this.numberOfRanks = population.size()+1;
        this.rankedSubpopulations = new ArrayList(this.numberOfRanks);
        for (int i=0; i<numberOfRanks-1;i++){
            this.rankedSubpopulations.add(new ArrayList<>());
        }
       // for (int i = 0; i < numberOfPoint ; i++) {
            //for each reference point, it calculates the euclidean distance
         //   List<Double> interestPoint = nextInterestPoint(indexReference,numberObjectives);
            indexReference += numberObjectives;
            this.utilityFunctions.updatePointOfInterest(referencePoint);
            SortedMap<Double,List<S>> map = new TreeMap<>();
            for (S solution: temporalList) {
                double value = this.utilityFunctions.evaluate(solution).doubleValue();

                //if(nConstrains!=null && nConstrains==0) {
                    List<S> auxiliar = map.get(value);
                    if (auxiliar == null) {
                        auxiliar = new ArrayList<>();
                    }
                    auxiliar.add(solution);
                    map.put(value, auxiliar);
               // }
            }
            int rank=0;

            List<List<S>> populationOrder = new ArrayList<>(map.values());
            for (List<S> solutionList:
                    populationOrder) {
                for (S solution:
                     solutionList) {
                    Integer nConstrains = numberOfViolatedConstraints.getAttribute(solution);
                    if((nConstrains!=null && nConstrains==0)|| nConstrains==null) {
                        this.setAttribute(solution, rank);
                        this.rankedSubpopulations.get(rank).add(solution);
                    }else{
                        this.setAttribute(solution, Integer.MAX_VALUE);
                        this.rankedSubpopulations.get(numberOfRanks-2).add(solution);
                    }
                }
                rank++;
            }
     //   }
        while(!temporalList.isEmpty()){
            int indexRandom =JMetalRandom.getInstance().nextInt(0,temporalList.size()-1);//0
            S solutionRandom = temporalList.get(indexRandom);
            temporalList.remove(indexRandom);
            for(int i=0;i<temporalList.size();i++){
                S solution = temporalList.get(i);
                double sum = this.preference(solutionRandom,solution,upperBound,lowerBound);
                if(sum < epsilon){
                   // removeRank(solution);
                    //assign the last rank
                    this.setAttribute(solution, Integer.MAX_VALUE);

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


    private void removeRank(S solution){
        boolean enc=false;
        int i=0;
        while(i< this.rankedSubpopulations.size()){
           // enc= this.rankedSubpopulations.get(i).contains(solution);
            while(this.rankedSubpopulations.get(i).contains(solution)){
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

