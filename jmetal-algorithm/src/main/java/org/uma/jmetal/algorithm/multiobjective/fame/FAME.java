package org.uma.jmetal.algorithm.multiobjective.fame;

import com.fuzzylite.Engine;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.SteadyStateNSGAII;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.mutation.UniformMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.impl.SpatialSpreadDeviationArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.SpatialSpreadDeviationComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetal.util.solutionattribute.impl.SpatialSpreadDeviation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class implements the FAME algorithm described in: A. Santiago, B. Dorronsoro, A.J. Nebro,
 * J.J. Durillo, O. Castillo, H.J. Fraire A Novel Multi-Objective Evolutionary Algorithm with Fuzzy
 * Logic Based Adaptive Selection of Operators: FAME. Information Sciences. Volume 471, January
 * 2019, Pages 233-251. DOI: https://doi.org/10.1016/j.ins.2018.09.005
 *
 * @author Alejandro Santiago <aurelio.santiago@upalt.edu.mx>
 */
public class FAME<S extends Solution<?>> extends SteadyStateNSGAII<S> {
  private double[] Utilization;
  private double[] OpProb;
  private int operators = 4;
  private int windowSize;
  private int window;
  private double Stagnation = 0.0;
  private SpatialSpreadDeviationArchive archiveSSD;
  Engine engine;
  InputVariable operatoruse, stagnation;
  OutputVariable probability;
  /** Constructor */
  public FAME(
      Problem<S> problem,
      int populationSize,
      int archiveSize,
      int maxEvaluations,
      SelectionOperator<List<S>, S> selectionOperator,
      SolutionListEvaluator<S> evaluator) {
    super(
        problem,
        maxEvaluations,
        populationSize,
        null,
        null,
        selectionOperator,
        new DominanceComparator<>(),
        evaluator);
    archiveSSD = new SpatialSpreadDeviationArchive(archiveSize);
    OpProb = new double[operators];
    Utilization = new double[operators];
    windowSize = (int) Math.ceil(3.33333 * operators);
    for (int x = 0; x < operators; x++) {
      OpProb[x] = (1.0);
      Utilization[x] = 0.0;
    }
    loadFIS();
    System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
  }

  private void loadFIS() {
    engine = new Engine();
    engine.setName("Probabilides-operadores");
    stagnation = new InputVariable();
    stagnation.setName("Stagnation");
    stagnation.setRange(0.0, 1.0);
    stagnation.addTerm(new Triangle("LOW", -0.4, 0.0, 0.4));
    stagnation.addTerm(new Triangle("MID", 0.1, 0.5, 0.9));
    stagnation.addTerm(new Triangle("HIGH", 0.6, 1.0, 1.4));
    engine.addInputVariable(stagnation);

    operatoruse = new InputVariable();
    operatoruse.setName("OperatorUse");
    operatoruse.setRange(0.0, 1.0);
    operatoruse.addTerm(new Triangle("LOW", -0.4, 0.0, 0.4));
    operatoruse.addTerm(new Triangle("MID", 0.1, 0.5, 0.9));
    operatoruse.addTerm(new Triangle("HIGH", 0.6, 1.0, 1.4));
    engine.addInputVariable(operatoruse);

    probability = new OutputVariable();
    probability.setName("Probability");
    probability.setRange(0.0, 1.0);
    probability.addTerm(
        new Triangle(
            "LOW", -0.4, 0.0,
            0.4)); // probability.addTerm(new Triangle("LOW", 0.000, 0.250, 0.500));
    probability.addTerm(
        new Triangle(
            "MID", 0.1, 0.5,
            0.9)); // probability.addTerm(new Triangle("MID", 0.250, 0.500, 0.750));
    probability.addTerm(
        new Triangle(
            "HIGH", 0.6, 1.0,
            1.4)); // probability.addTerm(new Triangle("HIGH", 0.500, 0.750, 1.000));
    engine.addOutputVariable(probability);

    RuleBlock ruleBlock = new RuleBlock();
    ruleBlock.addRule(
        Rule.parse(
            "if Stagnation is HIGH and OperatorUse is HIGH then Probability is MID", engine));
    ruleBlock.addRule(
        Rule.parse("if Stagnation is HIGH and OperatorUse is LOW then Probability is MID", engine));
    ruleBlock.addRule(
        Rule.parse("if Stagnation is HIGH and OperatorUse is MID then Probability is LOW", engine));
    ruleBlock.addRule(
        Rule.parse("if Stagnation is MID and OperatorUse is HIGH then Probability is MID", engine));
    ruleBlock.addRule(
        Rule.parse("if Stagnation is MID and OperatorUse is LOW then Probability is MID", engine));
    ruleBlock.addRule(
        Rule.parse("if Stagnation is MID and OperatorUse is MID then Probability is LOW", engine));
    ruleBlock.addRule(
        Rule.parse(
            "if Stagnation is LOW and OperatorUse is HIGH then Probability is HIGH", engine));
    ruleBlock.addRule(
        Rule.parse("if Stagnation is LOW and OperatorUse is LOW then Probability is LOW", engine));
    ruleBlock.addRule(
        Rule.parse("if Stagnation is LOW and OperatorUse is MID then Probability is MID", engine));
    engine.addRuleBlock(ruleBlock);

    engine.configure("Minimum", "Maximum", "Minimum", "Maximum", "Centroid");

    StringBuilder status = new StringBuilder();
    if (!engine.isReady(status)) {
      throw new RuntimeException(
          "Engine not ready. " + "The following errors were encountered:\n" + status.toString());
    }
  }

