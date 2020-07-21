.. _experimentation:

Experimental studies
====================

:Author: Antonio J. Nebro
:Author: Javier Pérez Abad
:Version: 1.0
:Date: 2020-15-07


This tutorial describes the features included in jMetal for performing experimental studies related to assessing and comparing the performance of multi-objective algorithms on a set of problems. 

Prerequisites
-------------

Any multi-objective algorithm in jMetal is aimed at finding a Pareto front approximation of a problem, which is returned at the end of the search as a list of solutions. Given an algorithm solving a continuous problem, the result of the optimization can be obtained with the `getResult()` method as follows:

.. code-block:: java

    DoubleProblem problem = new ZDT1() ;
    Algorithm<DoubleSolution> algorithm = new NSGAII(problem, ...) ;

    algorithm.run()

    List<DoubleSolution> solutionList = algorithm.getResult();

To store the results in files, we use an utility, as illustrated below:

.. code-block:: java

        new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

The "VAR.csv" and "FUN.csv" file contains the values of the solutions (variables) and the functions, respectively, using a comma as a separator. Thus, if the list has five solutions of problem whose formulation requires four decision variables, the contents of a "VAR.tsv" file would look like this:

.. code-block:: text

  0.5679977855389655,0.8716238660402919,0.499987060282603,0.5000004457944675 
  0.1768906260178687,0.0 0.500115617879,0.500053493354661,0.5000260921344895 
  0.3496133983640447,0.8800155625845776,0.499998935060059,0.4999598389448909
  0.7985363650851307,0.0811579657919492,0.502342224234442,0.5 
  0.7910160252541719,0.7866813061969048,0.499988329455465,0.5000170172595391

The corresponding function values will we stored in the "FUN.csv" file (assuming a bi-objective problem):

.. code-block:: text

  0.5,0.1
  0.4,0.2
  0.3,0.3
  0.2,0.4
  0.5,0.1

The reason to separate the output results in two files is that the "FUN.csv" can be directly plotted with GnuPlot, R, etc. 


Experimental studies
--------------------

An experimental study is used to make a comparison of a number of algorithms when solving a set of problems. It involves the execution and subsequent analysis of the results of *R* independent runs of *A* algorithms, each of will solve *P* problems. This process is carried out in three steps:

1) Execution of all the configurations (i.e., all the combinations of *A*, *P*, and *R*)
2) Apply quality indicators to the obtained fronts
3) Performance assessment by using statistical tests and generating stuff summarizing the results (e.g., Latex tables, charts)

These steps are supported by a set of classes included in the `jmetal-lab` sub-project, package `org.uma.jmetal.lab.experiment`.

This package has the following structure:

.. code-block:: text

  └── jmetal-lab: org.uma.jmetal.lab.experiment
      ├── Experiment
      ├── ExperimentBuilder
      ├── component
          ├── experimentComponent
          ├── impl
              ├── ExecuteAlgorithms
              ├── ComputeQualityIndicators
              ├── GenerateLatexTablesWithStatistics
              ├── GenerateHtmlPages (NEW)
              ├── GenerateReferenceParetoFront
              ├── GenerateReferenceParetoSetAndFrontFromDoubleSolutions
              ├── GenerateFriedmanHolmTestTables (NEW)
              ├── GenerateBoxplotsWithR
              └── GenerateWilcoxonTestTablesWithR
      ├── studies
          ├── ZDTStudy
          ├── ZDTComputingReferenceParetoFrontsStudy
          ├── NSGAIIStudy
          ├── NSGAIIComputingReferenceParetoFrontsStudy
          ├── ...
          └── BinaryProblemsStudy
          
We can observe that there is a class called `Experiment` (which has an associated `ExperimentBuilder` class), which can be populated with a number of components. The mentioned steps are performed by `ExecuteAlgorithms` (step 1), `ComputeQualityIndicators` (step 2), and the rest of components can be selected to produce a variety of elements to analyze the results, such as Latex tables, figures (boxplots), and HTML pages (a new feature in jMetal). To compute quality indicators, it is necessary to have a reference front per problem; when solving benchmark problems, these fronts are usually known (there are located by default in the `resources` folder of the jMetal project), but this is not the case when dealing with real-world problems. To cope with this issue, we include the  `GenerateReferenceParetoFront` class, which produces reference Pareto fronts from all the results yielded by all the runs of all the algorithms after executing the `ExecuteAlgorithms` component, and the related `GenerateReferenceParetoSetAndFrontFromDoubleSolutions`, which does the same if the problems to solve are continuous; in this case, a reference Pareto set is also generated, as well as files with the contributed solutions of each algorithm to this set. 

