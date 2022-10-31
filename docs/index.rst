
jMetal project documentation
============================

*Author: Antonio J. Nebro* <ajnebro@uma.es>

jMetal is a Java-based framework for multi-objective optimization with metaheuristics.  The current stable version is 5.11 (https://github.com/jMetal/jMetal), which is based on the description of jMetal 5 included in the paper "Redesigning the jMetal Multi-Objective Optimization Framework" (http://dx.doi.org/10.1145/2739482.2768462), presented at GECCO 2015.

We are currently working on preparing the next major release of jMetal (version 6.0). The version in GitHub now is 6.0-SNAPSHOT.

The jMetal project is a Maven project structured in the following sub-projects:

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


.. toctree::
    :maxdepth: 2
    :caption: Index:

    about.rst
    installation.rst
    encodings.rst
    problems.rst
    constraints.rst
    experimentation.rst
    parallel.rst
    autoconfiguration.rst
    component.rst
    mnds.rst
