.. _autoconfigurationNSGAII:

Auto-configuration of evolutionary algorithms: NSGA-II
======================================================

:Author: Antonio J. Nebro <ajnebro@uma.es>
:Date: 2022-1-21

Before reading this section, readers are referred to the paper "Automatic configuration of NSGA-II with jMetal and irace", presented in GECCO 2019 (DOI: https://doi.org/10.1145/3319619.3326832). This tutorial is intended as a guide to replicate the experimentation conducted in that paper, although since the paper was published we have made some updates. Please, take into account that this is a work in progress, so comments, suggestions, and bugs reporting are welcome. The source code is located in the ``org.uma.jmetal.auto`` package in the ``jmetal-auto`` sub-project.

Motivation
----------
A current trend in multi-objective optimization is to use automatic design and automatic parameter configuration tools to find accurate settings of metaheuristics to effectively solve a number of problems. The idea is to avoid the traditional approach of carrying out a number of pilot tests, which are typically conducted without following a systematic strategy. In this context, an algorithm configuration is a complete assignment of values to all required parameters of the algorithm.

The auto-configuration tool we have selected is irace, an R package that implements an
elitist iterated racing algorithm, where algorithm configurations
are sampled from a sampling distribution, uniformly at random at the beginning, but biased towards the best configurations found in later iterations. At each iteration, the generated configurations and
the "elite" ones from previous iterations are raced by evaluating
them on training problem instances. A statistical test is used to
decide which configurations should be eliminated from the race.
When the race terminates, the surviving configurations become
elites for the next iteration.

The issue we studied in the aforementioned paper is how to use jMetal combined with irace to allow the automatic configuration of multiobjective metaheuristics. As this is our first approximation to this matter, we decided to focus on NSGA-II and its use to solve continuous problems.


NSGA-II parameters
------------------
The standard NSGA-II is a generational evolutionary algorithm featured by using a ranking method based on Pareto ranking and the crowding distance density estimator, both in the selection and replacement steps.
When it is used to solve continuous problems, NSGA-II adopts the
simulated binary crossover (SBX) and the polynomial mutation. No
external archive is included in NSGA-II.
However, as we intend to configure NSGA-II in an automatic
way, we need to relax the aforementioned features in order to have
enough flexibility to modify the search capabilities of the algorithm.
This way, we are going to consider that any multi-objective evolutionary
algorithm with the typical parameters (selection, crossover,
and mutation) and using ranking and crowding in the replacement
step can be considered as a variant of NSGA-II.

The components and parameters of NSGA-II that can be tuned are included in this table: 

+---------------------------------------+-----------------------------------------------------+
| Parameter name                        | Allowed values                                      |
+=======================================+=====================================================+
| *populationSize*                      | 100                                                 |
+---------------------------------------+-----------------------------------------------------+
+---------------------------------------+-----------------------------------------------------+
| *algorithmResult*                     | *externalArchive*, *population*                     |
+---------------------------------------+-----------------------------------------------------+
| *populationSizeWithArchive*           | [10, 200]                                           |
+---------------------------------------+-----------------------------------------------------+
| *externalArchive*                     | *crowdingDistanceArchive*, *unboundedArchive*       |
+---------------------------------------+-----------------------------------------------------+
+---------------------------------------+-----------------------------------------------------+
| *createInitialSolutions*              | *random*, *latinHypercubeSampling*, *scatterSearch* |
+---------------------------------------+-----------------------------------------------------+
+---------------------------------------+-----------------------------------------------------+
| *variation*                           | *crossoverAndMutationVariation*                     |
+---------------------------------------+-----------------------------------------------------+
| *offspringPopulationSize*             | [1, 400]                                            |
+---------------------------------------+-----------------------------------------------------+
| *crossover*                           | *SBX*, *BLX_ALPHA*                                  |
+---------------------------------------+-----------------------------------------------------+
| *crossoverProbability*                | [0.0, 1.0]                                          |
+---------------------------------------+-----------------------------------------------------+
| *crossoverRepairStrategy*             | *random*, *round*, *bounds*                         |
+---------------------------------------+-----------------------------------------------------+
| *sbxCrossoverDistributionIndex*       | [5.0, 400.0]                                        |
+---------------------------------------+-----------------------------------------------------+
| *blxAlphaCrossoverAlphaValue*         | [0.0, 1.0]                                          |
+---------------------------------------+-----------------------------------------------------+
| *mutation*                            | *uniform*, *polynomial*, *nonuniform*               |
+---------------------------------------+-----------------------------------------------------+
| *mutationProbabilityFactor*           | [0.0, 2.0]                                          |
+---------------------------------------+-----------------------------------------------------+
| *mutationRepairStrategy*              | *random*, *round*, *bounds*                         |
+---------------------------------------+-----------------------------------------------------+
| *polynomialMutationDistributionIndex* | [5.0, 400.0]                                        |
+---------------------------------------+-----------------------------------------------------+
| *uniformMutationPerturbation*         | [0.0, 1.0]                                          |
+---------------------------------------+-----------------------------------------------------+
| *nonUniformMutationPerturbation*      | [0.0, 1.0]                                          |
+---------------------------------------+-----------------------------------------------------+
+---------------------------------------+-----------------------------------------------------+
| *selection*                           | *random*, *tournament*                              |
+---------------------------------------+-----------------------------------------------------+
| *selectionTournamentSize*             | [2, 10]                                             |
+---------------------------------------+-----------------------------------------------------+

Our *autoNSGAII* can adopt an external archive to store the non-dominated solutions found during the search process. There are two choices for this archive:

* A bounded archive that uses the crowding distance estimator to remove solutions with the archive is full. The size of this archive is given by the *populationSize*.
* An unbounded archive, but when the algorithm finishes a subset of solutions (of size *populationSize*) are selected from the archive and returned as a result. 
  
In case of using no archive, the result of the algorithm is the population, which is configured with the *populationSize* parameter; otherwise, the output is the external archive but then the population size can be tuned by taking values in the range [10, 200]).

