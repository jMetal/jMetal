package org.uma.jmetal.operator.impl.selection;

import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetal.util.solutionattribute.impl.PreferenceDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingAndPreferenceSelection<S extends Solution<?>>
        implements SelectionOperator<List<S>,List<S>> {
    private final int solutionsToSelect ;
    private List<Double> interestPoint;
    private double epsilon;
    /** Constructor */
    public RankingAndPreferenceSelection(int solutionsToSelect,List<Double> interestPoint,double epsilon) {
        this.solutionsToSelect = solutionsToSelect ;
        this.interestPoint = interestPoint;
        this.epsilon =epsilon;
    }

    /* Getter */
    public int getNumberOfSolutionsToSelect() {
        return solutionsToSelect;
    }

    @Override
    public List<S> execute(List<S> solutionList) {
        if (null == solutionList) {
            throw new JMetalException("The solution list is null");
        } else if (solutionList.isEmpty()) {
            throw new JMetalException("The solution list is empty") ;
        }  else if (solutionList.size() < solutionsToSelect) {
            throw new JMetalException("The population size ("+solutionList.size()+") is smaller than" +
                    "the solutions to selected ("+solutionsToSelect+")")  ;
        }

        Ranking<S> ranking = new DominanceRanking<S>();
        ranking.computeRanking(solutionList) ;

        return preferenceDistanceSelection(ranking,solutionList.get(0).getNumberOfObjectives());
    }
    protected List<S> preferenceDistanceSelection(Ranking<S> ranking,int numberOfObjectives) {
        int nInteresPoint=this.interestPoint.size()/numberOfObjectives;

            List<S> population = new ArrayList<>(solutionsToSelect);

            while (population.size() < solutionsToSelect) {
                int indexPoint=0;
                for(int n = 0;(n<nInteresPoint)&& (population.size() < solutionsToSelect);n++) {
                    List<S> auxPopulation = new ArrayList<>(solutionsToSelect/nInteresPoint);
                    List<Double> auxInterestPoint = nextInterestPoint(indexPoint,numberOfObjectives);
                    indexPoint+=numberOfObjectives;
                    PreferenceDistance<S> preferenceDistance = new PreferenceDistance<>(auxInterestPoint, epsilon);
                    int rankingIndex = 0;
                    while ((auxPopulation.size() < (solutionsToSelect/nInteresPoint))&& (population.size() < solutionsToSelect)) {
                        if (subfrontFillsIntoThePopulation(ranking, rankingIndex, auxPopulation)) {
                            addRankedSolutionsToPopulation(ranking, rankingIndex, auxPopulation);
                            rankingIndex++;
                        } else {
                            preferenceDistance.computeDensityEstimator(ranking.getSubfront(rankingIndex));
                            addLastRankedSolutionsToPopulation(ranking, rankingIndex, auxPopulation);
                        }
                    }
                    population.addAll(auxPopulation);
            }
        }
        PreferenceDistance<S> preferenceDistance  = new PreferenceDistance<>(interestPoint, epsilon);
        population=preferenceDistance.epsilonClean(population);
        return population ;
    }

    protected boolean subfrontFillsIntoThePopulation(Ranking<S> ranking, int rank, List<S> population) {
        return ranking.getSubfront(rank).size() < (solutionsToSelect - population.size()) ;
    }

    protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
        List<S> front ;

        front = ranking.getSubfront(rank);

        for (int i = 0 ; i < front.size(); i++) {
            population.add(front.get(i));
        }
    }

    protected void addLastRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S>population) {
        List<S> currentRankedFront = ranking.getSubfront(rank) ;

        Collections.sort(currentRankedFront, new CrowdingDistanceComparator<S>()) ;

        int i = 0 ;
        while (population.size() < solutionsToSelect) {
            population.add(currentRankedFront.get(i)) ;
            i++ ;
        }
    }
    private List<Double> nextInterestPoint(int index, int size){
        List<Double> result= null;
        if(index<this.interestPoint.size()){
            result = new ArrayList<>(size);
            for(int i=0;i<size;i++){
                result.add(this.interestPoint.get(index));
                index++;
            }
        }
        return  result;
    }
}
