package org.uma.jmetal.algorithm.multiobjective.microfame;

import generic.Input;
import generic.Output;
import generic.Tuple;
import org.uma.jmetal.algorithm.multiobjective.microfame.util.WFGHypervolumeV2;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.SteadyStateNSGAII;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.mutation.impl.UniformMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import type1.sets.T1MF_Gauangle;
import type1.system.T1_Antecedent;
import type1.system.T1_Consequent;
import type1.system.T1_Rule;
import type1.system.T1_Rulebase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** @author Alejandro Santiago <aurelio.santiago@upalt.edu.mx> Micro-FAME */
public class MicroFAME<S extends Solution<?>> extends SteadyStateNSGAII<S> {
  private double operators_use[];
  private double operators_desirability[];
  private int operators_num = 4;
  private int window_size;
  private int contador_ventana;
  private double estancamiento = 0.0;
  private HypervolumeArchive archive_hv;
  Input Stagnation, Operatoruse;
  Output Probability;
  T1_Rulebase rulebase;

  /** Constructor */
  public MicroFAME(
      Problem<S> problem,
      int maxEvaluations,
      int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator) {
    super(
        problem,
        maxEvaluations,
        populationSize,
        crossoverOperator,
        mutationOperator,
        selectionOperator,
        new RankingAndCrowdingDistanceComparator<>());
    archive_hv = new HypervolumeArchive(populationSize, new WFGHypervolumeV2());
    operators_desirability = new double[operators_num];
    operators_use = new double[operators_num];
    window_size = (int) Math.ceil(3.33333 * operators_num);
    System.out.println("Window size: " + window_size);
    for (int x = 0; x < operators_num; x++) {
      operators_desirability[x] = (1.0);
      operators_use[x] = 0.0;
    }
    load_fuzzy_motor();
  }

  private void load_fuzzy_motor() {

    Stagnation = new Input("Estancamiento", new Tuple(0, 1.0));
    Operatoruse = new Input("UsoOperador", new Tuple(0, 1.0));
    Probability = new Output("Probabiliadad", new Tuple(0, 1.0));
    T1MF_Gauangle lowStagnationUMF =
        new T1MF_Gauangle("Upper MF for low Stagnation", -0.4, 0.0, 0.4);
    T1MF_Gauangle midStagnationUMF =
        new T1MF_Gauangle("Upper MF for mid Stagnation", 0.1, 0.5, 0.9);
    T1MF_Gauangle highStagnationUMF =
        new T1MF_Gauangle("Upper MF for high Stagnation", 0.6, 1.0, 1.4);

    T1MF_Gauangle lowOperatoruseUMF =
        new T1MF_Gauangle("Upper MF for low Operatoruse", -0.4, 0.0, 0.4);
    T1MF_Gauangle midOperatoruseUMF =
        new T1MF_Gauangle("Upper MF for mid Operatoruse", 0.1, 0.5, 0.9);
    T1MF_Gauangle highOperatoruseUMF =
        new T1MF_Gauangle("Upper MF for highOperatoruse", 0.6, 1.0, 1.4);

    T1MF_Gauangle lowProbabilityUMF =
        new T1MF_Gauangle("Upper MF for low Probability", -0.4, 0.0, 0.4);
    T1MF_Gauangle midProbabilityUMF =
        new T1MF_Gauangle("Upper MF for mid Probability", 0.1, 0.5, 0.9);
    T1MF_Gauangle highProbabilityUMF =
        new T1MF_Gauangle("Upper MF for high Probability", 0.6, 1.0, 1.4);

    // SETUP ANTECEDENTS AND CONSECUENTS
    T1_Antecedent lowStagnation = new T1_Antecedent("LowStagnation", lowStagnationUMF, Stagnation);
    T1_Antecedent midStagnation = new T1_Antecedent("MidStagnation", midStagnationUMF, Stagnation);
    T1_Antecedent highStagnation =
        new T1_Antecedent("HighStagnation", highStagnationUMF, Stagnation);

    T1_Antecedent lowOperatoruse =
        new T1_Antecedent("LowStagnation", lowOperatoruseUMF, Stagnation);
    T1_Antecedent midOperatoruse =
        new T1_Antecedent("MidStagnation", midOperatoruseUMF, Stagnation);
    T1_Antecedent highOperatoruse =
        new T1_Antecedent("HighStagnation", highOperatoruseUMF, Stagnation);

    T1_Consequent lowProbability =
        new T1_Consequent("lowProbability", lowProbabilityUMF, Probability);
    T1_Consequent midProbability =
        new T1_Consequent("MidProbability", midProbabilityUMF, Probability);
    T1_Consequent highProbability =
        new T1_Consequent("HighProbability", highProbabilityUMF, Probability);

    rulebase = new T1_Rulebase(9);
    rulebase.addRule(
        new T1_Rule(new T1_Antecedent[] {highStagnation, highOperatoruse}, midProbability));
    rulebase.addRule(
        new T1_Rule(new T1_Antecedent[] {highStagnation, midOperatoruse}, lowProbability));
    rulebase.addRule(
        new T1_Rule(new T1_Antecedent[] {highStagnation, lowOperatoruse}, midProbability));
    rulebase.addRule(
        new T1_Rule(new T1_Antecedent[] {midStagnation, highOperatoruse}, midProbability));
    rulebase.addRule(
        new T1_Rule(new T1_Antecedent[] {midStagnation, midOperatoruse}, lowProbability));
    rulebase.addRule(
        new T1_Rule(new T1_Antecedent[] {midStagnation, lowOperatoruse}, midProbability));
    rulebase.addRule(
        new T1_Rule(new T1_Antecedent[] {lowStagnation, highOperatoruse}, highProbability));
    rulebase.addRule(
        new T1_Rule(new T1_Antecedent[] {lowStagnation, midOperatoruse}, midProbability));
    rulebase.addRule(
        new T1_Rule(new T1_Antecedent[] {lowStagnation, lowOperatoruse}, lowProbability));
    // print out the rules
    System.out.println("\n" + rulebase);
  }

