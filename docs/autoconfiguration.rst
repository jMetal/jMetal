.. _autoconfigurationNSGAII:

Automatic design and configuration of multi-objective metaheuristics
====================================================================

:Author: Antonio J. Nebro <ajnebro@uma.es>
:Date: 2022-11-17

Before reading this section, readers are referred to the papers :ref:`[NLB+19] <NLB+19>` and :ref:`[DNL+22]<DNL+22>`. Please, take into account that this is a research line that is currently guiding the evolution of jMetal,
so changes are expected in the incoming releases.

Motivation
----------
A current trend in multi-objective optimization is to use automatic design and automatic parameter
configuration tools to find accurate settings of metaheuristics to effectively solve a number of problems.
The idea is to avoid the traditional approach of carrying out a number of pilot tests,
which are typically conducted without following a systematic strategy.
In this context, an algorithm configuration is a complete assignment of values to all required parameters
of the algorithm.

Our approach is to combine jMetal with an auto-configuration tool. Concretely, we have selected irace (https://mlopez-ibanez.github.io/irace/),
an R package that implements an elitist iterated racing algorithm, where algorithm configurations
are sampled from a sampling distribution, uniformly at random at the beginning,
but biased towards the best configurations found in later iterations. At each iteration, the generated
configurations and the "elite" ones from previous iterations are raced by evaluating
them on training problem instances. A statistical test is used to
decide which configurations should be eliminated from the race.
When the race terminates, the surviving configurations become
elites for the next iteration.

Using the already existing algorithms in jMetal is not feasible as their design does not fulfill
the requirements of the integration with irace (see again :ref:`[NLB+19] <NLB+19>`).
Our strategy has been to develop two jMetal sub-projects: ``jmetal-component`` and ``jmetal-auto``.

We describe next the two auto-configurable algorithms that are currently available: AutoNSGAII and
AutoMOPSO.

AutoNSGA-II
-----------
The standard NSGA-II is a generational evolutionary algorithm featured by using a ranking method based on
Pareto ranking and the crowding distance density estimator, both in the selection and replacement steps.
When it is used to solve continuous problems, NSGA-II adopts the simulated binary crossover (SBX)
and the polynomial mutation. No external archive is included in NSGA-II.

However, as we intend to configure NSGA-II in an automatic
way, we have extended the aforementioned features in order to have
enough flexibility to modify the search capabilities of the algorithm.
This way, we are going to consider that any multi-objective evolutionary
algorithm with the typical parameters (selection, crossover,
and mutation) and using ranking and crowding in the replacement
step can be considered as a variant of NSGA-II.

The components and parameters of NSGA-II (i.e., the parameter space) that can be tuned are included in this table: 

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

The *autoNSGAII* has a *variation* component than can take a single value named *crossoverAndMutationVariation*. It is intended to represent the typical crossover and mutation operators of a genetic algorithm (additional values, e.g., *DifferentialiEvolutionVariation* are expected to be added in the future). The *crossover* operators included are *SBX* (simulated binary crossover) and *BLX_ALPHA*, which are featured by a given probability and a *crossoverRepairStrategy*, which defines what to do when the crossover produces a variable value out of the allowed bounds (please, refer to Section 3.2 and Figure 3 in the paper). The *SBX* and *BLX_ALPHA* require, if selected, a distribution index (a value within the range [5.0, 400]) and an alpha value (range [0.0, 1.0]), respectively. 

Similarly, there are several mutation operators to choose from, including *polynomial*, *uniform*, and *nonUniform*, requiring all of them a mutation probability and a repairing strategy; the polynomial mutation has, as the SBX crossover, a distribution index parameter (in the range [5.0, 400]) and the *uniform* and *nonUniform* mutation operators need a perturbation value in the range [0.0, 1.0]. The mutation probability is defined by using a mutation probability factor (a value in the range [0.0, 2.0]), so that the effective mutation probability is the multiplication of that factor with 1.0/N, where N is the number of variables of the problem being optimized.

Finally, the *selection* operator be *random* or *tournament*; this last one can take a value between 2 (i.e., binary tournament) and 10.

As we want to use irace as auto-tuning package, it requires a text file containing information about the parameters, the values they can take, an their relationships. We have created then a file called ``parameters-NSGAII.txt`` containing the parameter space::

  populationSize                           "--populationSize "                      o       (100)                                              
  #
  algorithmResult                          "--algorithmResult "                     c       (externalArchive, population)                      
  populationSizeWithArchive                "--populationSizeWithArchive "           i       (10, 200)                      | algorithmResult %in% c("externalArchive")
  externalArchive                          "--externalArchive "                     c       (crowdingDistanceArchive) | algorithmResult %in% c("externalArchive")
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
^^^^^^^^^^^^^^^^^^^^^^^^

Without entering into implementation details, the auto-configuration of NSGA-II is based on the ``AutoNSGAII`` class located in the ``org.uma.jmetal.auto.autoconfigurablealgorithm`` package. This class can parse a string defining a particular NSGA-II configuration and create an instance of the algorithm. Each parameter in the string is defined as a pair "--parameterName parameterValue ". An example can be found in the ``NSGAIIConfiguredFromAParameterString`` class (located in the ``examples`` sub-directory):

.. code-block:: java

  public class NSGAIIConfiguredFromAParameterString {

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

Auto-configuration of AutoNSGA-II
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

To replicate the study presented in :ref:`[NLB+19] <NLB+19>` you must follow the steps indicated in this section.

The software requirements are the following:

* Java JDK (14+)
* R


The first step is to create a directory for the experiment. Let us called is, for example, ``iraceJMetal``. This directory must contain:

* File ``jmetal-auto-6.0-SNAPSHOT-jar-with-dependencies.jar``. To generate this file, just type the following command at the root of the jMetal project:

    .. code-block:: bash

      mvn clean package -DskipTests=true

  If everything goes fine, the file will be generated in the ``jmetal-auto/target`` folder.
* The following contents available in folder ``jmetal-auto/src/main/resources/irace``:
  
  1. ``irace_3.5tar.gz``: file containing irace
  2. ``autoNSGAIIZDT``: directory containing the ``scenario-NSGAII.txt`` configuration file, which is prepared to execute the autoconfiguration of NSGA-II using the ZDT problems as training set and the hypervolume as quality indicator for irace. This directory will contain both the temporal as well as the final files generated during the run of irace.
  3. ``parameters-NSGAII.txt``: file describing the parameters that can be tuned, including their allowed values and their dependencies. You are free to modify some parameter values if you know their meaning.
  4. ``instances-list-ZDT.txt``: the problems to be solved (we are assuming the ZDT suite), their reference Pareto fronts, the maximum number of evaluations to be computed are included here:

  .. code-block:: text
  
org.uma.jmetal.problem.multiobjective.zdt.ZDT1 --referenceFrontFileName ZDT1.csv --maximumNumberOfEvaluations 10000
org.uma.jmetal.problem.multiobjective.zdt.ZDT2 --referenceFrontFileName ZDT2.csv --maximumNumberOfEvaluations 10000
org.uma.jmetal.problem.multiobjective.zdt.ZDT3 --referenceFrontFileName ZDT3.csv --maximumNumberOfEvaluations 10000
org.uma.jmetal.problem.multiobjective.zdt.ZDT4 --referenceFrontFileName ZDT4.csv --maximumNumberOfEvaluations 10000
org.uma.jmetal.problem.multiobjective.zdt.ZDT6 --referenceFrontFileName ZDT6.csv --maximumNumberOfEvaluations 10000
and the stopping condition of the algorithm (i.e., the maximum number of evaluations of the algorithm).

  5. ``target-runner-AutoNSGAIIIraceHV``. Bash script which is executed in every run of irace. This file must have execution rights (if not, just type ``chmod +x target-runner-AutoNSGAIIIraceHV`` in a terminal)
  6. ``run.sh``. Bash script to run irace. IMPORTANT: the number of cores to be used by irace are indicated in the ``IRACE_PARAMS`` variable (the default value is 24).

* A copy of the ``resources`` folder of the jMetal project. This is needed to allow the algorithm to find the reference fronts.


Running irace
^^^^^^^^^^^^^

Once we have all the needed resources in the ``iraceJmetal`` directory, we are ready to execute the script that will carry out the auto-configuraton by using irace. Take into account that irace will generate thousands of configurations (the default value is 100,000), so using a multi-core machine is advisable. We have tested the software in Linux and macOS.

To run irace simply run the following command:

.. code-block:: bash

  ./run.sh autoNSGAIIZDT/scenario-NSGAII.txt 1

The last parameter is used as a run identifier.

Results
^^^^^^^

irace will use the directory called ``autoNSGAIIZDT/execdir-1`` (the 1 is the run identifier) to write a number of output files. Two of those files are of particular interest: ``irace.stderr.out`` and ``irace.sdtout.out``. The first file should be empty, i.e., we should get an empty line when executing this command:

.. code-block:: bash

  cat autoNSGAIIZDT/execdir-1/irace.stderr.out

The second file contains a lot of information about the run of irace, including the configurations being tested. We are particularly interested in the best found configurations, which are written at the end of the file (just below the line starting by "# Best configuration as command lines"). For example, a result is the following:

.. code-block:: text

  # Best configurations as command lines (first number is the configuration ID; same order as above):
  4646  --algorithmResult externalArchive --populationSize 100 --populationSizeWithArchive 20 --maximumNumberOfEvaluations 25000 --createInitialSolutions random --variation crossoverAndMutationVariation --offspringPopulationSize 1 --crossover BLX_ALPHA --crossoverProbability 0.876 --crossoverRepairStrategy random --blxAlphaCrossoverAlphaValue 0.5729 --mutation uniform --mutationProbability 0.0439 --mutationRepairStrategy bounds --uniformMutationPerturbation 0.9957 --selection tournament --selectionTournamentSize 8

This configuration can be used in the ``NSGAIIConfiguredFromAParameterString`` program, replacing the existing one, to run NSGA-II with those settings.

AutoMOPSO
---------

After NSGA-II, the second algorithm we have considered for auto-design and configuration is a multi-objective
particle swarm optimizer (MOPSO). By taking the basic components of two MOPSO algorithms included
in jMetal, namely SMPSO and OMOPSO, we have implemented an ``AutoMOPSO`` class following the same strategy
adopted with ``AutoNSGAII``. This approach has led to the paper ``Automatic Design of Multi-Objective
Particle Swarm Optimizers``, which as been accepted in the ANTs 2022 conference.

The components and parameters space of *AutoMOPSO* are included in the next table:


+--------------------------------------------+----------------------------------------------------------------------------------+
| Parameter name                             | Allowed values                                                                   |
+===============================================================================================================================+
| *swarmSize*                                | [10, 200]                                                                        |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *externalArchive*                          | *crowdingDistanceArchive*, *hypervolumeArchive*, *spatialSpreadDeviationArchive* |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *swarmInitialization*                      | *random*, *latinHypercubeSampling*, *scatterSearch*                              |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *mutation*                                 | *uniform*, *polynomial*, *nonUniform*                                            |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *mutationProbabilityFactor*                | [0.0,2.0]                                                                        |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *mutationRepairStrategy*                   | *random*, *round*, *bounds*                                                      |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *uniformMutationPerturbation*              | [0.0,1.0] *if* mutation=uniform                                                  |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *polynomialMutationDistributionIndex*      | [5.0,400.0] *if* mutation=polynomial                                             |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *nonUniformMutationPerturbation*           | [0.0,1.0] *if* mutation=nonUniform                                               |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *frequencyOfApplicationOfMutationOperator* | [1,10]                                                                           |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *inertiaWeightComputingStrategy*           | *constant*, *random*, *linearIncreasing*, *linearDecreasing*                     |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *weight*                                   | [0.1,1.0]                                                                        |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *weightMin*                                | [0.1,2.0]                                                                        |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *weightMax*                                | [0.5,1.0]                                                                        |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *velocityUpdate*                           | *defaultVelocityUpdate*, *constrainedVelocityUpdate*                             |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *c1Min*                                    | [1.0,2.0]                                                                        |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *c1Max*                                    | [2.0,3.0]                                                                        |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *c2Min*                                    | [1.0,2.0]                                                                        |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *c2Max*                                    | [2.0,3.0]                                                                        |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *globalBestSelection*                      | *binaryTournament*, *random*                                                     |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *velocityChangeWhenLowerLimitIsReached*    | [-1.0,1.0]                                                                       |
+--------------------------------------------+----------------------------------------------------------------------------------+
| *velocityChangeWhenUpperLimitIsReached*    | [-1.0,1.0]                                                                       |
+--------------------------------------------+----------------------------------------------------------------------------------+


With *AutoMOPSO*, we assume that all the MOPSO algorithms have an external archive to store the non-dominated solutions found during the search process.
This archive is bounded, and three density estimators can be used to remove solutions when it is full:
* The crowding distance of NSGA-II
* The contribution to the Hypervolume
* The spatial spread deviation adoted by FAME

As the resulting solutions are those included in the archive, the swarm size can be tuned by taking values in the range [10, 200]).

As in *AutoNSGAII*, the initial swarm can be filled according to three strategies: random, latin hypercube sampling and scatter search.

The MOPSOs generated with *AutoMOPSO* are endowed with perturbation step, consisting in applying
a mutation operator to individuals of the swarm, which are selected according to a 
*frequencyOfApplicationOfMutationOperatorFrequency* parameter ranging between 1 and 10. Thus,
the particles to be mutated are those in positions divisible by that parameter. The mutation operator
are the same used in *AutoNSGAII*. The speeds of the particles are initialized by default to 0.0.

*weightMax* and *weightMin* represent the inertia weight. There are four strategies for computing the inertia weight: constant (a value between 0.1 and 1.0), random, linear increasing and linear decreasing, the three last with minimum and maximum weight values in the ranges [0.1, 0.5] and [0.5, 1.0], respectively.

The two alternatives for velocity updating are the default one, corresponding to the classical scheme that is used in *OMOPSO*, and the constraint speed mechanism applied in *SMPSO*. The *C1* and *C2* (min and max) coefficients take values from the ranges [1.0, 2.0] and [2.0, 3.0], respectively.

The default policies for initializing and updating the local best are that each particle is its local best at the beginning and the local best is updated if the particle dominates it.
The selection of the global best consists in taking solutions from the external archive by applying a random or a binary tournament scheme.

Finally, the default position update also applies the classical strategy, but if the resulting position of a particle is lower than the lower bound of the allowed position values, the position of the particle is set to the lower bound value and the velocity is changed by multiplying if by value in the range [-1, 1]. The same applies in the case of the upper bound.

As with *AutoNSGAII*, we have created then a file called ``parameters-MOPSO.txt`` containing the required information::

  swarmSize                                "--swarmSize "                           i       (10, 200)
  #
  archiveSize                              "--archiveSize "                         o       (100)
  #
  externalArchive                          "--externalArchive "                     c       (crowdingDistanceArchive)
  #
  swarmInitialization                      "--swarmInitialization "                 c       (random, latinHypercubeSampling, scatterSearch)
  #
  velocityInitialization                   "--velocityInitialization "              c       (defaultVelocityInitialization)
  #
  perturbation                             "--perturbation "                        c       (frequencySelectionMutationBasedPerturbation)
  mutation                                 "--mutation "                            c       (uniform, polynomial, nonUniform) | perturbation %in% c("frequencySelectionMutationBasedPerturbation")
  mutationProbability                      "--mutationProbability "                 r       (0.0, 1.0)                     | mutation %in% c("uniform","polynomial","nonUniform")
  mutationRepairStrategy                   "--mutationRepairStrategy "              c       (random, round, bounds)        | mutation %in% c("uniform","polynomial","nonUniform")
  polynomialMutationDistributionIndex      "--polynomialMutationDistributionIndex " r       (5.0, 400.0)                   | mutation %in% c("polynomial")
  uniformMutationPerturbation              "--uniformMutationPerturbation "         r       (0.0, 1.0)                     | mutation %in% c("uniform")
  nonUniformMutationPerturbation           "--nonUniformMutationPerturbation "      r       (0.0, 1.0)                     | mutation %in% c("nonUniform")
  frequencyOfApplicationOfMutationOperator "--frequencyOfApplicationOfMutationOperator " i       (1, 10)                        | perturbation %in% c("frequencySelectionMutationBasedPerturbation")
  #
  inertiaWeightComputingStrategy           "--inertiaWeightComputingStrategy "      c       (constantValue, randomSelectedValue, linearIncreasingValue, linearDecreasingValue)
  weight                                   "--weight "                              r       (0.1, 1.0)                     | inertiaWeightComputingStrategy %in% c("constantValue")
  weightMin                                "--weightMin "                           r       (0.1, 0.5)                     | inertiaWeightComputingStrategy %in% c("randomSelectedValue", "linearIncreasingValue", "linearDecreasingValue")
  weightMax                                "--weightMax "                           r       (0.5, 1.0)                     | inertiaWeightComputingStrategy %in% c("randomSelectedValue", "linearIncreasingValue", "linearDecreasingValue")
  #
  velocityUpdate                           "--velocityUpdate "                      c       (defaultVelocityUpdate, constrainedVelocityUpdate)
  c1Min                                    "--c1Min "                               r       (1.0, 2.0)                     | velocityUpdate %in% c("defaultVelocityUpdate","constrainedVelocityUpdate")
  c1Max                                    "--c1Max "                               r       (2.0, 3.0)                     | velocityUpdate %in% c("defaultVelocityUpdate","constrainedVelocityUpdate")
  c2Min                                    "--c2Min "                               r       (1.0, 2.0)                     | velocityUpdate %in% c("defaultVelocityUpdate","constrainedVelocityUpdate")
  c2Max                                    "--c2Max "                               r       (2.0, 3.0)                     | velocityUpdate %in% c("defaultVelocityUpdate","constrainedVelocityUpdate")
  #
  localBestInitialization                  "--localBestInitialization "             c       (defaultLocalBestInitialization)
  #
  globalBestInitialization                 "--globalBestInitialization "            c       (defaultGlobalBestInitialization)
  #
  globalBestSelection                      "--globalBestSelection "                 c       (binaryTournament, random)
  #
  globalBestUpdate                         "--globalBestUpdate "                    c       (defaultGlobalBestUpdate)
  #
  localBestUpdate                          "--localBestUpdate "                     c       (defaultLocalBestUpdate)
  #
  positionUpdate                           "--positionUpdate "                      c       (defaultPositionUpdate)
  velocityChangeWhenLowerLimitIsReached    "--velocityChangeWhenLowerLimitIsReached " r       (-1.0, 1.0)                    | positionUpdate %in% c("defaultPositionUpdate")
  velocityChangeWhenUpperLimitIsReached    "--velocityChangeWhenUpperLimitIsReached " r       (-1.0, 1.0)                    | positionUpdate %in% c("defaultPositionUpdate")


The ``AutoMOPSO`` class
^^^^^^^^^^^^^^^^^^^^^^^

The ``org.uma.jmetal.auto.autoconfigurablealgorithm`` contains the ``AutoMOPSO`` class, including
an example of how the SMPSO algorithm can be configured using it:

.. code-block:: java

    public class SMPSOConfiguredFromAParameterString {

      public static void main(String[] args) {
        String referenceFrontFileName = "ZDT4.csv";

        String[] parameters =
            ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT4 "
                + "--referenceFrontFileName "
                + referenceFrontFileName
                + " "
                + "--maximumNumberOfEvaluations 25000 "
                + "--swarmSize 100 "
                + "--archiveSize 100 "
                + "--swarmInitialization random "
                + "--velocityInitialization defaultVelocityInitialization "
                + "--leaderArchive crowdingDistanceArchive "
                + "--localBestInitialization defaultLocalBestInitialization "
                + "--globalBestInitialization defaultGlobalBestInitialization "
                + "--globalBestSelection binaryTournament "
                + "--perturbation frequencySelectionMutationBasedPerturbation "
                + "--frequencyOfApplicationOfMutationOperator 7 "
                + "--mutation polynomial "
                + "--mutationProbabilityFactor 1.0 "
                + "--mutationRepairStrategy bounds "
                + "--polynomialMutationDistributionIndex 20.0 "
                + "--positionUpdate defaultPositionUpdate "
                + "--velocityChangeWhenLowerLimitIsReached -1.0 "
                + "--velocityChangeWhenUpperLimitIsReached -1.0 "
                + "--globalBestUpdate defaultGlobalBestUpdate "
                + "--localBestUpdate defaultLocalBestUpdate "
                + "--velocityUpdate constrainedVelocityUpdate "
                + "--inertiaWeightComputingStrategy randomSelectedValue "
                + "--c1Min 1.5 "
                + "--c1Max 2.5 "
                + "--c2Min 1.5 "
                + "--c2Max 2.5 "
                + "--weightMin 0.1 "
                + "--weightMax 0.5 ")
                .split("\\s+");

        AutoMOPSO autoMOPSO = new AutoMOPSO();
        autoMOPSO.parseAndCheckParameters(parameters);

        AutoMOPSO.print(autoMOPSO.fixedParameterList);
        AutoMOPSO.print(autoMOPSO.autoConfigurableParameterList);

        ParticleSwarmOptimizationAlgorithm smpso = autoMOPSO.create();

        RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
            new RunTimeChartObserver<>(
                "SMPSO", 80, 500, "resources/referenceFrontsCSV/" + referenceFrontFileName);

        smpso.getObservable().register(runTimeChartObserver);

        smpso.run();

        JMetalLogger.logger.info("Total computing time: " + smpso.getTotalComputingTime()); ;

        new SolutionListOutput(smpso.getResult())
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

        System.exit(0);
      }
    }

