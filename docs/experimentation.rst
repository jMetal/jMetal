.. _experimentation:

Experimental studies
====================

:Author: Antonio J. Nebro
:Version: Draft
:Date: 2019-10-10


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

    new SolutionListOutput(solutionList)
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

The "VAR.tsv" and "FUN.tsv" file contains the values of the solutions (variables) and the functions, respectively, using a tab (symbol "\\t") as as separator. Thus, if the list has five solutions of problem whose formulation requires four decision variables, the contents of a "VAR.tsv" file would look like this:

.. code-block:: text

  0.5679977855389655 0.8716238660402919 0.499987060282603 0.5000004457944675 
  0.1768906260178687 0.0 0.500115617879 0.500053493354661 0.5000260921344895 
  0.3496133983640447 0.8800155625845776 0.499998935060059 0.4999598389448909
  0.7985363650851307 0.0811579657919492 0.502342224234442 0.5 
  0.7910160252541719 0.7866813061969048 0.49998832945546c 0.5000170172595391

The corresponding function values will we stored in the "FUN.tsv" file (assuming a bi-objective problem):

.. code-block:: text

  0.5 0.1
  0.4 0.2
  0.3 0.3
  0.2 0.4
  0.5 0.1

The reason to separate the output results in two files is that the "FUN.tsv" can be directly plotted with GnuPlot, R, etc.


Steps of an experimental study
------------------------------

An study includes the execution and subsequent analysis of the results of *R* independent runs of *A* algorithms, each of will solve *P* problems. This process is carried out in three steps: 1) execution of all the configurations, 2) apply quality indicators to the obtained fronts, and 3) performance assessment by using statistical tests and generating stuff summarizing the results (e.g., Latex tables, charts).

To illustrate the steps, let us consider that we want to compare four algorithms (NSGA-II, SPEA2, SMPSO, and MOEA/D) when solving the five continuous ZDT problems (ZDT1-4, 6), and the quality indicators hypervolume (*HV*) and additive epsilon (*EP*) will be used. 

1. Execute the algorithms. A number of *R* * *A* * *P* processes will be generated (in our example: *A* = 4, *P* = 5), resulting in the following folder directory:

.. code-block:: text

  └── data
      ├── NSGAII
        ├── ZDT1
        ├── ZDT2
        ├── ZDT3
        ├── ZDT4
        └── ZDT6
      ├── SPEA2
        ├── ZDT1
        ...
      ├── SMPSO
      └── MOEAD

A directory *data* will contain a folder per algorithm, each of which will have a subfolder per problem. The contents of each subfolder is a number of number files named *FUNx.tsv* and *VARx.tsv*, where *x* takes values in the range (0, R-1).

.. code-block:: text

  └── data
      ├── NSGAII
        ├── ZDT1
          ├── FUN0.tsv
          ├── FUN1.tsv
          ├── FUN2.tsv
          ...
          ├── VAR0.tsv
          ├── VAR1.tsv
          ├── VAR2.tsv
          ...
        ├── ZDT2
          ...
        ...
      ...

2. Compute quality indicators. Once the algorithms have been executed it is time to compute quality indicators on the obtained results. As we are considering the *HV* and *EP* indicators, the resulting output is shown next:

.. code-block:: text

  └── data
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

We can observe the generation of a file called *HV*; it will contains the value of the Hypervolume for each of the *R* fronts found in the previous step (for the current combination of algorithm and problem). Additionally, four files are generated, including the fronts with best and median indicator values, respectively. The same applies to the *EP* indicator and others than can be used.

Another result is a CSV file called *QualityIndicatorSummary.csv*, which contains a summary of all the quality indicators results. The header (and some lines of contents) contains these fields: algorithm name, problem name, indicator name (*HV*, *EP*, etc.), execution id (from 0 to *R - 1*), and indicator value. The first lines of this file would look like this:

.. code-block:: csv

 Algorithm,Problem,IndicatorName,ExecutionId,IndicatorValue
 NSGAII,ZDT1,EP,0,0.015705992620067832
 NSGAII,ZDT1,EP,1,0.012832504015918067
 NSGAII,ZDT1,EP,2,0.01071189935186434
 NSGAII,ZDT1,EP,3,0.011465571289007992
 NSGAII,ZDT1,EP,4,0.010279387564947617
 ...

The interesting point of generating the *QualityIndicatorSummary.csv* it that it can be analyzed outside jMetal. For example, you can use Pandas or even the analysis features of jMetalPy (https://github.com/jMetal/jMetalPy), the Python version of jMetal that is in development.

3. Generation of Latex tables and chars. Once the quality indicators have been computed, jMetal includes some functions to generate Latex files containing statistical data (mean/median and standard deviation/IQR,  Friedman ranking) and R scripts producing boxplots and Latex tables containing information about the Wilcoxon rank sum test.

The final folder directory is then the following one:

.. code-block:: text

    ├── QualityIndicatorSummary.csv
    ├── R
    ├── data
    └── latex