  @Override
  protected void updateProgress() {
    evaluations++;
    if (contador_ventana == window_size) {
      for (int x = 0; x < operators_num; x++) {
        Stagnation.setInput(estancamiento);
        Operatoruse.setInput(operators_use[x]);
        operators_desirability[x] = rulebase.evaluate(0).get(Probability);
        operators_use[x] = 0.0;
      }
      contador_ventana = 0;
      estancamiento = 0.0;
    }
  }

  @Override
  protected List<S> evaluatePopulation(List<S> population) {
    return population;
  }

  @Override
  protected List<S> selection(List<S> population) {
    List<S> matingPopulation = new ArrayList<>(3);
    for (int x = 0; x < 3; x++) {
      matingPopulation.add((S) selectionOperator.execute(archive_hv.getSolutionList()));
    }

    return matingPopulation;
  }

  @Override
  protected List<S> reproduction(List<S> population) {
    List<S> offspringPopulation = new ArrayList<>(1);
    List<S> parents = null;

    double probabilityPolynomial, DristributionIndex;
    probabilityPolynomial = 0.25;
    DristributionIndex = 20;
    Operator mutationPolynomial = new PolynomialMutation(probabilityPolynomial, DristributionIndex);

    double probabilityUniform, perturbation;
    probabilityUniform = 0.25;
    perturbation = 0.1;
    Operator mutationUniform = new UniformMutation(probabilityUniform, perturbation);

    double CR, F;
    CR = 1.0;
    F = 0.5;
    DifferentialEvolutionCrossover crossoverOperator_DE =
        new DifferentialEvolutionCrossover(
            CR, F, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    double crossoverProbability, crossoverDistributionIndex;
    crossoverProbability = 1.0;
    crossoverDistributionIndex = 20.0;
    Operator crossoverOperator_SBX =
        new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    Random rnd = new Random();
    int operator = rnd.nextInt(operators_num);
    List<S> offspring = new ArrayList<>(1);
    // RULETTA
    double counter = 0;
    for (int x = 0; x < operators_num; x++) {
      counter += operators_desirability[x];
    }
    while (counter > 0) {
      counter -= operators_desirability[operator];
      if (counter <= 0) {
        break;
      } else {
        operator++;
        if (operator == operators_desirability.length) operator = 0;
      }
    }
    // Evolutionary operator selection
    boolean flag_archive;
    switch (operator) {
        // DIFERENTIAL EVOLUTION
      case 0:
        parents = new ArrayList<>(3);
        parents.add(population.get(0));
        parents.add(population.get(1));
        parents.add(population.get(2));
        DoubleSolution solution = (DoubleSolution) population.get(2).copy();
        crossoverOperator_DE.setCurrentSolution(solution);
        offspring = (List<S>) crossoverOperator_DE.execute((List<DoubleSolution>) parents);
        evaluator.evaluate(offspring, getProblem());
        offspringPopulation.add(offspring.get(0));
        operators_use[0] += 1.0 / window_size;
        flag_archive = archive_hv.add(offspring.get(0));
        if (!flag_archive) {
          estancamiento += 1.0 / window_size;
        }
        break;
        // SBX Crossover
      case 1:
        parents = new ArrayList<>(2);
        parents.add(population.get(0));
        parents.add(population.get(1));
        offspring = (List<S>) crossoverOperator_SBX.execute(parents);
        evaluator.evaluate(offspring, getProblem());
        offspringPopulation.add(offspring.get(0));
        operators_use[1] += 1.0 / window_size;
        flag_archive = archive_hv.add(offspring.get(0));
        if (!flag_archive) {
          estancamiento += 1.0 / window_size;
        }
        break;
        // Polynomial mutation
      case 2:
        parents = new ArrayList<>(1);
        parents.add(population.get(0));
        offspring.add((S) (DoubleSolution) population.get(0).copy());
        mutationPolynomial.execute(offspring.get(0));
        evaluator.evaluate(offspring, getProblem());
        offspringPopulation.add(offspring.get(0));
        operators_use[2] += 1.0 / window_size;
        flag_archive = archive_hv.add(offspring.get(0));
        if (!flag_archive) {
          estancamiento += 1.0 / window_size;
        }
        break;
        // UNIFORM MUTATION
      case 3:
        parents = new ArrayList<>(1);
        parents.add(population.get(0));
        offspring.add((S) (DoubleSolution) population.get(0).copy());
        mutationUniform.execute(offspring.get(0));
        evaluator.evaluate(offspring, getProblem());
        offspringPopulation.add(offspring.get(0));
        operators_use[3] += 1.0 / window_size;
        flag_archive = archive_hv.add(offspring.get(0));
        if (!flag_archive) {
          estancamiento += 1.0 / window_size;
        }
        break;
      default:
        break;
    }
    contador_ventana++;
    return offspringPopulation;
  }

  @Override
  public String getName() {
    return "Micro-FAME";
  }

  @Override
  public String getDescription() {
    return "Micro-FAME last version";
  }

  @Override
  protected List<S> createInitialPopulation() {
    List<S> population = new ArrayList<>(archive_hv.getMaxSize());
    for (int i = 0; i < archive_hv.getMaxSize(); i++) {
      S newIndividual = getProblem().createSolution();
      population.add(newIndividual);
    }
    evaluator.evaluate(population, getProblem());
    for (int i = 0; i < archive_hv.getMaxSize(); i++) {
      archive_hv.add(population.get(i));
    }
    return null;
  }

  @Override
  public List<S> getResult() {
    return getNonDominatedSolutions(archive_hv.getSolutionList());
  }

  protected List<S> getNonDominatedSolutions(List<S> solutionList) {
    return SolutionListUtils.getNonDominatedSolutions(solutionList);
  }

  @Override
  protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    return null;
  }
}
