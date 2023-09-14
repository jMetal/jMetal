.. _algorithms:

Algorithms
==========

:Author: Antonio J. Nebro
:Version: 1.0
:Date: 2023-09-14

A metaheuristic in jMetal is an entity that implements the ``Algorithm`` interface (package ``org.uma.jmetal.algorithm``
in sub-project ``jmetal-core``:

.. code-block:: java

  package org.uma.jmetal.algorithm;

  /**
    * Interface representing an algorithm
    * @author Antonio J. Nebro
    * @version 1.0
    * @param <Result> Result
    */
  public interface Algorithm<Result> extends Runnable, Serializable {
    void run() ;
    Result result() ;
    String name();
    String description();
  }

This interface is very generic: it specifies that an algorithm must have a ``run()`` method and return a result by invoking the ``result()`` method. As it extends Runnable, any algorithm can be executed in a thread. The simplicity of the interface offers plenty of freedom to implement a metaheuristic. We explore three alternatives next by taking NSGA-II as the target algorithm.

Direct extension: NSGA-II in jMetal 4.5
---------------------------------------

The implementation of NSGA-II in the former versions of jMetal (prior to jMetal 5) was based
on a single class containing the behavior the algorithm. We include a revised implementation of this
approach in class ``NSGA45``. (package ``org.uma.jmetal.algorithm.multiobjective.nsgaii``, subproject
``jmetal-algorithm``). An extract of the code is the following:

.. code-block:: java

  package org.uma.jmetal.algorithm.multiobjective.nsgaii;

  /**
    * Implementation of NSGA-II following the scheme used in jMetal4.5 and former versions
    *
    * @author Antonio J. Nebro <antonio@lcc.uma.es>
   */
  public class NSGAII45<S extends Solution<?>> implements Algorithm<List<S>> {

    /** Constructor */
    public NSGAII45(
        Problem<S> problem,
        int maxEvaluations,
        int populationSize,
        CrossoverOperator<S> crossoverOperator,
        MutationOperator<S> mutationOperator,
        SelectionOperator<List<S>, S> selectionOperator,
        SolutionListEvaluator<S> evaluator) {
    }

    /** Run method */
    @Override
    public void run() {
      population = createInitialPopulation();
      evaluatePopulation(population);
      while (evaluations < maxEvaluations) {
        // Evolutionary steps: selection, variation, replacement
        ...

        // Ranking
        Ranking<S> ranking = new FastNonDominatedSortRanking<>();
        ranking.compute(jointPopulation);

        // Crowding distance calculation
        rankingAndCrowdingSelection = new RankingAndCrowdingSelection<>(populationSize);

        // Ranking and crowding replacement
        population = rankingAndCrowdingSelection.execute(jointPopulation);
      }
    }

    @Override
    public List<S> getResult() {
      return getNonDominatedSolutions(population);
    }


An advantage of this method is that, as all the logic of NSGA-II is in the ``run()`` method, it is easy to modify if we would like, for example, to change the stopping condition to finish after a number of seconds of computation or to store the population at the end of each iterations. 

A drawback is that, if we want to go back to the original code, we will have to undo all the changes and, in the end, the simplest strategy will be to copy the original code into another class and apply the changes to it. As a result, we will end up with several classes that implement variants of NSGA-II. This results in a poor code reuse and, in case of fixing a bug in the original code, we should fix manually all the copies. 

Another disadvantage has to do with software testing. Therefore, testing the steps of the algorithm (selection, variation, etc.) is difficult, as everything is mixed inside the ``run()`` method. 

An example of the use of this class can be found in class `NSGAII45Runner <https://github.com/jMetal/jMetal/blob/main/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/examples/multiobjective/nsgaii/NSGAII45Runner.java>`_.

Inheritance-based template: NSGA-II in jMetal 5.0
-------------------------------------------------

Addressing the drawbacks of the previous scheme was one of the reasons for redesigning jMetal from scratch, resulting in version 5.0 of the framework. In this release, we propose
the use of algorithm templates which contain the behavior of the algorithms, and implementing a given technique consists of filling in the template. In the case of evolutionary algorithms, the template is ``AbstractEvolutionaryAlgorithm`` abstract class (package ``org.uma.jmetal.algorithm.impl``, sub-project ``jmetal-core``), which is included next:

.. code-block:: java

  public abstract class AbstractEvolutionaryAlgorithm<S, R>  implements Algorithm<R>{
    protected abstract void initProgress();
    protected abstract void updateProgress();
    protected abstract boolean isStoppingConditionReached();
    protected abstract  List<S> createInitialPopulation() ;
    protected abstract List<S> evaluatePopulation(List<S> population);
    protected abstract List<S> selection(List<S> population);
    protected abstract List<S> reproduction(List<S> population);
    protected abstract List<S> replacement(List<S> population, List<S> offspringPopulation);

    @Override public void run() {
      List<S> offspringPopulation;
      List<S> matingPopulation;

      population = createInitialPopulation();
      population = evaluatePopulation(population);
      initProgress();
      while (!isStoppingConditionReached()) {
        matingPopulation = selection(population);
        offspringPopulation = reproduction(matingPopulation);
        offspringPopulation = evaluatePopulation(offspringPopulation);
        population = replacement(population, offspringPopulation);
        updateProgress();
      }
    }
  }

The template inherits from ``Algorithm`` and we can observe that the ``run()`` method closely mimics the behavior of a generic evolutionary algorithm, where each step has the form of an abstract method. This way, implementing an evolutionary algorithm requires to extended the template and provide the code for all the methods. In the case of NSGA-II (class ``NSGAII``, package ``org.uma.jmetal.algorithm.multiobjective.nsgaii``, sub-project ``jmetal-algorithm``), the selection and replacement methods are the following:

.. code-block:: java
  
    @Override
    protected List<S> selection(List<S> population) {
      List<S> matingPopulation = new ArrayList<>(population.size());
      for (int i = 0; i < matingPoolSize; i++) {
        S solution = selectionOperator.execute(population);
        matingPopulation.add(solution);
      }

      return matingPopulation;
    }

    @Override
    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
      List<S> jointPopulation = new ArrayList<>();
      jointPopulation.addAll(population);
      jointPopulation.addAll(offspringPopulation);

      RankingAndCrowdingSelection<S> rankingAndCrowdingSelection;
      rankingAndCrowdingSelection = new RankingAndCrowdingSelection<S>(getMaxPopulationSize(),
        dominanceComparator);

      return rankingAndCrowdingSelection.execute(jointPopulation);
    }

The ``initProgress()`` and ``updateProgress()`` are intended to, respectively, initialize and update some status information of the algorithm. Concretely, in the case of NSGA-II, they are used to update the evaluation counter:

.. code-block:: java
  
  @Override
  protected void initProgress() {
    evaluations = getMaxPopulationSize();
  }

  @Override
  protected void updateProgress() {
    evaluations += offspringPopulationSize;
  }

We can see that the methods are small and, in case of be interested, for example, in a version with a different selection scheme, we only need to extend class ``NSGAII`` and to redefine (override) the ``selection()`` method. Most of the algorithms in jMetal, included in the ``jmetal-algorithm`` sub-project, are based on templates that are extended by using inheritance.

Using templates can be complicated for some users, as the implementations of metaheuristics are divided into the templates and the classes extending them. If we consider to add a code to store the population after the end of every iteration, it cannot be clear how to do it; two alternatives are:
 
1. Modify the template to add the code at the end of the main loop. In the case of ``AbstractEvolutionaryAlgorithm``, this code would be after the ``updateProgress()`` method. This approach has the negative effect of that all the algorithms inheriting from the template will execute that code, which probably it not desirable. 
2. Create a new subclass redefining the  ``updateProgress()`` method as follows:

.. code-block:: java

  @Override
  protected void updateProgress() {
    // code to write the population in a file

    super.updateProgress() ;
  }

A consequence of the algorithm template strategy is that, whenever we need some algorithm variant, a new class must be created, what could result in a high number of sub-classes. 
Anyway, its main drawback from our point of view is that it lacks the required flexibility to create algorithms in a simple way. We found this limitation when starting to work on automatic algorithm design, where we needed to configure metaheuristics from a string composed of pairs <element, value>, where the elements can be parameters (such as the population size) or components (such particular crossover or mutation operators). An example is: ``"--populationSize 100 --crossover SBXCrossover --crossoverProbability 0.9 ..."``.

You can find many examples of metaheuristics that use this approach in the ``org.uma.jmetal.algorithm.examples`` package located in ``jmetal-algorithm``.

Component-based template: NSGA-II in jMetal 6.0
-----------------------------------------------

The idea of using a component-based template is to use delegation instead of inheritance, so that the template is not an abstract class but a concrete class where the algorithm steps are implemented with objects instead of methods. In the case of evolutionary algorithms, the template is included in class ``EvolutionaryAlgorithm`` (package ``org.uma.jmetal.component.algorithm``, sub-project ``jmetal-component``). We show a code snippet of this class next:

.. code-block:: java

  public class EvolutionaryAlgorithm<S extends Solution<?>> implements Algorithm<List<S>>{

  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private Selection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  public EvolutionaryAlgorithm(
      String name,
      SolutionsCreation<S> initialPopulationCreation,
      Evaluation<S> evaluation,
      Termination termination,
      Selection<S> selection,
      Variation<S> variation,
      Replacement<S> replacement) {
    this.name = name;
    this.createInitialPopulation = initialPopulationCreation;
    this.evaluation = evaluation;
    this.termination = termination;
    this.selection = selection;
    this.variation = variation;
    this.replacement = replacement;
  }

  public void run() {
    population = createInitialPopulation.create();
    population = evaluation.evaluate(population);
    initProgress();
    while (!termination.isMet(attributes)) {
      List<S> matingPopulation = selection.select(population);
      List<S> offspringPopulation = variation.variate(population, matingPopulation);
      offspringPopulation = evaluation.evaluate(offspringPopulation);

      population = replacement.replace(population, offspringPopulation);
      updateProgress();
    }
  }

We can observe that the ``run()`` is very similar to the one included in class ``AbstractEvolutionaryAlgorithm``, but now the algorithm steps are objects. This way, if we focus on the selection, ``Selection`` is a class (i.e., a component) providing a ``select()`` method; the other components are defined in similar way. The complement of this template is to have a catalogue for each of the component types, so creating a particular algorithm consists of adding the proper components to the template. 

The implementation of NSGA-II using components is included in the ``NSGAIIBuilder`` class (package: ``org.uma.jmetal.component.algorithm.multiobjective``, sub-project: ``jmetal-component``): 

.. code-block:: java

  public class NSGAIIBuilder<S extends Solution<?>> {

    public NSGAIIBuilder(Problem<S> problem, int populationSize, int offspringPopulationSize,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation) {
    name = "NSGAII";

    densityEstimator = new CrowdingDistanceDensityEstimator<>();
    ranking = new FastNonDominatedSortRanking<>();

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.ONE_SHOT);

    this.variation =
        new CrossoverAndMutationVariation<>(
            offspringPopulationSize, crossover, mutation);

    int tournamentSize = 2 ;
    // int tournamentSize = 8 ;
    this.selection =
        new NaryTournamentSelection<>(
            tournamentSize,
            variation.getMatingPoolSize(),
            new MultiComparator<>(
                Arrays.asList(
                    Comparator.comparing(ranking::getRank),
                    Comparator.comparing(densityEstimator::getValue).reversed())));

    this.termination = new TerminationByEvaluations(25000);
    // this.termination = new TerminationByKeyboard();
    // this.termination = new TerminationByComputingTime(5000);

    this.evaluation = new SequentialEvaluation<>(problem);
    // this.evaluation = new MultiThreadedEvaluation<>(8, problem);

  }

    public EvolutionaryAlgorithm<S> build() {
      return new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation, termination,
          selection, variation, replacement);
  }

We can see as the constructor of the class instantiates all the components characterizing NSGA-II, and the algorithm is set up by creating a instance of ``EvolutionaryAlgorithm`` in the ``build()`` method. We have included in the code snippet some comments indicating alternatives to the currently used components. 

More information about the component-based template can be found in the :doc:`component-based algorithms </component>` section of this documentation. Examples of using component based algorithms are located in the ``org.uma.jmetal.component.examples`` package in the ``jmetal-component`` sub-project.

As in the case of the inheritance-based template, some users can find this approach difficult to understand. Furthermore, algorithm designers will find that this scheme cannot be applied to those metaheuristics whose internal logic is tightly coupled, so that it can be very difficult to define independent components to be used with the template. 

The flexibility of this approach as allowed us to solve the aforementioned issue of having a way to instantiate NSGA-II (and other algorithms) from a parameter string. We have defined a class named ``AutoNSGAII`` (package: ``org.uma.jmetal.auto.autoconfigurablealgorithm``, sub-project: ``jmetal-auto``) that can be used in illustrated by this example (see class `NSGAIIConfiguredFromAParameterString.java <https://github.com/jMetal/jMetal/blob/main/jmetal-auto/src/main/java/org/uma/jmetal/auto/autoconfigurablealgorithm/examples/NSGAIIConfiguredFromAParameterString.java>`_): 


.. code-block:: java

    String referenceFrontFileName = "ZDT1.csv" ;

    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ referenceFrontFileName + " "
                + "--maximumNumberOfEvaluations 25000 "
                + "--algorithmResult population "
                + "--populationSize 100 "
                + "--offspringPopulationSize 100 "
                + "--createInitialSolutions random "
                + "--variation crossoverAndMutationVariation "
                + "--selection tournament "
                + "--selectionTournamentSize 2 "
                + "--rankingForSelection dominanceRanking "
                + "--densityEstimatorForSelection crowdingDistance "
                + "--crossover SBX "
                + "--crossoverProbability 0.9 "
                + "--crossoverRepairStrategy bounds "
                + "--sbxDistributionIndex 20.0 "
                + "--mutation polynomial "
                + "--mutationProbabilityFactor 1.0 "
                + "--mutationRepairStrategy bounds "
                + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parseAndCheckParameters(parameters);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();
  
    new SolutionListOutput(nsgaII.getResult())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

