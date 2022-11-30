.. _algorithms:

The ``Algorithm`` interface
===========================

:Author: Antonio J. Nebro
:Version: 1.0
:Date: 2022-11-30

A metaheuristic in jMetal 5 is an entity that implements the ``Algorithm`` interface (package ``org.uma.jmetal.algorithm``
in sub-project ``jmetal-core``:

.. code-block:: java

  package org.uma.jmetal.algorithm;

  /**
    * Interface representing an algorithm
    * @author Antonio J. Nebro
    * @version 1.0
    * @param <Result> Result
    */
  public interface Algorithm<Result> extends Runnable, Serializable, DescribedEntity {
    void run() ;
    Result getResult() ;
  }

This interface is very generic: it specifies that an algorithm must have a ``run()`` method and
return a result by invoking the ``getResult()`` method. As it extends Runnable, any algorithm can
be executed in a thread. The simplicity of the interface offers plenty of freedom to implement a metaheuristic. We explore
three alternatives next.

Direct extension: NSGA-II in jMetal 4.5
---------------------------------------

The implementation of NSGA-II in the former versions of jMetal (prior to jMetal 5) was based
on a single class containing the behavior the algorithm. We include a revised implementation of this
approach in class ``NSGA45``. (package ``org.uma.jmetal.algorithm.multiobjective.nsgaii``, subproject
``jmetal-algorithm``). An extract of the code is:

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



