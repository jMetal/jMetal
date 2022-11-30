
jMetal project documentation
============================

*Author: Antonio J. Nebro* <ajnebro@uma.es>

jMetal is an open source Java-based framework for multi-objective optimization with metaheuristics.
It includes a wide set of resources, including state-of-the-art multi-objective algorithms,
solution encodings, benchmark problems, quality indicators, and utilities for performing experimental
studies.

The current stable version is 5.11 (https://github.com/jMetal/jMetal), which is based on the
description of jMetal 5 included in the paper "Redesigning the jMetal Multi-Objective Optimization
Framework" (http://dx.doi.org/10.1145/2739482.2768462), presented at GECCO 2015.
We are currently working on preparing the next major release of jMetal (version 6.0).
The version in GitHub now is 6.0-SNAPSHOT.

jMetal is described in the following papers:

* `jMetal: A Java framework for multi-objective optimization <https://doi.org/10.1016/j.advengsoft.2011.05.014>`_. jMetal 4.x
* `Redesigning the jMetal Multi-Objective Optimization Framework <https://doi.org/10.1145/2739482.2768462>`_. jMetal 5.x
* `Automatic configuration of NSGA-II with jMetal and irace <https://dl.acm.org/doi/abs/10.1145/3319619.3326832>`_. jMetal pre-6.0
* `Evolving a Multi-objective Optimization Framework <https://doi.org/10.1007/978-981-16-0662-5_9>`_. jMetal pre-6.0

Summary of features:

* Multi-objective algorithms: NSGA-II, SPEA2, PAES, PESA-II, OMOPSO, MOCell, AbYSS, MOEA/D, GDE3, IBEA, SMPSO, SMPSOhv, SMS-EMOA, MOEA/D-STM, MOEA/D-DE, MOCHC, MOMBI, MOMBI-II, NSGA-III, WASF-GA, GWASF-GA, R-NSGA-II, CDG-MOEA, ESPEA, SMSPO/RP, AGEMOEA, CDG, FAME, MicroFAME, MOSA.
* Single-objective algorithms: genetic algorithm (variants: generational, steady-state), evolution strategy (variants: elitist or mu+lambda, non-elitist or mu, lambda), DE, CMA-ES, PSO (Stantard 2007, Standard 2011), Coral reef optimization.
* Parallel models: Synchronous (multi-threaded, Apache Spark), asynchronous
* Variable representations (encodings): binary, real, integer, permutation, mixed
* Problems:

  - Problem families: ZDT, DTLZ, WFG, RE, CRE, FDA, CEC2009, LZ09, GLT, MOP, LIRCMOP, MOP, UF
  - Classical problems: Kursawe, Fonseca, Schaffer, Viennet2, Viennet3
  - Constrained problems: Srinivas, Tanaka, Osyczka2, Constr_Ex, Golinski, Water, Viennet4
  - Combinatorial problems: multi-objective TSP
  - Academic problems: OneMax, OneZeroMax

* Quality indicators: hypervolume, spread, generational distance, inverted generational distance, inverted generational distance plus, additive epsilon.
* Support for experimental studies
* Support for automatic algorithm configuration and design

.. toctree::
    :maxdepth: 2
    :hidden:
    :caption: Introduction

    about.rst
    installation.rst
    quickStart.rst

.. toctree::
    :maxdepth: 2
    :hidden:
    :caption: Basics

    structure.rst
    encodings.rst
    algorithms.rst
    problems.rst
    constraints.rst

.. toctree::
    :maxdepth: 2
    :hidden:
    :caption: Advanced

    experimentation.rst
    parallel.rst
    autoconfiguration.rst
    component.rst
    mnds.rst
