package org.uma.jmetal.algorithm.multiobjective.cellde;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 *
 */
public class CellDE {

/*
  public class CellDE extends AbstractDifferentialEvolution<List<DoubleSolution>>  {
  protected int evaluations;
  protected int maxEvaluations;
  protected final SolutionListEvaluator<DoubleSolution> evaluator;

  private Neighborhood<DoubleSolution> neighborhood;
  private int currentIndividual;
  private List<DoubleSolution> currentNeighbors;

  private DifferentialEvolutionSelection selection ;
  private DifferentialEvolutionCrossover crossover ;

  private BoundedArchive<DoubleSolution> archive;

  private Comparator<DoubleSolution> dominanceComparator;
  private LocationAttribute<DoubleSolution> location;
*/
  /**
   * Constructor
   * @param problem
   * @param maxEvaluations
   * @param populationSize
   * @param neighborhood
   * @param evaluator
   */
  /*
  public CellDE(Problem<DoubleSolution> problem,
                int maxEvaluations,
                int populationSize,
                BoundedArchive<DoubleSolution> archive,
                Neighborhood<DoubleSolution> neighborhood,
                DifferentialEvolutionSelection selection,
                DifferentialEvolutionCrossover crossover,
                double cr,
                double f,
                SolutionListEvaluator<DoubleSolution> evaluator) {
    setProblem(problem);
    setMaxPopulationSize(populationSize);
    this.maxEvaluations = maxEvaluations;
    this.archive = archive ;
    this.neighborhood = neighborhood ;
    this.selection = selection;
    this.crossover = crossover;
    this.dominanceComparator = new DominanceComparator<DoubleSolution>() ;

    this.evaluator = evaluator ;
  }

  @Override
  protected void initProgress() {
    evaluations = 0;
    currentIndividual=0;
  }

  @Override
  protected void updateProgress() {
    evaluations++;
    currentIndividual=(currentIndividual+1)%getMaxPopulationSize();
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return (evaluations==maxEvaluations);
  }

  @Override
  protected List<DoubleSolution> createInitialPopulation() {
    List<DoubleSolution> population = new ArrayList<>(getMaxPopulationSize());
    for (int i = 0; i < getMaxPopulationSize(); i++) {
      DoubleSolution newIndividual = getProblem().createSolution();
      population.add(newIndividual);
    }
    location = new LocationAttribute<>(population);
    return population;
  }

  @Override
  protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
    return evaluator.evaluate(population, getProblem());
  }
/*
  @Override
  protected List<DoubleSolution> selection(List<DoubleSolution> population) {
    List<DoubleSolution> parents = new ArrayList<>(2);
    currentNeighbors = neighborhood.getNeighbors(population, currentIndividual);
    currentNeighbors.add(population.get(currentIndividual));

    parents.add(selectionOperator.execute(currentNeighbors));
    if (archive.size() > 0) {
      parents.add(selectionOperator.execute(archive.getSolutionList()));
    } else {
      parents.add(selectionOperator.execute(currentNeighbors));
    }
    return parents;
  }

  @Override
  protected List<DoubleSolution> reproduction(List<S> population) {
    List<DoubleSolution> result = new ArrayList<>(1);
    List<> offspring = crossoverOperator.execute(population);
    mutationOperator.execute(offspring.get(0));
    result.add(offspring.get(0));
    return result;
  }

  @Override
  protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    int flag = dominanceComparator.compare(population.get(currentIndividual),offspringPopulation.get(0));

    if (flag == 1) { //The new individual dominates
      insertNewIndividualWhenDominates(population,offspringPopulation);
    } else if (flag == 0) { //The new individual is non-dominated
      insertNewIndividualWhenNonDominated(population,offspringPopulation);
    }
    return population;
  }

  @Override
  public List<    List<DoubleSolution> matingPopulation = new LinkedList<>();
  for (int i = 0; i < getMaxPopulationSize(); i++) {
    // Obtain parents. Two parameters are required: the population and the
    //                 index of the current individual
    selectionOperator.setIndex(i);
    List<DoubleSolution> parents = selectionOperator.execute(population);

    matingPopulation.addAll(parents);
  }

  return matingPopulation;> getResult() {
    return archive.getSolutionList();
  }

  private void insertNewIndividualWhenDominates(List<S> population, List<S> offspringPopulation) {
    location.setAttribute(offspringPopulation.get(0),
        location.getAttribute(population.get(currentIndividual)));

    population.set(location.getAttribute(offspringPopulation.get(0)),offspringPopulation.get(0));
    archive.add(offspringPopulation.get(0));
  }


  private void insertNewIndividualWhenNonDominated(List<S> population, List<S> offspringPopulation) {
    currentNeighbors.add(offspringPopulation.get(0));
    location.setAttribute(offspringPopulation.get(0), -1);

    Ranking<S> rank = new DominanceRanking<S>();
    rank.computeRanking(currentNeighbors);

    CrowdingDistance<S> crowdingDistance = new CrowdingDistance<S>();
    for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
      crowdingDistance.computeDensityEstimator(rank.getSubfront(j));
    }

    Collections.sort(this.currentNeighbors,new RankingAndCrowdingDistanceComparator<S>());
    S worst = this.currentNeighbors.get(this.currentNeighbors.size()-1);

    if (location.getAttribute(worst) == -1) { //The worst is the offspring
      archive.add(offspringPopulation.get(0));
    } else {
      location.setAttribute(offspringPopulation.get(0),
          location.getAttribute(worst));
      population.set(location.getAttribute(offspringPopulation.get(0)),offspringPopulation.get(0));
      archive.add(offspringPopulation.get(0));
    }
  }

  @Override public String getName() {
    return "MOCell" ;
  }

  @Override public String getDescription() {
    return "Multi-Objective Cellular evolutionry algorithm" ;
  }
*/
}
