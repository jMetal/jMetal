# jMetal Development Site

**jMetal** is an object-oriented Java-based framework for multi-objective optimization with metaheuristics
(http://jmetal.sourceforge.net).

The current jMetal development version is hosted in this repository; this way, interested users can take a look to
the new incoming features in advance. If you want to make some contribution, please feel free to clone the project
and submit a pull request.

Suggestions and comments are welcome.

### Incoming features

- We are using Maven since now on.
- SonarQube is used to improve the code quality.
- The multithreaded support (package: jmetal.util.parallel) has been rewritten. TO BE TESTED.
- The Experiment class (package: jmetal.experiments) has been refactored and now the configuration of any experiment
can be set by using properties files. TO BE TESTED.
- New algorithms: StandardPSO2007 and StandardPSO2011 (package: jmetal.metaheuristics.singleObjctive.particleSwarmOptimization).
- New problems: CEC 2005 Competition test suite (class: jmetal.problems.singleObjective.CEC2005Problem). TO BE TESTED.
- New test package to apply unit testing (a lot of work to do here).


