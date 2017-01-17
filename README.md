# jMetal 5 Development Site

### Status
[![Build Status](https://travis-ci.org/jMetal/jMetal.svg?branch=master)](https://travis-ci.org/jMetal/jMetal)

**jMetal** is an object-oriented Java-based framework for multi-objective optimization with metaheuristics
The Web page of the project is: [http://jmetal.github.io/jMetal/](http://jmetal.github.io/jMetal/). Former jMetal versions can be found in [SourceForge](http://jmetal.sourceforge.net). The current version is jMetal 5.1.

The jMetal development version is hosted in this repository; this way, interested users can take a look to
the new incoming features in advance.

If you are interested in contributing with your ideas and comments, please take a look the current discussions in the [Issues section](https://github.com/jMetal/jMetal/issues).

##Changelog of the next incoming release (jMetal 5.2)
### New algorithms
* Coral Reefs Optimization (Single Objective): S. Salcedo-Sanz, J. Del Ser, S. Gil-López, I. Landa-Torres and J. A. Portilla-Figueras, "The coral reefs optimization algorithm: an efficient meta-heuristic for solving hard optimization problems". 15th Applied Stochastic Models and Data Analysis International Conference, Mataró, Spain, June, 2013. Contribution of Inacio Medeiros. 

### Bug fixes
* Class [`Rosenbrock`](https://github.com/jMetal/jMetal/blob/master/jmetal-problem/src/main/java/org/uma/jmetal/problem/singleobjective/Rosenbrock.java) didn't set the lower and upper limits.
* The default number of objectives in class [`UF9`](https://github.com/jMetal/jMetal/blob/master/jmetal-problem/src/main/java/org/uma/jmetal/problem/multiobjective/UF/UF9.java) was not correct.
* A sentece was missing in class [`LZ09`](https://github.com/jMetal/jMetal/blob/master/jmetal-problem/src/main/java/org/uma/jmetal/problem/multiobjective/lz09/LZ09.java).
* Fixed a bug in the `prune()` method of class `AdaptiveGridArchive` (thanks to SunnyWind).

##jMetal is available as a Maven Project in The Central Repository

The link to the modules is: https://search.maven.org/#search%7Cga%7C1%7Cjmetal

##jMetal documentation
The documentation is hosted in https://github.com/jMetal/jMetalDocumentation

##Publications
A.J. Nebro, J.J. Durillo, M. Vergne: "Redesigning the jMetal Multi-Objective Optimization Framework". Proceedings of the Companion Publication of the 2015 on Genetic and Evolutionary Computation Conference (GECCO Companion '15) Pages 1093-1100. DOI: http://dx.doi.org/10.1145/2739482.2768462

##Code coverage (4th April 2016)
Coverage data of the jmetal-core package reported by IntelliJ Idea:

|Class % |Method %| Line % |
|--------|--------|--------|
|51,8% (93/181) |	40.0% (375/393) | 37%% (1183/5084)



