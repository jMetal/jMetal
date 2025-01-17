package org.uma.jmetal.component.algorithm.multiobjective;

import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.SMSEMOAReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.RandomSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.DifferentialEvolutionCrossoverVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerBoundedSequenceGenerator;

/**
 * Class to configure and build an instance of the SMS-EMOA algorithm
 *
 * @param <DoubleSolution>
 */
public class SMSEMOADEBuilder {
  private String name;
  private Ranking<DoubleSolution> ranking;
  private Evaluation<DoubleSolution> evaluation;
  private SolutionsCreation<DoubleSolution> createInitialPopulation;
  private Termination termination;
  private Selection<DoubleSolution> selection;
  private Variation<DoubleSolution> variation;
  private Replacement<DoubleSolution> replacement;

  public SMSEMOADEBuilder(
          Problem<DoubleSolution> problem, 
          int populationSize,
          double cr,
          double f,
          MutationOperator<DoubleSolution> mutation,
          DifferentialEvolutionCrossover.DE_VARIANT differentialEvolutionVariant) {
    name = "SMS-EMOA-DE";

    ranking = new FastNonDominatedSortRanking<>();
    var sequenceGenerator = new IntegerBoundedSequenceGenerator(populationSize);

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    Hypervolume<DoubleSolution> hypervolume = new PISAHypervolume<>();
    this.replacement = new SMSEMOAReplacement<>(ranking, hypervolume);

    DifferentialEvolutionCrossover crossover =
            new DifferentialEvolutionCrossover(
                    cr, f, differentialEvolutionVariant);

    this.variation =
            new DifferentialEvolutionCrossoverVariation(
                    1, crossover, mutation, sequenceGenerator);

    int numberOfParentsToSelect = crossover.numberOfRequiredParents() ;
    this.selection =
            new DifferentialEvolutionSelection(variation.getMatingPoolSize(), numberOfParentsToSelect, false,
                    sequenceGenerator);
    
    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public SMSEMOADEBuilder setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public SMSEMOADEBuilder setRanking(Ranking<DoubleSolution> ranking) {
    this.ranking = ranking;
    Hypervolume<DoubleSolution> hypervolume = new PISAHypervolume<>();
    this.replacement = new SMSEMOAReplacement<>(ranking, hypervolume);

    return this;
  }

  public SMSEMOADEBuilder setEvaluation(Evaluation<DoubleSolution> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public EvolutionaryAlgorithm<DoubleSolution> build() {
    return new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation, termination,
        selection, variation, replacement);
  }
}
