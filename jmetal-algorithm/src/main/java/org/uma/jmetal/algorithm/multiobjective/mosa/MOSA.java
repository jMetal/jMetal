package org.uma.jmetal.algorithm.multiobjective.mosa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionStrategy;
import org.uma.jmetal.algorithm.multiobjective.mosa.cooling.CoolingScheme;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.GenericBoundedArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.densityestimator.impl.GridDensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 * This class implements a multi-objective simulated annealing algorithm.
 */
@SuppressWarnings("serial")
public class MOSA<S extends Solution<?>> extends AbstractEvolutionStrategy<S, List<S>> {
  protected int maxEvaluations;
  protected int evaluations;

  protected BoundedArchive<S> archive;
  protected Comparator<S> comparator;

  protected static final double minimumTemperature = 0.000001;
  protected double temperature;

  protected CoolingScheme coolingScheme;
  protected int numberOfWorstAcceptedSolutions = 0;

  /**
   * Constructor
   */
  public MOSA(
          Problem<S> problem,
          int maxEvaluations,
          BoundedArchive<S> archive,
          MutationOperator<S> mutationOperator,
          double initialTemperature,
          CoolingScheme coolingScheme) {
    super(problem);
    setProblem(problem);
    this.maxEvaluations = maxEvaluations;
    this.archive = archive;
    this.mutationOperator = mutationOperator;

    this.temperature = initialTemperature;
    this.coolingScheme = coolingScheme;

    comparator = new DominanceWithConstraintsComparator<S>();
  }

  public MOSA(
          Problem<S> problem,
          int maxEvaluations,
          int archiveSize,
          int biSections,
          MutationOperator<S> mutationOperator,
          double initialTemperature,
          CoolingScheme coolingScheme) {
    this(
            problem,
            maxEvaluations,
            new GenericBoundedArchive<>(
                    archiveSize, new GridDensityEstimator<>(biSections, problem.getNumberOfObjectives())),
            mutationOperator, initialTemperature, coolingScheme);
  }

  /* Getters */
  public BoundedArchive<S> getArchive() {
    return archive;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }

  @Override
  protected void initProgress() {
    evaluations = 1;
  }

  @Override
  protected void updateProgress() {
    evaluations++;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override
  protected List<S> createInitialPopulation() {
    List<S> solutionList = new ArrayList<>(1);
    solutionList.add(getProblem().createSolution());
    archive.add(solutionList.get(0));

    return solutionList;
  }

  @Override
  protected List<S> evaluatePopulation(List<S> population) {
    getProblem().evaluate(population.get(0));
    return population;
  }

  @Override
  protected List<S> selection(List<S> population) {
    return population;
  }

  @Override
  protected List<S> reproduction(List<S> population) {
    S mutatedSolution = (S) population.get(0).copy();
    mutationOperator.execute(mutatedSolution);

    List<S> mutationSolutionList = new ArrayList<>(1);
    mutationSolutionList.add(mutatedSolution);
    return mutationSolutionList;
  }

  @Override
  protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    S current = population.get(0);
    S mutatedSolution = (S) offspringPopulation.get(0).copy();

    int flag = comparator.compare(current, mutatedSolution);
    if (flag == 1) {
      current = mutatedSolution;
      archive.add(mutatedSolution);
    } else if (flag == 0) {
      if (archive.add(mutatedSolution)) {
        if (archive.getComparator().compare(current, mutatedSolution) > 0) {
          current = mutatedSolution;
        }
      }
    } else {
      double acceptanceProbability = compute_acceptance_probability(current, mutatedSolution, temperature);

      if (acceptanceProbability > JMetalRandom.getInstance().nextDouble()) {
        current = mutatedSolution;
        numberOfWorstAcceptedSolutions++;
      }
    }

    temperature = coolingScheme.updateTemperature(temperature, evaluations);

    population.set(0, current);
    return population;
  }

  protected double compute_acceptance_probability(S currentSolution, S mutatedSolution, double temperature) {
      double value = 0.0;
      int bound = currentSolution.objectives().length;
      for (int i = 0; i < bound; i++) {
          double v = (mutatedSolution.objectives()[i] - currentSolution.objectives()[i])
                  / Math.max(temperature, minimumTemperature);
          value += v;
      }

      double probability = Math.exp(-1.0 * value);
    Check.probabilityIsValid(probability);
    return probability;
  }

  public double getNumberOfWorstAcceptedSolutions() {
    return numberOfWorstAcceptedSolutions;
  }

  @Override
  public List<S> getResult() {
    return archive.getSolutionList();
  }

  @Override
  public String getName() {
    return "MOSA";
  }

  @Override
  public String getDescription() {
    return "Multi-objective Simulated Annealing";
  }
}
