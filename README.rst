jMetal project Web site
==========================
.. image:: https://travis-ci.org/jMetal/jMetal.svg?branch=master
    :alt: Build Status
    :target: https://travis-ci.org/jMetal/jMetal

.. image:: https://readthedocs.org/projects/jmetal/badge/?version=latest
   :alt: Documentation Status
   :target: https://jmetal.readthedocs.io/?badge=latest

jMetal is a Java-based framework for multi-objective optimization with metaheuristics. The current stable version is 5.10 (https://github.com/jMetal/jMetal/tree/jmetal-5.10), which is based on the description of jMetal 5 included in the paper "Redesigning the jMetal Multi-Objective Optimization Framework" (http://dx.doi.org/10.1145/2739482.2768462), presented at GECCO 2015.

jMetal is implemented in Java 11 and it is a Maven project structured in six sub-projects:


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


Changelog
---------

* [7/15/2020] `Automatic generation of HTML pages <https://jmetal.readthedocs.io/en/latest/experimentation.html#generation-of-html-pages>`_. summarizing the results of experimental studies. Contributed by Javier Pérez Abad.

* [7/14/2020] New experiment component: `GenerateFriedmanHolmTestTables <https://github.com/jMetal/jMetal/blob/master/jmetal-lab/src/main/java/org/uma/jmetal/lab/experiment/component/impl/GenerateFriedmanHolmTestTables.java>`_. Contributed by Javier Pérez Abad.

* [3/19/2020] New quality indicator: `NormalizedHypervolume <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/qualityindicator/impl/NormalizedHypervolume.java>`_.

* [3/19/2020] The jMetal project adopts Java 11.

* [2/11/2020] All the files containing Pareto front approximations and weight vectors have been moved to the ``resources`` folder, located in root project directory.

* [1/23/2020] A solution encoding for defining solution having mixed internal representations has been developed using class `CompositeSolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/compositesolution/CompositeSolution.java>`_. This class acts as solution container, so their variables are solutions of any kind. Variation operators for this class have been developed (classes `CompositeCrossover <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/operator/crossover/impl/CompositeCrossover.java>`_ and `CompositeMutation <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/operator/mutation/impl/CompositeMutation.java>`_). An example of configuring and running NSGA-II to solve a problem using this encoding is available in class `NSGAIIWithMixedSolutionEncodingExample <https://github.com/jMetal/jMetal/blob/master/jmetal-example/src/main/java/org/uma/jmetal/example/multiobjective/nsgaii/NSGAIIWithMixedSolutionEncodingExample.java>`_.  Any feedback about this new feature is welcome.

* [12/10/2019] Merge non dominated sorting algorithm (contributed by Javier Moreno), described in `"Merge Non-Dominated Sorting Algorithm for Many-Objective Optimization" <https://arxiv.org/abs/1809.06106>`_ . The code is included in (class `org.uma.jmetal.component.ranking.MergeNonDominatedSortRanking <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/component/ranking/impl/MergeNonDominatedSortRanking.java>`_). An example of using this class in NSGA-II is contained in `NSGAIIWithMNDSRankingExample <https://github.com/jMetal/jMetal/blob/master/jmetal-example/src/main/java/org/uma/jmetal/example/multiobjective/nsgaii/NSGAIIWithExperimentalNDSAlgorithmExample.java>`_.

* [12/02/2019] Experimental non dominated sorting algorithm (contributed by Maxim Buzdalov). The code is included in (class `org.uma.jmetal.component.ranking.ExperimentalFastNonDominanceRanking <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/component/ranking/impl/ExperimentalFastNonDominanceRanking.java>`_). An example of using this class in NSGA-II is contained in `NSGAIIWithExperimentalNDSAlgorithmExample <https://github.com/jMetal/jMetal/blob/master/jmetal-example/src/main/java/org/uma/jmetal/example/multiobjective/nsgaii/NSGAIIWithExperimentalNDSAlgorithmExample.java>`_.
