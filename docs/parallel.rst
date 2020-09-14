.. _parallel:

Sub-module jmetal-parallel
==========================

:Author: Antonio J. Nebro
:Version: 1.0
:Date: 2020-14-09

This section describes the contents of the `jmetal-parallel` submodule. 

Synchronous parallelism
-----------------------
Traditionally, jMetal has provided basic support for evaluating list of solutions (i.e., populations and swarms) in parallel by using the processor cores. The adopted strategy is to include classes implementing the `SolutionListEvaluator` interface:

.. code-block:: java

    public interface SolutionListEvaluator<S> extends Serializable {
      List<S> evaluate(List<S> solutionList, Problem<S> problem) ;
      void shutdown() ;
    }

The `evaluate()` method receives the list of solutions to be evaluated by a problem, and returns the list of evaluated solutions. Two evaluators are included:

* `SequentialSolutionListEvaluator`: the solutions are evaluated sequentially.
* `MultiThreadedSolutionListEvaluator`: the solutions are evaluated in parallel, by using the processor cores.

The resulting parallel model when using the `MultiThreadedSolutionListEvaluator` is **synchronous**, which implies that the behavior of the parallel algorithm is the same as the sequential one, but performance is affected by the fact that the algorithm alternates parallel (the solutions evaluation) and sequential codes (the rest of the algorithm code), so this model does not scale well.

An evaluator missing in jMetal is one based on Apache Spark, which was described in the paper `C. Barba-González, J. García-Nieto, Antonio J. Nebro, J.F.Aldana-Montes: Multi-objective Big Data Optimization with jMetal and Spark . EMO 2017 <http://dx.doi.org/10.1007/978-3-319-54157-0_2>`_. However, including this evaluator in the core jMetal sub-module would require to include the dependency to the Spark Maven package in the `pom.xml` file of the `jmetal-core` sub-module. So, we decided to create the `jmetal-parallel` sub-module to include not only the dependencies of the Spark packages but also others that will be eventually be added in the future.

Currently, besides the `SparkSolutionListEvaluator`, we provide also a `SparkEvaluation` class, which implements the `Evaluation` interface.


Asynchronous parallelism
------------------------
An advantage of synchronous parallelism is that it does not require to modify the code of the metaheuristic using it. The only caveat is to ensure that the `evaluate()` method of the problem is thread-safe.

As mentioned before, a drawback of synchronous parallelism is that it can find difficulties to scale when the number of cores/processors is high. An alternative is to use an **asynchronous** parallel model, which basically consists in that, whenever a new solution has to evaluated, it is submitted immediately for evaluation asynchronously, without having to wait. Therefore, this model can scale better than the synchronous one, but the metaheuristic has to be modified to adopt it, and the resulting algorithm does not behave exactly as the sequential one.
We studied this issue in: `J.J. Durillo, A.J. Nebro, F. Luna, E. Alba A Study of Master-Slave Approaches to Parallelize NSGA-II. 11th International Workshop on Nature Inspired Distributed Computing (NIDISC) 2008.<http://dx.doi.org/10.1109/IPDPS.2008.4536375>`_. 

The `jmetal-parallel` sub-module contains an `AsynchronousParallelAlgorithm` interface and a multi-threaded implementation of it based on the master-worker scheme, which is applied in two classes: `AsynchronousMultiThreadedGeneticAlgorithm` and `AsynchronousMultiThreadedNSGAII`.




