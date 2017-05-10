# jMetal 5 Development Site

### Status
[![Build Status](https://travis-ci.org/jMetal/jMetal.svg?branch=master)](https://travis-ci.org/jMetal/jMetal)

**jMetal** is an object-oriented Java-based framework for multi-objective optimization with metaheuristics
The Web page of the project is: [http://jmetal.github.io/jMetal/](http://jmetal.github.io/jMetal/). Former jMetal versions can be found in [SourceForge](http://jmetal.sourceforge.net). The current version is jMetal 5.2. 

The jMetal development version is hosted in this repository; this way, interested users can take a look to
the new incoming features in advance.

If you are interested in contributing with your ideas and comments, please take a look the current discussions in the [Issues section](https://github.com/jMetal/jMetal/issues).

## Changelog of the next incoming release (jMetal 5.3)

### Algorithms
* Algorithm [MOEA/DD](http://ieeexplore.ieee.org/document/6964796/). Contribution of @vinixnan.
* Variants with measures of algorithms WASF-GA, DMOPSO, and SMPSO

### Quality indicators
* The R2 indicator can be applied now to fronts with more than two objectives.

### Miscelanea
* Class [`ChartContainer`](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/chartcontainer/ChartContainer.java), which is being developed by @georgero5. This class allows to display information of algorithms while they are running, such as the current Pareto front approximation or the evolution of indicators such as the Hypervolume. See classes [DMOPSOMeasuresRunner](https://github.com/jMetal/jMetal/blob/master/jmetal-exec/src/main/java/org/uma/jmetal/runner/multiobjective/DMOPSOMeasuresRunner.java) and [NSGAIIMeasuresWithChartsRunner](https://github.com/jMetal/jMetal/blob/master/jmetal-exec/src/main/java/org/uma/jmetal/runner/multiobjective/NSGAIIMeasuresWithChartsRunner.java) for examples using it.
* Added getters and setters to all the parameters of the operators.

### Bugs
* Fixed a Bug in algorithm MOEA/D.

## jMetal is available as a Maven Project in The Central Repository

The link to the modules is: https://search.maven.org/#search%7Cga%7C1%7Cjmetal

## jMetal documentation
The documentation is hosted in https://github.com/jMetal/jMetalDocumentation

## Publications
A.J. Nebro, J.J. Durillo, M. Vergne: "Redesigning the jMetal Multi-Objective Optimization Framework". Proceedings of the Companion Publication of the 2015 on Genetic and Evolutionary Computation Conference (GECCO Companion '15) Pages 1093-1100. DOI: http://dx.doi.org/10.1145/2739482.2768462

## Code coverage (4th April 2016)
Coverage data of the jmetal-core package reported by IntelliJ Idea:

|Class % |Method %| Line % |
|--------|--------|--------|
|51,8% (93/181) |	40.0% (375/393) | 37%% (1183/5084)



