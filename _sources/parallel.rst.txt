.. _parallel:

Sub-module jmetal-parallel
==========================

:Author: Antonio J. Nebro
:Version: 1.0
:Date: 2020-14-09

This section describes the contents of the `jmetal-parallel` submodule. 

Synchronous parallelism
-----------------------
Traditionally, jMetal has provided basic support for evaluating list of solutions (i.e., populations and swarms) in parallel by using the processor cores. The adopted strategy is to include classes implementing the `SolutionListEvaluator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/evaluator/SolutionListEvaluator.java>`_ interface:

.. code-block:: java

    public interface SolutionListEvaluator<S> extends Serializable {
      List<S> evaluate(List<S> solutionList, Problem<S> problem) ;
      void shutdown() ;
    }

The `evaluate()` method receives the list of solutions to be evaluated by a problem, and returns the list of evaluated solutions. Two evaluators are included:

* `SequentialSolutionListEvaluator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/evaluator/impl/SequentialSolutionListEvaluator.java>`_: the solutions are evaluated sequentially.
* `MultiThreadedSolutionListEvaluator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/evaluator/impl/MultiThreadedSolutionListEvaluator.java>`_: the solutions are evaluated in parallel, by using the processor cores.

An example is shown in the following code snippet:

.. code-block:: java

    SolutionListEvaluator<DoubleSolution> evaluator = new MultiThreadedSolutionListEvaluator<DoubleSolution>(8);

    NSGAIIBuilder<DoubleSolution> builder =
        new NSGAIIBuilder<>(problem, crossover, mutation, populationSize)
            .setSelectionOperator(selection)
            .setMaxEvaluations(25000)
            .setSolutionListEvaluator(evaluator);

The resulting parallel model when using the `MultiThreadedSolutionListEvaluator` is **synchronous**, which implies that the behavior of the parallel algorithm is the same as the sequential one, but performance is affected by the fact that the algorithm alternates parallel (the solutions evaluation) and sequential codes (the rest of the algorithm code), so this model does not scale well.

An evaluator missing in jMetal is one based on Apache Spark, which was described in the paper `C. Barba-González, J. García-Nieto, Antonio J. Nebro, J.F.Aldana-Montes: Multi-objective Big Data Optimization with jMetal and Spark . EMO 2017 <http://dx.doi.org/10.1007/978-3-319-54157-0_2>`_. However, including this evaluator in the core jMetal sub-module would require to include the dependency to the Spark Maven package in the `pom.xml` file of the `jmetal-core` sub-module. So, we decided to create the `jmetal-parallel` sub-module to include not only the dependencies of the Spark packages but also others that will be eventually be added in the future.

The use of the `SparkSolutionListEvaluator <https://github.com/jMetal/jMetal/blob/master/jmetal-parallel/src/main/java/org/uma/jmetal/parallel/synchronous/SparkSolutionListEvaluator.java>`_ class is include in the `SynchronousNSGAIIWithSparkExample <https://github.com/jMetal/jMetal/blob/master/jmetal-parallel/src/main/java/org/uma/jmetal/parallel/example/SynchronousNSGAIIWithSparkExample.java>`_: 

.. code-block:: java

    SparkConf sparkConf = new SparkConf()
            .setMaster("local[8]") // 8 cores
            .setAppName("NSGA-II with Spark");

    JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
    SolutionListEvaluator<DoubleSolution> evaluator = new SparkSolutionListEvaluator<>(sparkContext) ;

    algorithm = new NSGAIIBuilder<>(problem, crossover, mutation, populationSize)
            .setSelectionOperator(selection)
            .setMaxEvaluations(maxEvaluations)
            .setSolutionListEvaluator(new SparkSolutionListEvaluator<>(sparkContext))
            .build();

    algorithm.run();
    List<DoubleSolution> population = algorithm.getResult();

    evaluator.shutdown();


Currently, besides the `SparkSolutionListEvaluator`, we provide also a `SparkEvaluation <https://github.com/jMetal/jMetal/blob/master/jmetal-parallel/src/main/java/org/uma/jmetal/parallel/synchronous/SparkEvaluation.java>`_ class, which implements the `Evaluation <https://github.com/jMetal/jMetal/blob/master/jmetal-experimental/src/main/java/org/uma/jmetal/experimental/componentbasedalgorithm/catalogue/evaluation/Evaluation.java>`_ interface.


Asynchronous parallelism
------------------------
An advantage of synchronous parallelism is that it does not require to modify the code of the metaheuristic using it. The only caveat is to ensure that the `evaluate()` method of the problem is thread-safe.

As mentioned before, a drawback of synchronous parallelism is that it can find difficulties to scale when the number of cores/processors is high. An alternative is to use an **asynchronous** parallel model, which basically consists in that, whenever a new solution has to evaluated, it is submitted immediately for evaluation asynchronously, without having to wait. Therefore, this model can scale better than the synchronous one, but the metaheuristic has to be modified to adopt it, and the resulting algorithm does not behave exactly as the sequential one.
We studied this issue in: `J.J. Durillo, A.J. Nebro, F. Luna, E. Alba A Study of Master-Slave Approaches to Parallelize NSGA-II. 11th International Workshop on Nature Inspired Distributed Computing (NIDISC) 2008. <http://dx.doi.org/10.1109/IPDPS.2008.4536375>`_. 

The `jmetal-parallel` sub-module contains an `AsynchronousParallelAlgorithm <https://github.com/jMetal/jMetal/blob/master/jmetal-parallel/src/main/java/org/uma/jmetal/parallel/asynchronous/algorithm/AsynchronousParallelAlgorithm.java>`_ interface and a multi-threaded implementation of it based on the master-worker scheme, which is applied in two classes: `AsynchronousMultiThreadedGeneticAlgorithm <https://github.com/jMetal/jMetal/blob/master/jmetal-parallel/src/main/java/org/uma/jmetal/parallel/asynchronous/algorithm/impl/AsynchronousMultiThreadedGeneticAlgorithm.java>`_ and `AsynchronousMultiThreadedNSGAII <https://github.com/jMetal/jMetal/blob/master/jmetal-parallel/src/main/java/org/uma/jmetal/parallel/asynchronous/algorithm/impl/AsynchronousMultiThreadedNSGAII.java>`_.


Module contents
---------------

The current structure of the `jmetal-parallel` sub-module is the following:

.. code-block:: text

  └── jmetal-parallel: org.uma.jmetal.parallel
      ├── asynchronous
          ├── algorithm
              ├── impl
                  ├── AsynchronousMultiThreadedGeneticAlgorithm
                  └── AsynchronousMultiThreadedNSGAII
          ├── multithreaded
              ├── Master
              └── Worker
          └── task
      ├── synchronous
          ├── SparkEvaluation
          └── SparkSolutionListEvaluator      
      └── example
          ├── AsynchronousMasterWorkerBasedNSGAIIExample
          ├── AsynchronousMultiThreadedGeneticAlgorithmExample
          ├── SynchronousNSGAIIWithSparkExample
          └── SynchronousComponentBasedNSGAIIWithSparkExample 

