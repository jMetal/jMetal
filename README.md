# jMetal 5 Development Site

## Purpose

**jMetal** is an object-oriented Java-based framework for multi-objective optimization with metaheuristics.
The Web page of the project is: [http://jmetal.github.io/jMetal/](http://jmetal.github.io/jMetal/).
Former jMetal versions can be found in [SourceForge](http://jmetal.sourceforge.net). The current version is jMetal 5.4. 

## How to use

You can find the last released versions of jMetal on the [Maven Central Repository](https://search.maven.org/search?q=g:org.uma.jmetal).

To use jMetal in your project, you need at least the `jmetal-core` artifact.
It provides various components used to implement jMetal algorithms.
To implement your own, you only need this package.

jMetal comes with various algorithms already implemented, which you can obtain by adding the `jmetal-algorithm` artifact.
If you are more interested in experimenting with your own algorithms, you can instead add the `jmetal-problem` artifact to obtain various problems to solve.
You can of course add both of them to try combinations.
If you want to go further by running and evaluating different combinations, you can finally add the `jmetal-exec` artifact, which comes with various utilities.

## Status
[![Build Status](https://travis-ci.org/jMetal/jMetal.svg?branch=master)](https://travis-ci.org/jMetal/jMetal)

The jMetal development version is hosted in this repository; this way, interested users can take a look to the new incoming features in advance.

If you are interested in contributing with your ideas and comments, please take a look at the current discussions in the [Issues section](https://github.com/jMetal/jMetal/issues).

## Changelog of the next incoming release (jMetal 5.5)

### Algorithms

* Improvements in algorithms [WASF-GA](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/wasfga) and [GWASF-GA](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/gwasfga). Contribution of Rubén Saborido.
* Algorithm MOEA/D and variants can now return a subset of evenly distributed solutions (see Miscelanea)
* Algorithm [ESPEA](https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/espea). Contribution of Marlon Braun.
* Algorithm [CDG-MOEA](https://github.com/jMetal/jMetal/blob/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/cdg/CDG.java). Contribution of Feng Zhang and Xinye Cai.

### Problems
* Problems of the [CEC 2018 competition on many-objective optimization](https://github.com/jMetal/jMetal/tree/master/jmetal-problem/src/main/java/org/uma/jmetal/problem/multiobjective/maf). Contribution of Gian Mauricio Fritsche.

### Miscelanea

* Added class [KNearestNeighborhood](https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/neighborhood/impl/KNearestNeighborhood.java). 
* Added new neighborhood classes: C25, C49, L13, L25 and L41 (https://github.com/jMetal/jMetal/tree/master/jmetal-core/src/main/java/org/uma/jmetal/util/neighborhood/impl).
* Added method `getSubsetOfEvenlyDistributedSolutions()` to class [MOEADUtils](https://github.com/jMetal/jMetal/blob/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/moead/util/MOEADUtils.java).

### Bugs
* Fixed a bug in the component of the experiment package that executes the algorithms in parallel. All the experiment classes in package jmetal-exec have been updated.
* Fixed a bug in the experiment framework which led to recompute quality indicators more times than needed.
* Fixed a bug in class `AbstractGenericSolution` caused by solutions stored as attributes. This issue might lead to infinite recursive calls when two solutions are compared.


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
|51,8% (93/181) |	40.0% (375/393) | 37% (1183/5084)
