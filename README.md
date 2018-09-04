# jMetal 5 Development Site

## Purpose

**jMetal** is an object-oriented Java-based framework for multi-objective optimization with metaheuristics.
The Web page of the project is: [http://jmetal.github.io/jMetal/](http://jmetal.github.io/jMetal/).
Former jMetal versions can be found in [SourceForge](http://jmetal.sourceforge.net). The current version is jMetal 5.6. 

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

## Changelog of the next incoming release (jMetal 5.7)

## jMetal documentation
The documentation is hosted in https://github.com/jMetal/jMetalDocumentation

## Publications
A.J. Nebro, J.J. Durillo, M. Vergne: "Redesigning the jMetal Multi-Objective Optimization Framework". Proceedings of the Companion Publication of the 2015 on Genetic and Evolutionary Computation Conference (GECCO Companion '15) Pages 1093-1100. DOI: http://dx.doi.org/10.1145/2739482.2768462

## Code coverage (5th October 2018)
Coverage data of the jmetal-core package reported by IntelliJ Idea:

|Class % |Method %| Line % |
|--------|--------|--------|
|58% (122/210) |	42% (529/1236) | 40% (2524/6263)
