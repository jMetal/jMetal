jMetal project Web site
=======================

.. image:: https://github.com/jMetal/jMetal/actions/workflows/build.yml/badge.svg
    :alt: Build Status
    :target: https://github.com/jMetal/jMetal/actions/workflows/build.yml

.. image:: https://github.com/jMetal/jMetal/actions/workflows/test.yml/badge.svg
    :alt: Test Status
    :target: https://github.com/jMetal/jMetal/actions/workflows/test.yml

.. image:: https://github.com/jMetal/jMetal/actions/workflows/integration-test.yml/badge.svg
    :alt: Integration Test Status
    :target: https://github.com/jMetal/jMetal/actions/workflows/integration-test.yml

.. image:: https://readthedocs.org/projects/jmetal/badge/?version=latest
   :alt: Documentation Status
   :target: https://jmetal.readthedocs.io/?badge=latest

jMetal is a Java-based framework for multi-objective optimization with metaheuristics.
The last stable version is 6.9.1.
The most recent documentation is hosted in https://jmetal.readthedocs.io.


The current development version (6.9.2-SNAPSHOT) is a Maven project structured in the following sub-projects:

+---------------------+------------------------------------+
| Sub-project         |  Contents                          | 
+=====================+====================================+
| jmetal-core         |  Core classes                      |
+---------------------+------------------------------------+
| jmetal-solution     |  Solution encodings                |
+---------------------+------------------------------------+
| jmetal-algorithm    |  Algorithm implementations         |
+---------------------+------------------------------------+
| jmetal-problem      |  Benchmark problems                |
+---------------------+------------------------------------+
| jmetal-lab          |  Experimentation and visualization |
+---------------------+------------------------------------+
| jmetal-parallel     |  Parallel extensions               |
+---------------------+------------------------------------+
| jmetal-auto         |  Auto-design and configuration     |
+---------------------+------------------------------------+
| jmetal-component    |  Component-based algorithms        |
+---------------------+------------------------------------+


Related projects
----------------
* `jMetalPy <https://github.com/jMetal/jmetalpy>`_: jMetal in Python
* `SAES <https://github.com/jMetal/SAES>`_: Python library to analyse and compare the performance of multi-objective algorithms

Changelog
---------
* [07/14/2025] jMetal 6.9.1 is released.

* [07/07/2025] jMetal 6.8 is released.

* [07/07/2025] Added new crossover (BLX-Alpha-Beta, Laplace, Arithmetic, Fuzzy recombination, UNDX) and mutation (Power Law, Levy Flight, Simple Random) operators for continuous problems.

* [06/18/2025] A new folder *resources/referenceFrontsRSG* have been added. It contains Pareto front approximations produced with the `Reference Set Generator (DOI: https://doi.org/10.3390/math13101626) <https://doi.org/10.3390/math13101626>`_  method. Contribution of Oliver Schütze.

* [05/09/2025] jMetal 6.7 is released.

* [05/09/2025] The jMetal project adopts Java 22.

* [01/17/2025] Added a variant of SMS-EMOA using differential evolution.

* [11/30/2024] Added single and multi-objective versions of the Knapsack problem.

* [11/22/2024] Added new crossover (Cycle, EdgeRecombination, ODX, Population-Based) and mutation (Displacement, Insert, Inversion, Scamble, SimpleInversion) operators for permutations. Contribution of Nicolas R. Uribe (@NicolasRodriguezUribe)

* [06/27/2024] The jMetal project adopts Java 19.

* [11/30/2023] The Zapotecas-Coello-Aguirre-benchmark (ZCAT) (https://doi.org/10.1016/j.swevo.2023.101350) is included in jMetal.

* [09/25/2023] The jMetal project adopts Java 17.

* [09/25/2023] Added a variant of NSGA-II using differential evolution.

* [07/19/2023] jMetal 6.1 is released.

* [07/04/2023]. Added the RWA benchmark, described in "Engineering applications of multi-objective evolutionary algorithms: A test suite of box-constrained real-world problems". DOI: https://doi.org/10.1016/j.engappai.2023.106192

* [12/13/2022] jMetal 6.0 is released.

* [9/12/2022] The master branch has been renamed main.

* [7/5/2022] The jMetal project adopts Java 14.

* [5/23/2022] Algorithm AGE-MOEA-II. Contribution of Annibale Panichella (@apanichella).

* [5/9/2022] Algorithm `AGE-MOEA <https://dl.acm.org/doi/10.1145/3321707.3321839>`_. Contribution of Annibale Panichella (@apanichella).

* [3/28/2022] The CF benchmark of constrained multi-objective problems, defined in `Constrained Multiobjective Optimization: Test Problem Construction and Performance Evaluations <https://doi.org/10.1109/TEVC.2020.3011829>`_, has been included.

* [3/28/2022] The constraint handling code has been refactorized and the `documentation <https://jmetal.readthedocs.io/en/latest/constraints.html>`_ has been updated.

* [9/6/2021] Release 5.11

* [9/6/2021] The jMetal project adopts Java 13.

* [7/6/2021] The `LSMOP benchmark <https://doi.org/10.1109/TCYB.2016.2600577>`_ is available. 

* [2/19/2021] The `Solution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/Solution.java>`_ interface has been refactorized.

* [2/19/2021] New implementation of quality indicators to remove the dependence of jMetal classes. Now, all of them accept as a parameter a matrix containing objective values.

* [1/21/2021] Added the MicroFAME multi-objective genetic algorith, described in: Alejandro Santiago, Bernabé Dorronsoro, Héctor Fraire, Patricia Ruíz: Micro-Genetic algorithm with fuzzy selection of operators for multi-Objective optimization: microFAME. Swarm and Evolutionary Computation, V.61, March 2021. `DOI <https://doi.org/10.1016/j.swevo.2020.100818>`_. Contributed by Alejandro Santiago.

