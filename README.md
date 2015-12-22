# jMetal 5 Development Site

**jMetal** is an object-oriented Java-based framework for multi-objective optimization with metaheuristics
The Web page of the project is: [http://jmetal.github.io/jMetal/](http://jmetal.github.io/jMetal/). Former jMetal versions can be found in [SourceForge](http://jmetal.sourceforge.net).

The current jMetal development version is hosted in this repository; this way, interested users can take a look to
the new incoming features in advance.

If you are interested in contributing with your ideas and comments, please take a look the current discussions in the [Issues section](https://github.com/jMetal/jMetal/issues).

##Changelog of the next incoming release (jMetal 5.1)
* New algorithms
 * [MOCHC](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/mochc) 
 (Optimal antenna placement using a new multi-objective chc algorithm. A.J. Nebro, E. Alba, G. Molina, F. Chicano, F. Luna, J.J. Durillo. GECCO '07: Proceedings of the 9th annual conference on Genetic and evolutionary computation. London, England. July 2007. DOI: [10.1145/1276958.1277128](http://dx.doi.org/10.1145/1276958.1277128)) 
  * [MOMBI-II](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/mombi) (Improved Metaheuristic Based on the R2 Indicator for Many-Objective Optimization. R. Hernández Gómez, C.A. Coello Coello. Proceeding GECCO '15 Proceedings of the 2015 on Genetic and Evolutionary Computation Conference. Pages 679-686. DOI: [10.1145/2739480.2754776](http://dx.doi.org/10.1145/2739480.2754776)) 
  * [NSGA-III](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/nsgaiii) (Implementation based on the code of Tsung-Che Chiang: [http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm](http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm))
  * [WASF-GA](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/wasfga) (A Preference-based Evolutionary Algorithm for Multiobjective Optimization: The Weighting Achievement Scalarizing Function Genetic Algorithm". Journal of Global Optimization, May 2015, Volume 62, Issue 1, pp 101-129. DOI: [10.1007/s10898-014-0214-y](http://dx.doi.org/10.1007/s10898-014-0214-y)) 
  * [GWASF-GA](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/gwasfga) (Global WASF-GA: An Evolutionary Algorithm in Multiobjective Optimization to Approximate the whole Pareto Optimal Front. R. Saborido, A.B. Ruiz and M. Luque. Evolutionary Computation. Accepted for publication. 2015.) 
  * SMPSOhv: Two variants can be configured depending on the [`HypervolumeArchive`](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/archive/impl/HypervolumeArchive.java), which can use a [`PISAHypervolume`](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/qualityindicator/impl/hypervolume/PISAHypervolume.java) or a [`WFGHypervolume`](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/qualityindicator/impl/hypervolume/WFGHypervolume.java) object.
  * [Standard PSO 2007](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/singleobjective/particleswarmoptimization) (single objective)
  * [Standard PSO 2011](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/singleobjective/particleswarmoptimization) (single objective)
  
* New operators
  * [`BestSolutionSelection`](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/operator/impl/selection/BestSolutionSelection.java)

* New util classes
  * [`AdaptiveRandomNeighborhood`](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/neighborhood/impl/AdaptiveRandomNeighborhood.java)
  * [`HypervolumeArchive`](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/archive/impl/HypervolumeArchive.java)
 
* New features
  * Support for [experimental studies](https://github.com/jMetal/jMetal/tree/master/jmetal-exec/src/main/java/org/uma/jmetal/experiment)
    * Parallel algorithm execution.
    * Quality indicators computing.
    * Reference Pareto front approximation computing.
    * Generation of Latex tables:
      * Basic statistics (mean/median and standard deviation/IQR).
      * Wilcoxon rank sum test.
      * Friedman test ranking.
    * Generation of boxplots. 
  * Classes `PolynomialMutation` and `SBXCrossover` include setters to update their probability and distribution index values.
  * Two implementations of the [Hypervolume](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/qualityindicator/impl/Hypervolume.java) quality indicator are provided: [`PISAHypervolume`](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/qualityindicator/impl/hypervolume/PISAHypervolume.java) and [`WFGHypervolume`](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/qualityindicator/impl/hypervolume/WFGHypervolume.java)

* Bugs fixed
  * Fixed a bug in class [`DominanceComparator`](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/comparator/DominanceComparator.java)

##jMetal is available as a Maven Project in The Central Repository

The link to the modules is: https://search.maven.org/#search%7Cga%7C1%7Cjmetal

##jMetal documentation
The documentation is hosted in https://github.com/jMetal/jMetalDocumentation

##Publications
A.J. Nebro, J.J. Durillo, M. Vergne: "Redesigning the jMetal Multi-Objective Optimization Framework". Proceedings of the Companion Publication of the 2015 on Genetic and Evolutionary Computation Conference (GECCO Companion '15) Pages 1093-1100. DOI: http://dx.doi.org/10.1145/2739482.2768462

<!--
####Code coverage (25th January 2015)

|Class % |Method %| Line % |
|--------|--------|--------|
|17,8% (48/ 270) |	16,8% (243/ 1443) |	13,4% (1199/ 8975)

-->

