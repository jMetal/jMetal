.. jmetal documentation master file, created by
   sphinx-quickstart on Wed Sep 11 12:00:46 2019.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

jMetal 6 project documentation Web site
=======================================

.. sectionauthor:: Antonio J. Nebro <ajnebro@uma.es>

jMetal is a Java-based framework for multi-objective optimization with metaheuristics. It is an open-source project released under MIT license, and it is hosted in GitHub: https://github.com/jMetal/jMetal. The current version is jMetal 5.8. The next major release of jMetal will be version 6.0, which is being developed in the ``jmetal6`` branch of the repository as version 6.0-SNAPSHOT.

This Web site is devoted to describing the new features of jMetal 6.0. Please, note that **this is an active development branch**, where some ideas are still not completely defined and many changes will take place until the version is closed. In its current state, this branch is not intended to newcomers but to users already familiarized with jMetal. It is worth mentioning that we have taken as starting point all the code included in jMetal 5.8, so this branch is fully functional and in fact we are currently using it in our research work. Comments and suggestions
are welcome; we encourage interested users in contributing to open new issues in the project repository.

A summary of the features of jMetal 6.0 are listed next:

* Use of Sphinx for the documentation. In jMetal 5 the documentation is based on Markdown files; it not complete and some parts are outdated.

* Support for automatic configuration of metaheuristics. We include a sub-project called ``jmetal-auto``, which currently contains a version of NSGA-II that can be fully auto tuned using ``irace``, as is described in the paper "Automatic Configuration of NSGA-II with jMetal and irace", presented at GECCO 2019 (DOI: https://doi.org/10.1145/3319619.3326832).

* Improved experimentation. The output of an experiment (i.e., the execution of a number of algorithms on a set of problems) is an CSV file which can be further analyzed to produce Latex tables and graphics with statistical information. We plan to use Tablesaw and Smile for the analysis tasks. All the experimentation code is located in sub-package ``jmetal-lab``.

* All the core packages in ``jmetal-core`` (``solution``, ``problem``, ``algorithm``, ``operator``, ``quality indicator``), are being reviewed, tested, and refactored. Some relevant changes already addressed are:

  * All the classes related to solutions encodings depended on a problem (i.e., a sub-class of class ``Problem``). This dependence has been removed, so a solution can be created without the need of a problem to do that.

* The implementation of most of the algorithms in ``jmetal-algorithm`` will be revised. A major issue here is that the current base classes for evolutionary algorithms were designed to be extended by using inheritance, while for the autoconfiguration of algorithms we have designed a new template based on delegation, which is more flexible and allows to fully configure an algorithm with a string of arguments. As for now, the only algorithm we are woking with is NSGA-II, so we have now different versions of it (the original one in sub-project ``jmetal-algorithm`` and two new versions in sub-project ``jmetal-auto``), what can be confusing. 

jMetal 6 is implemented in Java 8 and it is a Maven project structured in six sub-projects:


+------------------+-----------------------------------+
| Sub-project      |  Contents                         | 
+==================+===================================+
| jmetal-core      |  Core classes                     |
+------------------+-----------------------------------+
| jmetal-solution  |  Solution encodings               |
+------------------+-----------------------------------+
| jmetal-algorithm |  Algorithm implementations        |
+------------------+-----------------------------------+
| jmetal-problem   |  Bencharmk problems               |
+------------------+-----------------------------------+
| jmetal-exec      |  Examples                         |
+------------------+-----------------------------------+
| jmetal-lab       |  Experimentation                  |
+------------------+-----------------------------------+
| jmetal-auto      |  Auto configuration               |
+------------------+-----------------------------------+

.. toctree::
    :maxdepth: 2
    :caption: CONTENTS:
    
    installation.rst
    autoconfiguration.rst

Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`
