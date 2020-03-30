jMetal 6 project Web site
==========================
.. image:: https://travis-ci.org/jMetal/jMetal.svg?branch=master
    :alt: Build Status
    :target: https://travis-ci.org/jMetal/jMetal

.. image:: https://readthedocs.org/projects/jmetal/badge/?version=latest
   :alt: Documentation Status
   :target: https://jmetal.readthedocs.io/?badge=latest

jMetal is a Java-based framework for multi-objective optimization with metaheuristics. The current stable version is 5.8 (https://github.com/jMetal/jMetal/tree/jmetal-5.8), which is based on the description of jMetal 5 included in the paper "Redesigning the jMetal Multi-Objective Optimization Framework" (http://dx.doi.org/10.1145/2739482.2768462), presented at GECCO 2015.

After five years working with jMetal 5, a new major release, jMetal 6, is under development and, after some months working of a development branch, it has been moved to the master branch as version 6.0-SNAPSHOT.

A summary of the features of jMetal 6.0 are listed next:

* jMetal 6 requires Java 11. 

* The default format for files containing fronts (including the reference Pareto fronts) is CSV.

* Use of Sphinx for the documentation. In jMetal 5 the documentation is based on Markdown files; it is not complete and some parts are outdated (this documentation is still available: https://github.com/jMetal/jMetalDocumentation). The documentation of jMetal 6 is being elaborated here: https://jmetal.readthedocs.io/en/latest/.

* Support for automatic configuration of metaheuristics. We include a sub-project called ``jmetal-auto``, which currently contains a version of NSGA-II that can be fully auto tuned using ``irace``, as is described in the paper "Automatic Configuration of NSGA-II with jMetal and irace", presented at GECCO 2019 (DOI: https://doi.org/10.1145/3319619.3326832).

* Improved experimentation. The output of an experiment (i.e., the execution of a number of algorithms on a set of problems) is an CSV file which can be further analyzed to produce Latex tables and graphics with statistical information. We plan to use Tablesaw (https://github.com/jtablesaw/tablesaw) and Smile (http://haifengl.github.io/smile/) for analysis tasks and for visualization. All the experimentation code is located in a new sub-package called ``jmetal-lab``.

* All the core packages in ``jmetal-core`` (``solution``, ``problem``, ``algorithm``, ``operator``, ``quality indicator``), are being revised, tested, and refactored. The same applies to the ``jmetal-core/util`` package.

* The implementation of the algorithms in ``jmetal-algorithm`` will be revised. 

jMetal 6 is implemented in Java 8 (although I plan to change to Java 11) and it is a Maven project structured in six sub-projects:


+------------------+-----------------------------------+
| Sub-project      |  Contents                         | 
+==================+===================================+
| jmetal-core      |  Core classes                     |
+------------------+-----------------------------------+
| jmetal-solution  |  Solution encodings               |
+------------------+-----------------------------------+
| jmetal-algorithm |  Algorithm implementations        |
+------------------+-----------------------------------+
| jmetal-problem   |  Benchmark problems               |
+------------------+-----------------------------------+
| jmetal-example   |  Examples                         |
+------------------+-----------------------------------+
| jmetal-lab       |  Experimentation                  |
+------------------+-----------------------------------+
| jmetal-auto      |  Auto configuration               |
+------------------+-----------------------------------+

All the code included in jMetal 5.8 is included in jMetal 6.0-SNAPSHOT, so the project is fully functional and we are currently using it in our research work. 

Comments and suggestions are very welcome.

Changelog
---------

* [3/19/2020] New quality indicator: `NormalizedHypervolume <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/qualityindicator/impl/NormalizedHypervolume.java>`_.

* [3/19/2020] The jMetal 6 project is changed to use Java 11.

* [2/11/2020] All the files containing Pareto front approximations and weight vectors have been moved to the ``resources`` folder, located in root project directory.

* [2/06/2020] `New implementation <https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/smsemoa>`_ of the SMS-EMOA algorithm based on a component-based scheme.

* [1/23/2020] A solution encoding for defining solution having mixed internal representations has been developed using class `CompositeSolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/compositesolution/CompositeSolution.java>`_. This class acts as solution container, so their variables are solutions of any kind. Variation operators for this class have been developed (classes `CompositeCrossover <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/operator/crossover/impl/CompositeCrossover.java>`_ and `CompositeMutation <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/operator/mutation/impl/CompositeMutation.java>`_). An example of configuring and running NSGA-II to solve a problem using this encoding is available in class `NSGAIIWithMixedSolutionEncodingExample <https://github.com/jMetal/jMetal/blob/master/jmetal-example/src/main/java/org/uma/jmetal/example/multiobjective/nsgaii/NSGAIIWithMixedSolutionEncodingExample.java>`_.  Any feedback about this new feature is welcome.

* [1/13/2020] `New implementation <https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/moead>`_ of the MOEA/D algorithm based on a component-based scheme. A description of the implementation and examples of using it can be found in the `documentation <https://jmetal.readthedocs.io/en/latest/moead.html>`_.

* [12/10/2019] Merge non dominated sorting algorithm (contributed by Javier Moreno), described in `"Merge Non-Dominated Sorting Algorithm for Many-Objective Optimization" <https://arxiv.org/abs/1809.06106>`_ . The code is included in (class `org.uma.jmetal.component.ranking.MergeNonDominatedSortRanking <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/component/ranking/impl/MergeNonDominatedSortRanking.java>`_). An example of using this class in NSGA-II is contained in `NSGAIIWithMNDSRankingExample <https://github.com/jMetal/jMetal/blob/master/jmetal-example/src/main/java/org/uma/jmetal/example/multiobjective/nsgaii/NSGAIIWithExperimentalNDSAlgorithmExample.java>`_.

* [12/02/2019] New implementation of NSGA-II (class `org.uma.jmetal.algorithm.multiobjective.nsgaii <https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/nsgaii>`_). This class is documented `here <https://jmetal.readthedocs.io/en/latest/nsgaII.html>`_.

* [12/02/2019] Experimental non dominated sorting algorithm (contributed by Maxim Buzdalov). The code is included in (class `org.uma.jmetal.component.ranking.ExperimentalFastNonDominanceRanking <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/component/ranking/impl/ExperimentalFastNonDominanceRanking.java>`_). An example of using this class in NSGA-II is contained in `NSGAIIWithExperimentalNDSAlgorithmExample <https://github.com/jMetal/jMetal/blob/master/jmetal-example/src/main/java/org/uma/jmetal/example/multiobjective/nsgaii/NSGAIIWithExperimentalNDSAlgorithmExample.java>`_.
