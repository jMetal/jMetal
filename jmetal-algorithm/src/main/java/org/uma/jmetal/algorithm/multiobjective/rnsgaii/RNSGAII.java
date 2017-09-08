package org.uma.jmetal.algorithm.multiobjective.rnsgaii;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.util.ReferencePoint;
import org.uma.jmetal.algorithm.multiobjective.rnsgaii.util.RNSGAIIRanking;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.PreferenceDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.util.distance.EuclideanDistance;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetal.util.solutionattribute.impl.PreferenceDistance;

import java.util.*;

public class RNSGAII <S extends Solution<?>> extends NSGAII<S> {

    protected List<Double> interestPoint = null;
    protected Vector<Integer> numberOfDivisions  ;
    protected List<Point> referencePoints  = null;
    protected double epsilon;
    /**
     * Constructor
     *
     * @param problem
     * @param maxEvaluations
     * @param populationSize
     * @param crossoverOperator
     * @param mutationOperator
     * @param selectionOperator
     * @param evaluator
     */
    public RNSGAII(Problem<S> problem, int maxEvaluations, int populationSize, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator,List<Double> referencePoint, double epsilon) {
        super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        interestPoint = referencePoint;
        this.epsilon = epsilon;
        referencePoints = new ArrayList<>() ;
        updateReferencePoint(interestPoint);
    }

    public void updateReferencePoint(List<Double> pointList){
        int cont = pointList.size()/2;
        int i=0;
        for (int j = 0; j < cont ; j++) {
            Point point = new ArrayPoint(2);
            point.setDimensionValue(0,pointList.get(i));
            i++;
            point.setDimensionValue(1,pointList.get(i));
            referencePoints.add(point);
        }
    }



    @Override protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        List<S> jointPopulation = new ArrayList<>();
        jointPopulation.addAll(population);
        jointPopulation.addAll(offspringPopulation);

        Ranking<S> ranking = computeRanking(jointPopulation);

        return preferenceDistancieSelection(ranking);
    }

    @Override public List<S> getResult() {
        return getNonDominatedSolutions(getPopulation());
    }

    @Override
    protected Ranking<S> computeRanking(List<S> solutionList) {
        Ranking<S> ranking = new DominanceRanking<S>();//RNSGAIIRanking<>(referencePoints);
        ranking.computeRanking(solutionList);

        return ranking;
    }

    protected List<S> preferenceDistancieSelection(Ranking<S> ranking) {

        CrowdingDistance<S> crowdingDistance = new CrowdingDistance<S>();
        List<S> population = new ArrayList<>(getMaxPopulationSize());
        int rankingIndex = 0;
        while (populationIsNotFull(population)) {
            if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
                addRankedSolutionsToPopulation(ranking, rankingIndex, population);
                rankingIndex++;
            } else {
                crowdingDistance.computeDensityEstimator(ranking.getSubfront(rankingIndex));
                addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
            }
        }
        for (Point reference:referencePoints) {
            Collections.sort(population,new PreferenceDistanceComparator(reference));
        }
        return population;
    }
    @Override
    protected boolean populationIsNotFull(List<S> population) {
        return population.size() < getMaxPopulationSize();
    }

    @Override
    protected boolean subfrontFillsIntoThePopulation(Ranking<S> ranking, int rank, List<S> population) {
        return ranking.getSubfront(rank).size() < (getMaxPopulationSize() - population.size());
    }

    @Override
    protected void addLastRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
        List<S> currentRankedFront = ranking.getSubfront(rank);

        for (Point reference:referencePoints) {
            Collections.sort(population,new PreferenceDistanceComparator(reference));
        }

        int i = 0;
        while (population.size() < getMaxPopulationSize()) {
            population.add(currentRankedFront.get(i));
            i++;
        }
    }
    @Override
    protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
        List<S> front;

        front = ranking.getSubfront(rank);


        for (S solution : front) {
            population.add(solution);
        }
    }


    @Override
    protected List<S> getNonDominatedSolutions(List<S> solutionList) {
        return SolutionListUtils.getNondominatedSolutions(solutionList);
    }

    @Override public String getName() {
        return "RNSGAII" ;
    }

    @Override public String getDescription() {
        return "Nondominated Sorting Genetic Algorithm version II with Reference Point" ;
    }
}