The initial population is typically filled with randomly created solutions, but we also allows to use a latin hypercube sampling scheme and a strategy similar to the one used in the scatter search algorithm.

In the classical NSGA-II, the offspring population size is equal to the population size, but we can set its value from 1 (which leads to a steady-state selection scheme) to 400.

The *autoNSGAII* has a *variation* component than can take a single value named *crossoverAndMutationVariation*. It is intended to represent the typical crossover and mutation operators of a genetic algorithm (additional values, e.g., *DifferentialiEvolutionVariation* are expected to be added in the future). The *crossover* operators included are *SBX* (simulated binary crossover) and *BLX_ALPHA*, which are featured by a given probability and a *crossoverRepairStrategy*, which defines what to do when the crossover produces a variable value out of the allowed bounds (please, refer to Section 3.2 and Figure 3 in the paper). The *SBX* and *BLX_ALPHA* require, if selected, a distribution index (a value in the range [5.0, 400]) and an alpha value (in the range [0.0, 1.0]), respectively. 

Similarly, there are several mutation operators to choose from, including *polynomial*, *uniform*, and *nonUniform*, requiring all of them a mutation probability and a repairing strategy; the polynomial mutation has, as the SBX crossover, a distribution index parameter (in the range [5.0, 400]) and the *uniform* and *nonUniform* mutation operators need a perturbation value in the range [0.0, 1.0]. The mutation probability is defined by using a mutation probability factor (a value in the range [0.0, 2.0]), so that the effective mutation probability is the multiplication of that factor with 1.0/N, where N is the number of variables of the problem being optimized.

Finally, the *selection* operator be *random* or *tournament*; this last one can take a value between 2 (i.e., binary tournament) and 10.