To show how these components can be used in an experiment, we have included a number of examples in the `studies` package. We explain next the `ZDTStudy <https://github.com/jMetal/jMetal/blob/master/jmetal-lab/src/main/java/org/uma/jmetal/lab/experiment/studies/ZDTStudy.java>`_ and the `NSGAIIComputingReferenceParetoFrontsStudy <https://github.com/jMetal/jMetal/blob/master/jmetal-lab/src/main/java/org/uma/jmetal/lab/experiment/studies/NSGAIIComputingReferenceParetoFrontsStudy.java>`_ classes.


Class `ZDTStudy`
----------------

This class is intended to cope with the following scenario: we want to compare three algorithms (NSGA-II, SMPSO, and MOEA/D) when solving the five continuous ZDT problems (ZDT1-4, 6), and to apply the quality indicators hypervolume (*HV*) and additive epsilon (*EP*) (for the sake of simplicity, we take these two indicators here; the class contains five quality indicators). We explore the contents of the `ZDTStudy` class next.

Experiment configuration
^^^^^^^^^^^^^^^^^^^^^^^^
After the import section, the code of the class starts in line 52:

.. code-block:: java 
  :linenos: 
  :lineno-start: 52

  public class ZDTStudy {
    private static final int INDEPENDENT_RUNS = 25;

    public static void main(String[] args) throws IOException {
      if (args.length != 1) {
        throw new JMetalException("Missing argument: experimentBaseDirectory");
      }
      String experimentBaseDirectory = args[0];

We can observe that the number of independent runs is set to 25. When an experiment is going to be executed, it will generate a lot of files, so a directory to store all the experiment data is required. 

.. code-block:: java 
   :linenos: 
   :lineno-start: 61

    List<ExperimentProblem<DoubleSolution>> problemList = List.of(
            new ExperimentProblem<>(new ZDT1()),
            // new ExperimentProblem<>(new ZDT1().setReferenceFront("front.csv")) 
            new ExperimentProblem<>(new ZDT2()), 
            new ExperimentProblem<>(new ZDT3()),
            new ExperimentProblem<>(new ZDT4()),
            new ExperimentProblem<>(new ZDT6()));

The list of problems to be solved is configured by default as shown in lines 61-67. The commented line 63 illustrates the case where the default file name containing the reference Pareto front is not named `ZDT1.csv` (the assumed default name) but `front.csv`.

.. code-block:: java 
   :linenos: 
   :lineno-start: 69

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
            configureAlgorithmList(problemList);


A list with the algorithms already configured to be executed is created in a method called `configureAlgorithmList()`, which is included between lines 99-150 in the class. An example of how this list is updated with the SMPSO algorithm is shown next:

.. code-block:: java 
   :linenos: 
   :lineno-start: 99
   
   /**
    * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
    * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}.
    */
   static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
          List<ExperimentProblem<DoubleSolution>> problemList) {
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
    for (int run = 0; run < INDEPENDENT_RUNS; run++) {
      for (var experimentProblem : problemList) {
        double mutationProbability = 1.0 / experimentProblem.getProblem().getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder(
                (DoubleProblem) experimentProblem.getProblem(),
                new CrowdingDistanceArchive<DoubleSolution>(100))
                .setMutation(new PolynomialMutation(mutationProbability, mutationDistributionIndex))
                .setMaxIterations(250)
                .setSwarmSize(100)
                .setSolutionListEvaluator(new SequentialSolutionListEvaluator<>())
                .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
      }

      ... // Configuration of the rest of algorithms

We can observe that there is an outer loop (line 106) and a inner loop (line 107) to create an instance of SMPSO per independent run and problem. The configured algorithm is added to the list in line 118, by creating an instance of class `ExperimentAlgorithm`.

.. code-block:: java 
   :linenos: 
   :lineno-start: 72

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
            new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("ZDTStudy")
                    .setExperimentBaseDirectory(experimentBaseDirectory)
                    .setAlgorithmList(algorithmList)
                    .setProblemList(problemList)
                    .setReferenceFrontDirectory("resources/referenceFrontsCSV")
                    .setOutputParetoFrontFileName("FUN")
                    .setOutputParetoSetFileName("VAR")
                    .setIndicatorList(List.of(
                            new Epsilon<>(),
                            new Spread<>(),
                            new GenerationalDistance<>(),
                            new PISAHypervolume<>(),
                            new InvertedGenerationalDistancePlus<>()))
                    .setIndependentRuns(INDEPENDENT_RUNS)
                    .setNumberOfCores(8)
                    .build();

The experiment is configured using the `ExperimentBuilder` class as shown in lines 72-88. The settings include:

1. The experiment base directory (line 73).
2. Output directory name (line 74). A directory called `ZDTStudy` will be created in the experiment base directory.
3. The algorithm and problem lists (lines 75, 76).
4. The directory containing the reference fronts of the problems (line 77).
5. The default prefix of the names of the output files containing the solutions (`VAR`) and objectives (`FUN`). For each combination of algorithm and problem, the output files will be `FUN0.csv`, `FUN1.csv`, ... , `FUN24.csv`, and `VAR0.csv`, `VAR.csv`, ... ,`VAR24.csv`.
6. A list with the quality indicators (lines 80-85).
7. The number of independent runs (line 86).
8. The number of cores (line 8). An experiment can require a large computing time. This parameter indicates the number of cores that can be used to run the configurations in parallel. 

Algorithm execution
^^^^^^^^^^^^^^^^^^^
Once the experiment class is created and configured, we are ready to execute the algorithms: 

.. code-block:: java 
   :linenos: 
   :lineno-start: 90

   new ExecuteAlgorithms<>(experiment).run();

As a result, the following folder directory will be generated:

.. code-block:: text

  └── data
      ├── NSGAII
        ├── ZDT1
          ├── FUN0.csv
          ├── FUN1.csv
          ├── FUN2.csv
          ├── ... 
          └── FUN24.csv
        ├── ZDT2
          ├── FUN0.csv
          ├── FUN1.csv
          ├── FUN2.csv
          ├── ... 
          └── FUN24.csv
        ├── ZDT3
        ├── ZDT4
        └── ZDT6
      ├── SMPSO
        ├── ZDT1
        ...
      └── MOEAD

A directory called `data` contains a folder per algorithm, each of which stores a sub-folder per problem. The contents of each sub-folder is composed of the files named `FUNx.csv` and `VARx.csv`, where `x` takes values in the range (0, 24), as mentioned before (let's remind that the number of independent runs was set to 25).

Quality Indicator Computing
^^^^^^^^^^^^^^^^^^^^^^^^^^^
When the execution of all the algorithms has finished, the quality indicators are computed next:  

.. code-block:: java 
   :linenos: 
   :lineno-start: 91
   
   new ComputeQualityIndicators<>(experiment).run();


To illustrate the output of this step, let's us consider the hypervolume. The results of calculating this indicator for every `FUNx.csv` file are stored in a file named `HV` (the short name of the indicator), which contains a line per indicator value (in our example, this file contains 25 lines). Furthermore, as sometimes is convenient to know which fronts have the best or median indicator values, the following files are also created: `BEST_HV_FUN.csv`, `BEST_HV_VAR`, `MEDIAN_HV_FUN.csv`, and `MEDIAN_HV_VAR.csv`. The same process is repeated for the rest of quality indicators. 

Consequently, after the computing of the quality indicators, the output directory structure will looks this way:

.. code-block:: text

  └── data
      ├── QualityIndicatorSummary.csv
      ├── NSGAII
        ├── ZDT1
          ├── FUN0.tsv
          ├── FUN1.tsv
          ├── FUN2.tsv
          ...
          ├── HV
          ├── BEST_HV_FUN.tsv
          ├── BEST_HV_VAR.tsv
          ├── MEDIAN_HV_FUN.tsv
          ├── MEDIAN_HV_VAR.tsv
          ├── EP
          ...
          ├── VAR0.tsv
          ├── VAR1.tsv
          ├── VAR2.tsv
          ...
        ├── ZDT2
          ...
        ...
      ...

We observe that another result of this step is a CSV file called `QualityIndicatorSummary.csv`, which contains a summary of all the quality indicator values. The header contains these fields: algorithm name, problem name, indicator name (*HV*, *EP*, etc.), execution id (from 0 to the number of independent runs minus 1), and indicator value. The first lines of this file would look like this:

.. code-block:: text

 Algorithm,Problem,IndicatorName,ExecutionId,IndicatorValue
 NSGAII,ZDT1,EP,0,0.015705992620067832
 NSGAII,ZDT1,EP,1,0.012832504015918067
 NSGAII,ZDT1,EP,2,0.01071189935186434
 NSGAII,ZDT1,EP,3,0.011465571289007992
 NSGAII,ZDT1,EP,4,0.010279387564947617
 ...

The interesting point of generating the *QualityIndicatorSummary.csv* it that it can be analyzed outside jMetal. For example, you can use Pandas or the analysis features of jMetalPy (https://github.com/jMetal/jMetalPy), the Python version of jMetal.

Generation of Latex Tables and R Files
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
The next step after getting the indicator values of the fronts obtained by all the algorithms on the selected problems is to conduct an statistical analysis. To support this analysis, the experiment package of jMetal includes components to  generate Latex files containing statistical data (mean/median and standard deviation/IQR,  Friedman ranking) and R scripts producing boxplots and Latex tables containing information about the Wilcoxon rank sum test. The available components include:

.. code-block:: java 
   :linenos: 
   :lineno-start: 92
   
   new GenerateLatexTablesWithStatistics(experiment).run();
   new GenerateFriedmanHolmTestTables<>(experiment).run();
   new GenerateWilcoxonTestTablesWithR<>(experiment).run();
   new GenerateBoxplotsWithR<>(experiment).setRows(2).setColumns(3).run();

Generation of HTML Pages
^^^^^^^^^^^^^^^^^^^^^^^^
Obtaining Latex files containing tables with statistical data resulting from an experimental study is interesting because those tables can be included in research papers. Still, it is a bit cumbersome to analyze them because all the information is distributed among many files. A new feature included in jMetal 6 is the automatic generation of HTML pages including all these information:

.. code-block:: java
   :linenos:
   :lineno-start: 96

   new GenerateHtmlPages<>(experiment, StudyVisualizer.TYPE_OF_FRONT_TO_SHOW.MEDIAN).run() ;

This component creates a directory called `html` and generates an HTML file per quality indicator (i.e., `EP.html`, `HV.html`, etc.). Each page contains the following:

1. Table with median values.
2. Table with the results of the Wilcoxon rank-sum test.
3. Table with the ranking obtained after applying the Friedman test and the post hoc Holm test.
4. A boxplot per problem.
5. Optionally, a chart with the front having the best or median indicator value.

The interesting point is that all this information is included in a single page that can be visualized in a browser. Just take a look to the generated files:

* `EP.html <_static/html/EP.html>`_
* `HV.html <_static/html/HV.html>`_
* `SPREAD.html <_static/html/SPREAD.html>`_
* `IGDPlus.html <_static/html/IGDPlus.html>`_


Final Result Folder Structure
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
After the execution of the experiment, the output directory will contain the following folders:

.. code-block:: text

    ├── QualityIndicatorSummary.csv
    ├── html
    ├── R
    ├── data
    └── latex


Class `NSGAIIComputingReferenceParetoFrontsStudy`
-------------------------------------------------

The `NSGAIIComputingReferenceParetoFrontsStudy <https://github.com/jMetal/jMetal/blob/master/jmetal-lab/src/main/java/org/uma/jmetal/lab/experiment/studies/NSGAIIComputingReferenceParetoFrontsStudy.java>`_ class contains an example of study where it is assumed that the reference fronts of the problems are unknown and, instead of comparing different algorithms, a number of versions of a single one (NSGA-II) are used; in particular, the idea is to analyze the effect of using different crossover probabilities when using NSGA-II to solve the ZDT problems.

Compared with the `ZDTStudy`, the main differences are:

1. The reference front directory must be empty, and all the generated files will stored into it.
2. Before computing the quality indicators, the reference fronts must be computed:

.. code-block:: java 
   :linenos: 
   :lineno-start: 83

   new ExecuteAlgorithms<>(experiment).run();
   new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
   new ComputeQualityIndicators<>(experiment).run();

In this case, as we are solving again the ZDT problems, we use the component `GenerateReferenceParetoSetAndFrontFromDoubleSolutions`.

3. The name of an algorithm is used by default in all the generated tables and charts, but as we intend to use only NSGA-II, we add a tag when the algorithm is added to the algorithm list (see line 112):

.. code-block:: java 
   :linenos: 
   :lineno-start: 102

    for (int run = 0; run < INDEPENDENT_RUNS; run++) {
      for (ExperimentProblem<DoubleSolution> experimentProblem : problemList) {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(
                experimentProblem.getProblem(),
                new SBXCrossover(1.0, 5),
                new PolynomialMutation(1.0 / experimentProblem.getProblem().getNumberOfVariables(),
                        10.0),
                100)
                .setMaxEvaluations(25000)
                .build();
        algorithms.add(new ExperimentAlgorithm<>(algorithm, "NSGAIIa", experimentProblem, run));
      }

    ...  // Rest of NSGA-II configurations

The NSGA-II variants will be named *NSGAIIa*, *NSGAIIb*, *NSGAIIc*, and *NSGAIId*.