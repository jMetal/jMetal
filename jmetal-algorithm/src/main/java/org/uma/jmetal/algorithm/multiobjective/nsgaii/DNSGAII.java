package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.RankingAndDirScoreSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * created at 10:45 am, 2019/1/29
 * This algorithm (DIR-Enhanced NSGA-II [DNSGA-II]) is according to
 * "Cai X, Sun H, Fan Z. A diversity indicator based on reference vectors for many-objective optimization[J]. Information Sciences, 2018, 430-431:467-486.".
 * @author sunhaoran <nuaa_sunhr@yeah.net>
 */
@SuppressWarnings("serial")
public class DNSGAII<S extends Solution<?>> extends NSGAII<S> {

    private double[][] referenceVectors ;

    public DNSGAII(Problem<S> problem, int maxEvaluations,
                                           int populationSize, int matingPoolSize, int offspringPopulationSize,
                                           CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                                           SelectionOperator<List<S>,S> selectionOperator, Comparator<S> dominanceComparator,
                                           SolutionListEvaluator<S> evaluator) {
        super(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize,
                crossoverOperator, mutationOperator, selectionOperator, dominanceComparator, evaluator);
    }

    public void setReferenceVectors(double[][] referenceVectors) {
        this.referenceVectors = referenceVectors;
    }

    @Override
    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        List<S> jointPopulation = new ArrayList<>();
        jointPopulation.addAll(population);
        jointPopulation.addAll(offspringPopulation);
        RankingAndDirScoreSelection<S> rankingAndDirScoreSelection = new RankingAndDirScoreSelection<>(getMaxPopulationSize(), dominanceComparator, referenceVectors) ;
        return rankingAndDirScoreSelection.execute(jointPopulation) ;
    }

    @Override
    public String getName() {
        return "D-NSGA-II";
    }

    @Override
    public String getDescription() {
        return "DIR based NSGA-II";
    }
}