  @Override
  protected void updateProgress() {
    evaluations++;
    if (window == windowSize) {
      for (int x = 0; x < operators; x++) {
        engine.setInputValue("Stagnation", Stagnation);
        engine.setInputValue("OperatorUse", Utilization[x]);
        engine.process();

        OpProb[x] = probability.getOutputValue();
        Utilization[x] = 0.0;
      }
      window = 0;
      Stagnation = 0.0;
    }
  }

  @Override
  protected List<S> selection(List<S> population) {
    List<S> matingPopulation = new ArrayList<>(3);
    for (int x = 0; x < 3; x++) {
      double aleat = Math.random();
      if (aleat <= 0.1) // 0.1 n_n wiiii (0.1 lo hace parecido a measo1 (0.05 asco))
      matingPopulation.add((S) selectionOperator.execute(population));
      else {
        matingPopulation.add((S) selectionOperator.execute(archiveSSD.getSolutionList()));
      }
    }

    return matingPopulation;
  }

  @Override
  protected List<S> reproduction(List<S> population) {
    List<S> offspringPopulation = new ArrayList<>(1);
    List<S> parents = null;

    double probabilityPolynomial, DristributionIndex;
    probabilityPolynomial = 0.30;
    DristributionIndex = 20;
    Operator mutationPolynomial = new PolynomialMutation(probabilityPolynomial, DristributionIndex);

    double probabilityUniform, perturbation;
    probabilityUniform = 0.30;
    perturbation = 0.1;
    Operator mutationUniform = new UniformMutation(probabilityUniform, perturbation);

    double CR, F;
    CR = 1.0;
    F = 0.5;
    DifferentialEvolutionCrossover crossoverOperator_DE =
        new DifferentialEvolutionCrossover(CR, F, "rand/1/bin");

    double crossoverProbability, crossoverDistributionIndex;
    crossoverProbability = 1.0;
    crossoverDistributionIndex = 20.0;
    Operator crossoverOperator_SBX =
        new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    Random rnd = new Random();
    int operator = rnd.nextInt(operators);
    List<S> offspring = new ArrayList<>(1);
    // ROULETTE
    double cont = 0;
    for (int x = 0; x < operators; x++) {
      cont += OpProb[x];
    }
    while (cont > 0) {
      cont -= OpProb[operator];
      if (cont <= 0) {
        break;
      } else {
        operator++;
        if (operator == OpProb.length) operator = 0;
      }
    }
    // seleccion del operator
    // operador=2;
    boolean flag_dep;
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
        Utilization[0] += 1.0 / windowSize;
        flag_dep = archiveSSD.add(offspring.get(0));
        if (!flag_dep) {
          Stagnation += 1.0 / windowSize;
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
        Utilization[1] += 1.0 / windowSize;
        flag_dep = archiveSSD.add(offspring.get(0));
        if (!flag_dep) {
          Stagnation += 1.0 / windowSize;
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
        Utilization[2] += 1.0 / windowSize;
        flag_dep = archiveSSD.add(offspring.get(0));
        if (!flag_dep) {
          Stagnation += 1.0 / windowSize;
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
        Utilization[3] += 1.0 / windowSize;
        flag_dep = archiveSSD.add(offspring.get(0));
        if (!flag_dep) {
          Stagnation += 1.0 / windowSize;
        }
        break;
      default:
        break;
    }
    window++;
    return offspringPopulation;
  }

  @Override
  public String getName() {
    return "FAME";
  }

  @Override
  public String getDescription() {
    return "FAME ultima version";
  }

  @Override
  protected List<S> createInitialPopulation() {
    SpatialSpreadDeviation distancia = new SpatialSpreadDeviation();
    List<S> population = new ArrayList<>(getMaxPopulationSize());
    for (int i = 0; i < getMaxPopulationSize(); i++) {
      S newIndividual = getProblem().createSolution();
      distancia.setAttribute(newIndividual, 0.0);
      population.add(newIndividual);
    }
    evaluator.evaluate(population, getProblem());
    for (int i = 0; i < getMaxPopulationSize(); i++) {
      archiveSSD.add(population.get(i));
    }
    return population;
  }

  @Override
  public List<S> getResult() {
    return SolutionListUtils.getNondominatedSolutions(archiveSSD.getSolutionList());
  }

  @Override
  protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);
    Ranking<S> ranking = new DominanceRanking<>();
    ranking.computeRanking(jointPopulation);

    return fast_nondonimated_sort(ranking);
  }

  protected List<S> fast_nondonimated_sort(Ranking<S> ranking) {
    SpatialSpreadDeviation<S> SSD = new SpatialSpreadDeviation<S>();
    List<S> population = new ArrayList<>(getMaxPopulationSize());
    int rankingIndex = 0;
    while (populationIsNotFull(population)) {
      if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
        addRankedSolutionsToPopulation(ranking, rankingIndex, population);
        rankingIndex++;
      } else {
        SSD.computeDensityEstimator(ranking.getSubfront(rankingIndex));
        addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
      }
    }

    return population;
  }

  protected boolean populationIsNotFull(List<S> population) {
    return population.size() < getMaxPopulationSize();
  }

  protected boolean subfrontFillsIntoThePopulation(
      Ranking<S> ranking, int rank, List<S> population) {
    return ranking.getSubfront(rank).size() < (getMaxPopulationSize() - population.size());
  }

  protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
    List<S> front;

    front = ranking.getSubfront(rank);

    for (S solution : front) {
      population.add(solution);
    }
  }

  protected void addLastRankedSolutionsToPopulation(
      Ranking<S> ranking, int rank, List<S> population) {
    List<S> currentRankedFront = ranking.getSubfront(rank);

    Collections.sort(currentRankedFront, new SpatialSpreadDeviationComparator<>());

    int i = 0;
    while (population.size() < getMaxPopulationSize()) {
      population.add(currentRankedFront.get(i));
      i++;
    }
  }
}
