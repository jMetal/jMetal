
jMetal project documentation
==============================

*Author: Antonio J. Nebro* <ajnebro@uma.es>

jMetal is a Java-based framework for multi-objective optimization with metaheuristics.  The current stable version is 5.11 (https://github.com/jMetal/jMetal), which is based on the description of jMetal 5 included in the paper "Redesigning the jMetal Multi-Objective Optimization Framework" (http://dx.doi.org/10.1145/2739482.2768462), presented at GECCO 2015.

The current development version (5.12-SNAPSHOT) is a Maven project structured in seven subprojects:

+----------------------+-------------------------------------+
| Sub-project          | Contents                            | 
+======================+=====================================+
| jmetal-core 	       | Core classes                        |
+----------------------+-------------------------------------+
| jmetal-solution      | Solution encodings                  |
+----------------------+-------------------------------------+
| jmetal-algorithm     | Algorithm implementations           |
+----------------------+-------------------------------------+
| jmetal-problem       | Benchmark problems                  |
+----------------------+-------------------------------------+
| jmetal-example       | Examples                            |
+----------------------+-------------------------------------+
| jmetal-lab           | Experimentation and visualization   |
+----------------------+-------------------------------------+
| jmetal-experimental  | New features under development      |
+----------------------+-------------------------------------+
| jmetal-parallel      | Parallel extensions                 |
+----------------------+-------------------------------------+


.. toctree::
    :maxdepth: 1
    :caption: Index:

    installation.rst
    encodings.rst
    problems.rst
    constraints.rst
    experimentation.rst
    parallel.rst
    autoconfiguration.rst
    mnds.rst
