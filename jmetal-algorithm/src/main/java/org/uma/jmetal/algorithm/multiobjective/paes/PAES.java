package org.uma.jmetal.algorithm.multiobjective.paes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.algorithm.impl.AbstractEvolutionStrategy;
import org.uma.jmetal.algorithm.multiobjective.pesa2.util.AdaptiveGridArchive;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.GenericBoundedArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.densityestimator.impl.GridDensityEstimator;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 *     <p>This class implements the PAES algorithm. This implementation is not restricted to use the
 *     {@link AdaptiveGridArchive} class, but other bounder archives can be used (e.g., the {@link
 *     CrowdingDistanceArchive})
 */
@SuppressWarnings("serial")
public class PAES<S extends Solution<?>> extends AbstractEvolutionStrategy<S, List<S>> {
  protected Problem<S> problem ;
  protected int maxEvaluations;
  protected int evaluations;

  protected BoundedArchive<S> archive;
  protected Comparator<S> comparator;

  /** Constructor */
  public PAES(
      Problem<S> problem,
      int maxEvaluations,
      BoundedArchive<S> archive,
      MutationOperator<S> mutationOperator) {
    setProblem(problem);
    this.maxEvaluations = maxEvaluations;
    this.archive = archive;
    this.mutationOperator = mutationOperator;

    comparator = new DominanceWithConstraintsComparator<S>();
  }

  public PAES(
      Problem<S> problem,
      int maxEvaluations,
      int archiveSize,
      int biSections,
      MutationOperator<S> mutationOperator) {
    this(
        problem,
        maxEvaluations,
        new GenericBoundedArchive<>(
            archiveSize, new GridDensityEstimator<>(biSections, problem.numberOfObjectives())),
        mutationOperator);
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
        if (archive.comparator().compare(current, mutatedSolution) > 0) {
          current = mutatedSolution;
        }
      }
    }

    population.set(0, current);
    return population;
  }

  @Override
  public List<S> getResult() {
    return archive.solutions();
  }

  @Override
  public String getName() {
    return "PAES";
  }

  @Override
  public String getDescription() {
    return "Pareto-Archived Evolution Strategy";
  }
}
