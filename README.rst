jMetal 6 project Web site
==========================

jMetal is a Java-based framework for multi-objective optimization with metaheuristics. The current stable version is 5.8 (https://github.com/jMetal/jMetal/tree/jmetal-5.8), which is based on the description of jMetal 5 included in the paper "Redesigning the jMetal Multi-Objective Optimization Framework" (http://dx.doi.org/10.1145/2739482.2768462), presented at GECCO 2015.

After five years working with jMetal 5, a new major release, jMetal 6, is under development and, after some months working of a development branch, it has been moved to the master branch as version 6.0-SNAPSHOT.

A summary of the features of jMetal 6.0 are listed next:

* Use of Sphinx for the documentation. In jMetal 5 the documentation is based on Markdown files; it is not complete and some parts are outdated (this documentation is still available: https://github.com/jMetal/jMetalDocumentation). The documentation of jMetal 6 is being elaborated here :https://jmetal.readthedocs.io/en/latest/.

* Support for automatic configuration of metaheuristics. We include a sub-project called ``jmetal-auto``, which currently contains a version of NSGA-II that can be fully auto tuned using ``irace``, as is described in the paper "Automatic Configuration of NSGA-II with jMetal and irace", presented at GECCO 2019 (DOI: https://doi.org/10.1145/3319619.3326832).

* Improved experimentation. The output of an experiment (i.e., the execution of a number of algorithms on a set of problems) is an CSV file which can be further analyzed to produce Latex tables and graphics with statistical information. I plan to use Tablesaw (https://github.com/jtablesaw/tablesaw) and Smile (http://haifengl.github.io/smile/) for analysis tasks and for visualization. All the experimentation code is located in a new sub-package called ``jmetal-lab``.

* All the core packages in ``jmetal-core`` (``solution``, ``problem``, ``algorithm``, ``operator``, ``quality indicator``), are being reviewed, tested, and refactored. The same applies to the ``jmetal-core/util`` package.

* The implementation of the algorithms in ``jmetal-algorithm`` will be revised. A major issue here is that the current base classes for evolutionary algorithms were designed to be extended by using inheritance, while for the autoconfiguration of algorithms we have designed a new template based on delegation, which is more flexible and allows to fully configure an algorithm with a string of arguments. As for now, the only algorithm we are working with is NSGA-II, so we have now different versions of it (the original one in sub-project ``jmetal-algorithm`` and two new versions in sub-project ``jmetal-auto``).

jMetal 6 is implemented in Java 8 (although I plan to change to Java 11) and it is a Maven project structured in six sub-projects:


+------------------+-----------------------------------+
| Sub-project      |  Contents                         | 
+==================+===================================+
| jmetal-core      |  Core classes                     |
+------------------+-----------------------------------+
| jmetal-solution  |  Solution encodings               |
+------------------+-----------------------------------+
| jmetal-algorithm |  Algorithm implementations        |
+------------------+-----------------------------------+
| jmetal-problem   |  Benchmark problems               |
+------------------+-----------------------------------+
| jmetal-example   |  Examples                         |
+------------------+-----------------------------------+
| jmetal-lab       |  Experimentation                  |
+------------------+-----------------------------------+
| jmetal-auto      |  Auto configuration               |
+------------------+-----------------------------------+

All the code included in jMetal 5.8 is included in jMetal 6.0-SNAPSHOT, so the project is fully functional and we are currently using it in our research work. 

Comments and suggestions are very welcome.