As we intend to use irace as auto-tuning package, it requires a text file containing information about the parameters, the values they can take, an their relationships. We have created then a file called ``parameters-NSGAII.txt`` containing the required data::

  populationSize                           "--populationSize "                      o       (100)                                              
  #
  algorithmResult                          "--algorithmResult "                     c       (externalArchive, population)                      
  populationSizeWithArchive                "--populationSizeWithArchive "           i       (10, 200)                      | algorithmResult %in% c("externalArchive")
  externalArchive                          "--externalArchive "                     c       (crowdingDistanceArchive, unboundedArchive) | algorithmResult %in% c("externalArchive")
  #
  createInitialSolutions                   "--createInitialSolutions "              c       (random,latinHypercubeSampling,scatterSearch)
  #
  variation                                "--variation "                           c       (crossoverAndMutationVariation)
  offspringPopulationSize                  "--offspringPopulationSize "             i       (1, 400)
  crossover                                "--crossover "                           c       (SBX,BLX_ALPHA)
  crossoverProbability                     "--crossoverProbability "                r       (0.0, 1.0)                     | crossover %in% c("SBX","BLX_ALPHA")
  crossoverRepairStrategy                  "--crossoverRepairStrategy "             c       (random, round, bounds)        | crossover %in% c("SBX","BLX_ALPHA")
  sbxDistributionIndex                     "--sbxDistributionIndex "                r       (5.0, 400.0)                   | crossover %in% c("SBX")
  blxAlphaCrossoverAlphaValue              "--blxAlphaCrossoverAlphaValue "         r       (0.0, 1.0)                     | crossover %in% c("BLX_ALPHA")
  mutation                                 "--mutation "                            c       (uniform, polynomial, nonUniform)
  mutationProbabilityFactor                "--mutationProbabilityFactor "           r       (0.0, 2.0)                     | mutation %in% c("uniform","polynomial","nonUniform")
  mutationRepairStrategy                   "--mutationRepairStrategy "              c       (random, round, bounds)        | mutation %in% c("uniform","polynomial","nonUniform")
  polynomialMutationDistributionIndex      "--polynomialMutationDistributionIndex " r       (5.0, 400.0)                   | mutation %in% c("polynomial")
  uniformMutationPerturbation              "--uniformMutationPerturbation "         r       (0.0, 1.0)                     | mutation %in% c("uniform")
  nonUniformMutationPerturbation           "--nonUniformMutationPerturbation "      r       (0.0, 1.0)                     | mutation %in% c("nonUniform")
  #
  selection                                "--selection "                           c       (tournament, random)
  selectionTournamentSize                  "--selectionTournamentSize "             i       (2, 10)                        | selection %in% c("tournament")
  #

To know about the syntax of irace configuration files, please refer to the irace documentation.


The ``AutoNSGAII`` class
------------------------
Without entering in implementation details, the auto-configuration of NSGA-II is based on the ``AutoNSGAII`` class located in the ``org.uma.jmetal.auto.autoconfigurablealgorithm.autonsgaii`` package. This class can parse a string defining a particular NSGA-II configuration and create an instance of the algorithm. Each parameter in the string is defined as a pair "--parameterName parameterValue ". An example can be found in the ``AutoNSGAIIConfiguredWithStandardSettingsFromAParameterString`` class:

.. code-block:: java

  public class AutoNSGAIIConfiguredWithStandardSettingsFromAParameterString {

    public static void main(String[] args) {
      String referenceFrontFileName = "ZDT1.csv" ;

      String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
                + "--referenceFrontFileName "+ referenceFrontFileName + " "
                + "--maximumNumberOfEvaluations 25000 "
                + "--algorithmResult population "
                + "--populationSize 100 "
                + "--offspringPopulationSize 100 "
                + "--createInitialSolutions random "
                + "--variation crossoverAndMutationVariation "
                + "--selection tournament "
                + "--selectionTournamentSize 2 "
                + "--rankingForSelection dominanceRanking "
                + "--densityEstimatorForSelection crowdingDistance "
                + "--crossover SBX "
                + "--crossoverProbability 0.9 "
                + "--crossoverRepairStrategy bounds "
                + "--sbxDistributionIndex 20.0 "
                + "--mutation polynomial "
                + "--mutationProbabilityFactor 1.0 "
                + "--mutationRepairStrategy bounds "
                + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

      AutoNSGAII NSGAII = new AutoNSGAII();
      NSGAII.parseAndCheckParameters(parameters);

      EvolutionaryAlgorithm<DoubleSolution> nsgaII = NSGAII.create();

      nsgaII.run();

      new SolutionListOutput(nsgaII.getResult())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();
    }
  }

Auto-configuration process
--------------------------

To replicate the study presented in https://doi.org/10.1145/3319619.3326832 R must be installed. The steps to follow are described next.

Prepare the needed stuff
^^^^^^^^^^^^^^^^^^^^^^^^
The first step is to create a directory for the experiment. Let us called is, for example, ``iraceJMetal ``. This directory must contain:

* The jar file ``jmetal-auto-5.12-jar-with-dependencies.jar``.
* The contents of folder ``jmetal-auto/src/main/resources/irace``:
  
  1. FADS
  2. ASF

* A directory called ``execdir`` that must contain a copy of the ``resources`` folder of the jMetal project. This is needed to allow the algorithm to find the reference fronts.

To generate the ``jmetal-auto-5.12-jar-with-dependencies.jar`` file, just type the following command at the root of the jMetal project:

.. code-block:: bash

  mvn clean package -DskipTests=true

If everything goes fine, the file will be generated in the ``jmetal-auto/target`` folder.

The contents of irace folder are the following:

