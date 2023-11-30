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
The last stable version is 6.1.
The most recent documentation is hosted in https://jmetal.readthedocs.io.


The current development version (6.2-SNAPSHOT) is a Maven project structured in the following sub-projects:

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

Comments and suggestions are welcome.

Changelog
---------
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
