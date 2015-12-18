# jMetal 5 Development Site

**jMetal** is an object-oriented Java-based framework for multi-objective optimization with metaheuristics
The Web page of the project is: [http://jmetal.github.io/jMetal/](http://jmetal.github.io/jMetal/). Former jMetal versions can be found in [SourceForge](http://jmetal.sourceforge.net).

The current jMetal development version is hosted in this repository; this way, interested users can take a look to
the new incoming features in advance.

If you are interested in contributing with your ideas and comments, please take a look the current discussions in the [Issues section](https://github.com/jMetal/jMetal/issues).

##Changelog
* New algorithms
  * [MOMBI-II](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/mombi) (Improved Metaheuristic Based on the R2 Indicator for Many-Objective Optimization. R. Hernández Gómez, C.A. Coello Coello. Proceeding GECCO '15 Proceedings of the 2015 on Genetic and Evolutionary Computation Conference. Pages 679-686. DOI: [10.1145/2739480.2754776](http://dx.doi.org/10.1145/2739480.2754776)) 
  * [NSGA-III](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/nsgaiii) (Implementation based on the code of Tsung-Che Chiang: [http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm](http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm))
  * [WASF-GA](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/wasfga) (A Preference-based Evolutionary Algorithm for Multiobjective Optimization: The Weighting Achievement Scalarizing Function Genetic Algorithm". Journal of Global Optimization, May 2015, Volume 62, Issue 1, pp 101-129. DOI: [10.1007/s10898-014-0214-y](http://dx.doi.org/10.1007/s10898-014-0214-y)) 
  * [Standard PSO 2007](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/singleobjective/particleswarmoptimization) (single objective)
  * [Standard PSO 2011](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/singleobjective/particleswarmoptimization) (single objective)
  * SMPSOhv: Two variants can be configured depending on the [HypervolumeArchive](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/archive/impl/HypervolumeArchive.java), which can use a [PISAHypervolume](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/qualityindicator/impl/hypervolume/PISAHypervolume.java) or a [WFGHypervolume](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/qualityindicator/impl/hypervolume/WFGHypervolume.java) object.

* New operators
  * BestSolutionSelection

* New util classes
  * AdaptiveRandomNeighborhood
 
* New features
  * [Support for experimental studies](https://github.com/jMetal/jMetal/tree/master/jmetal-exec/src/main/java/org/uma/jmetal/experiment)
  * Classes `PolynomialMutation` and `SBXCrossover` include setters to update their probability and distribution index values.
  * Two implementations of the [Hypervolume](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/qualityindicator/impl/Hypervolume.java) quality indicator are provided: [PISAHypervolume](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/qualityindicator/impl/hypervolume/PISAHypervolume.java) and [WFGHypervolume](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/qualityindicator/impl/hypervolume/WFGHypervolume.java)

* Bugs fixed
  * Fixed a bug in class `DominanceComparator`

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

