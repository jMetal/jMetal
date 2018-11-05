package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import java.util.Collections;
import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.distance.impl.DistanceBetweenSolutionAndKNearestNeighbors;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NSGAIINovelty2 extends NSGAII<DoubleSolution> {
  protected final int maxEvaluations;

  protected final SolutionListEvaluator<DoubleSolution> evaluator;

  protected int evaluations;
  protected Comparator<DoubleSolution> dominanceComparator ;

  protected List<DoubleSolution> noveltyArchive ;
  protected double noveltyThreshold ;
  protected int noveltySetSize ;
  private DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution, List<DoubleSolution>> distance ;


  /**
   * Constructor
   */
  public NSGAIINovelty2(Problem<DoubleSolution> problem, int maxEvaluations, int populationSize,
                        CrossoverOperator<DoubleSolution> crossoverOperator, MutationOperator<DoubleSolution> mutationOperator,
                        SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator, SolutionListEvaluator<DoubleSolution> evaluator) {
    this(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator,
        new DominanceComparator<DoubleSolution>(), evaluator);
  }
  /**
   * Constructor
   */
  public NSGAIINovelty2(Problem<DoubleSolution> problem, int maxEvaluations, int populationSize,
                        CrossoverOperator<DoubleSolution> crossoverOperator, MutationOperator<DoubleSolution> mutationOperator,
                        SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator,
                        Comparator<DoubleSolution> dominanceComparator,
                        SolutionListEvaluator<DoubleSolution> evaluator) {
    super(problem,
        maxEvaluations,
        populationSize,
        crossoverOperator,
        mutationOperator,
        selectionOperator,
        dominanceComparator,
        evaluator);
    this.maxEvaluations = maxEvaluations;
    setMaxPopulationSize(populationSize); ;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;

    this.evaluator = evaluator;
    this.dominanceComparator = dominanceComparator ;

    int k = 5 ;
    this.distance = new DistanceBetweenSolutionAndKNearestNeighbors<>(k) ;

    this.noveltyArchive = new ArrayList<>() ;
    this.noveltyThreshold = 0.1 ;
    this.noveltySetSize = 10 ;
    this.distance = new DistanceBetweenSolutionAndKNearestNeighbors<>(k) ;
  }

  @Override protected void updateProgress() {
    super.updateProgress();

    //getPopulation()
    //    .stream()
    //    .forEach(solution -> solution.setAttribute("KNNDistance", distance.getDistance(solution, getPopulation())));

    for (DoubleSolution solution : getPopulation()) {
      solution.setAttribute("KNNDistance", distance.getDistance(solution, getPopulation())) ;
    }

    getPopulation().forEach(s -> System.out.println(s.getAttribute("KNNDistance")));
    System.out.println("----") ;


    getPopulation().sort((s1, s2) -> {
      double distance1 = (double)s1.getAttribute("KNNDistance") ;
      double distance2 = (double)s2.getAttribute("KNNDistance") ;

      if (distance1 == distance2) {
        return 0;
      } else if (distance1 < distance2) {
        return 1;
      } else {
        return -1;
      }
    }) ;
    //getPopulation().sort(Comparator.comparing(DoubleSolution::getAttribute("KNNDistance"))) ;
    //Collections.sort(getPopulation(), (s1, s2) -> {
    //  if (s1.getAttribute("KNNDistance") == (s2.getAttribute("KNNDistance"))) return 0 ;
    //else if ((double)s1.getAttribute("KNNDistance") > ((double)s2.getAttribute("KNNDistance"))) return 1 ;
    //else return 0 ;});

    //for (DoubleSolution solution : getPopulation()) {
    //  System.out.println(solution.getAttribute("KNNDistance")) ;
    //}

    getPopulation().forEach(s -> System.out.println(s.getAttribute("KNNDistance")));
    int a = 1 ;
    System.exit(0) ;
  }


  @Override public String getName() {
    return "NSGAII Novelty 2" ;
  }

  @Override public String getDescription() {
    return "Nondominated Sorting Genetic Algorithm version II Novelty 2" ;
  }
}