Auto-configuration of AutoSMPSO
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

To replicate the study presented in :ref:`[DNL+22]<DNL+22>` we have just to repeat the same steps followed for AutoNSGA-II, but taking account the following:

1. The parameter file is ``parameters-MOPSO.txt``.
2. The scenario is defined in the file ``autoMOPSOZDT/scenario-MOPSO.txt``.
3. To run irace, the command is:

.. code-block:: bash

  ./run.sh autoNSGAIIZDT/scenario-MOPSO.txt 1

When irace finishes, the best found configuration can be found by typing:

.. code-block:: bash
  
  cat autoMOPSOZDT/execdir-1/irace.stderr.out

At the end of the output file, we can find something similar to this piece of text:

.. code-block:: text

  # Best configurations as commandlines (first number is the configuration ID; same order as above):
  2464  --swarmSize 11 --archiveSize 100 --externalArchive hypervolumeArchive --swarmInitialization scatterSearch --velocityInitialization defaultVelocityInitialization --perturbation frequencySelectionMutationBasedPerturbation --mutation uniform --mutationProbabilityFactor 0.1791 --mutationRepairStrategy random --uniformMutationPerturbation 0.7245 --frequencyOfApplicationOfMutationOperator 8 --inertiaWeightComputingStrategy constantValue --weight 0.1081 --velocityUpdate constrainedVelocityUpdate --c1Min 1.7965 --c1Max 2.4579 --c2Min 1.0514 --c2Max 2.5417 --localBestInitialization defaultLocalBestInitialization --globalBestInitialization defaultGlobalBestInitialization --globalBestSelection binaryTournament --globalBestUpdate defaultGlobalBestUpdate --localBestUpdate defaultLocalBestUpdate --positionUpdate defaultPositionUpdate --velocityChangeWhenLowerLimitIsReached 0.1399 --velocityChangeWhenUpperLimitIsReached -0.7488

This configuration can be used in the ``SMPSOConfiguredFromAParameterString`` program, replacing the existing one, to run *AutoMOPSO* with those settings.

References
----------

.. _NLB+19:

[NLB+19]: Nebro, A.J., López-Ibáñez, M., Barba-González, C., García-Nieto, J.: Automatic configuration of NSGA-II with jMetal and irace. GECCO '19: Proceedings of the Genetic and Evolutionary Computation Conference CompanionJuly 2019. DOI: https://doi.org/10.1145/3319619.3326832

.. _DNL+22:

[DNL+22]: Doblas, D., Nebro, A.J., López-Ibáñez, M., García-Nieto, J, Coello Coello, C.A.: Automatic Design of Multi-objective Particle Swarm Optimizers. International Conference on Swarm Intelligence (ANTS 2022). DOI: https://doi.org/10.1007/978-3-031-20176-9_3
