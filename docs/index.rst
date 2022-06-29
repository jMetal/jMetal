
jMetal project documentation
============================

*Author: Antonio J. Nebro* <ajnebro@uma.es>

jMetal is a Java-based framework for multi-objective optimization with metaheuristics.  The current stable version is 5.12 (https://github.com/jMetal/jMetal), which is based on the description of jMetal 5 included in the paper "Redesigning the jMetal Multi-Objective Optimization Framework" (http://dx.doi.org/10.1145/2739482.2768462), presented at GECCO 2015.

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
| jmetal-example      |  Examples                          |
+---------------------+------------------------------------+
| jmetal-lab          |  Experimentation and visualization |
+---------------------+------------------------------------+
| jmetal-experimental |  New features in development       |
+---------------------+------------------------------------+
| jmetal-parallel     |  Parallel extensions               |
+---------------------+------------------------------------+
| jmetal-auto         |  Auto-design and configuration     |
+---------------------+------------------------------------+
| jmetal-component    |  Component-based algorithms        |
+---------------------+------------------------------------+


.. toctree::
    :maxdepth: 1
    :caption: Index:

    installation.rst
    encodings.rst
    problems.rst
    constraints.rst
    experimentation.rst
    parallel.rst
    autoconfigurationNSGAII.rst
    autoconfigurationMOPSO.rst
    component.rst
    mnds.rst