1. ``irace.tar.gz``: file containing irace
2. ``parameters-NSGAII.txt``: file describing the parameters that can be tuned, including their allowed values and their dependences. You are free to modify some parameter values if you know their meaning.
3. ``instances-list.txt``: the problems to be solved and their reference Pareto fronts are included here. It currently contains the following:

.. code-block:: text
  
  org.uma.jmetal.problem.multiobjective.wfg.WFG1 --referenceFrontFileName WFG1.2D.csv --maximumNumberOfEvaluations 25000
  org.uma.jmetal.problem.multiobjective.wfg.WFG2 --referenceFrontFileName WFG2.2D.csv --maximumNumberOfEvaluations 25000
  org.uma.jmetal.problem.multiobjective.wfg.WFG3 --referenceFrontFileName WFG3.2D.csv --maximumNumberOfEvaluations 25000
  org.uma.jmetal.problem.multiobjective.wfg.WFG4 --referenceFrontFileName WFG4.2D.csv --maximumNumberOfEvaluations 25000
  org.uma.jmetal.problem.multiobjective.wfg.WFG5 --referenceFrontFileName WFG5.2D.csv --maximumNumberOfEvaluations 25000
  org.uma.jmetal.problem.multiobjective.wfg.WFG6 --referenceFrontFileName WFG6.2D.csv --maximumNumberOfEvaluations 25000
  org.uma.jmetal.problem.multiobjective.wfg.WFG7 --referenceFrontFileName WFG7.2D.csv --maximumNumberOfEvaluations 25000
  org.uma.jmetal.problem.multiobjective.wfg.WFG8 --referenceFrontFileName WFG8.2D.csv --maximumNumberOfEvaluations 25000
  org.uma.jmetal.problem.multiobjective.wfg.WFG9 --referenceFrontFileName WFG9.2D.csv --maximumNumberOfEvaluations 25000

Just note that the stopping condition of the algorithm (i.e., the maximum number of evaluations) is set in this file.

4. ``scenario-NSGAII.txt``: default irace parameters (we usually keep this file unchanged)
5. ``target-runner``. Bash script which is executed in every run of irace. This file must have execution rights (if not, just type ``chmod +x target-runner`` in a terminal).
6. ``run.sh``. Bash script to run irace. VERY IMPORTANT: the number of cores to be used by irace are indicated in the ``IRACE_PARAMS`` variable (the default value is 24).

Running everything
------------------

Once you have all the needed resources, just create a folder in the machine where you are going to run the experiment and copy  the contents of the `irace` folder and the ``jmetal-experimental-5.12-SNAPSHOT-jar-with-dependencies.jar`` file into it. Take into account that irace will generate thousands of configurations, so using a multi-core machine is advisable (we use a Linux virtual machine with 32 cores). We have tested the software in Linux, macOS, and Windows 10 (in the Ubuntu Bash console).

To run irace simply run the following command:

.. code-block:: bash

  ./run.sh NSGAII 3

The last parameter is used as a seed.

Results
-------

irace will create a directory called ``execdir`` where it will write a number of output files. Two of those files are of particular interest: ``irace.stderr.out`` and ``irace.sdtout.out``. The first file should be empty, i.e., we should get an empty line are executing this command:

.. code-block:: bash

  cat execdir/irace.stderr.out

The second file contains a lot of information about the run of irace, including the configurations being tested. We are particularly interested in the best found configurations, which are written at the end of the file (just below the line starting by "# Best configuration as command lines"). For example, a result is the following:

.. code-block:: text

  # Best configurations as commandlines (first number is the configuration ID; same order as above):
  4646  --algorithmResult externalArchive --populationSize 100 --populationSizeWithArchive 20 --maximumNumberOfEvaluations 25000 --createInitialSolutions random --variation crossoverAndMutationVariation --offspringPopulationSize 1 --crossover BLX_ALPHA --crossoverProbability 0.876 --crossoverRepairStrategy random --blxAlphaCrossoverAlphaValue 0.5729 --mutation uniform --mutationProbability 0.0439 --mutationRepairStrategy bounds --uniformMutationPerturbation 0.9957 --selection tournament --selectionTournamentSize 8

This configuration can be used with the ``NSGAWithParameters`` program to run NSGA-II with those settings.

NOTE: we fixed a bug in the selection operator, so the best configuration reported in https://doi.org/10.1145/3319619.3326832 will be different to the one that irace can get now. Anyway, the results of AutoNSGAII with the settings found by irace should be very similar to those in the paper.

