.. _mnds:

Using MNDS in NSGA-II
=====================

:Author: Antonio J. Nebro
:Version: Draft
:Date: 2020-2-19

Merge Non-Dominated Sorting Algorithm (MNDS) is a dominance ranking method intended to accelerate the running times of Pareto-based multi-objectives metaheuristics such as NSGA-II. The algorithm is described in:  `Javier Moreno, Daniel Rodríguez, Antonio J. Nebro, Jose A. Lozano, "Merge Nondominated Sorting Algorithm for Many-Objective Optimization". IEEE Transactions on Cybernetics. In press (2020) <https://ieeexplore.ieee.org/document/9000950>`_.

The implementation of NSGA-II in jMetal allows to indicate the dominance ranking algorithm as a parameter. An example is shown in class `NSGAIIWithMNDSRankingExample  <https://github.com/jMetal/jMetal/blob/master/jmetal-example/src/main/java/org/uma/jmetal/example/multiobjective/nsgaii/NSGAIIWithMNDSRankingExample.java>`_:

.. code-block:: java

    Problem<DoubleSolution> problem;
    NSGAII<DoubleSolution> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;

    String problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2";
    String referenceParetoFront = "resources/referenceFronts/DTLZ2.3D.pf";

    problem = ProblemUtils.<DoubleSolution>loadProblem(problemName);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(75000);

    Ranking<DoubleSolution> ranking = new MergeNonDominatedSortRanking<>();

    algorithm =
        new NSGAII<>(
                problem,
                populationSize,
                offspringPopulationSize,
                crossover,
                mutation,
                termination,
                ranking);

    algorithm.run();

The non-dominated raking algorithm is set in line 32, where an instance of `MergeNonDominatedSortRanking` class is created. If the ranking parameter is omitted, NSGA-II is configured with the stantard fast non-dominated sorting strategy.

To give some hints of the speedps that can be obtained, we have measured the running times in different scenarios. In all the cases the number of function evaluations is set to 100000. The results are reported next:

+---------+-----------------+------------+------------+-------------------+
| Problem | Population size | Objectives |   NSGA-II  | NSGA-II with MNDS |    
+=========+=================+============+============+===================+
| DTLZ1   |      100        |      2     |  2205ms    |       1047ms      |        
+---------+-----------------+------------+------------+-------------------+
| DTLZ1   |      500        |      4     |  6340ms    |       1208ms      |
+---------+-----------------+------------+------------+-------------------+
| DTLZ1   |      1000       |      8     |  13746ms   |       1723ms      | 
+---------+-----------------+------------+------------+-------------------+
| DTLZ2   |      100        |      2     |  1955ms    |       1112ms      |
+---------+-----------------+------------+------------+-------------------+
| DTLZ2   |      500        |      4     |  5818ms    |       1626ms      |
+---------+-----------------+------------+------------+-------------------+
| DTLZ2   |      1000       |      8     |  13637ms   |       1834ms      |
+---------+-----------------+------------+------------+-------------------+

Hardware and software details:

* Processor: Intel(R) Core(TM) i7-6700 CPU @ 3.40GHz
* Memory: 64 GB
* Operating System: Windows 10 Pro (Version	10.0.18363)
* Java SE version: 1.8.0_221 (build 1.8.0_221-b11)
* jMetal 6.0-SNAPSHOT
