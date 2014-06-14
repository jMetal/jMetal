# jMetal Development Site

**jMetal** is an object-oriented Java-based framework for multi-objective optimization with metaheuristics
(http://jmetal.sourceforge.net).

The current jMetal development version is hosted in this repository; this way, interested users can take a look to
the new incoming features in advance.

Suggestions and comments are welcome.

After eight years since the first release of jMetal, we have decided it's time to make a deep redesign of the
software. Some of the ideas we are elaborating are:
* Maven is used as the tool for development, testing, packaging and deployment.
* The encoding takes into account the recommendations provided in “Clean code: A Handbook of Agile Software Craftsmanship" (Robert C. Martin)
* The Fluent Interface (http://martinfowler.com/bliki/FluentInterface.html) is applied to configure and execute
the algorithms.
* We will incorporate progressively unit tests to all the clases.

### Clean code
After applying clean code, the main code of NSGA-II is now as follows:
``` java
   public class NSGAII extends NSGAIITemplate {

     /**
      * Runs the NSGA-II algorithm.
      *
      * @return a <code>SolutionSet</code> that is a set of non dominated solutions
      * as a result of the algorithm execution
      * @throws jmetal.util.JMetalException
      */
     public SolutionSet execute() throws JMetalException, ClassNotFoundException {
       createInitialPopulation();
       evaluatePopulation(population_);

       while (!stoppingCondition()) {
         offspringPopulation_ = new SolutionSet(populationSize_);
         for (int i = 0; i < (populationSize_ / 2); i++) {
           if (!stoppingCondition()) {
             Solution[] parents = new Solution[2];
             parents[0] = (Solution) selectionOperator_.execute(population_);
             parents[1] = (Solution) selectionOperator_.execute(population_);

             Solution[] offSpring = (Solution[]) crossoverOperator_.execute(parents);

             mutationOperator_.execute(offSpring[0]);
             mutationOperator_.execute(offSpring[1]);

             offspringPopulation_.add(offSpring[0]);
             offspringPopulation_.add(offSpring[1]);
           }
         }

         evaluatePopulation(offspringPopulation_);
         evaluations_ += offspringPopulation_.size() ;

         Ranking ranking = new Ranking(population_.union(offspringPopulation_));

         population_.clear();
         int rankingIndex = 0 ;
         while (populationIsNotFull()) {
           if (subfrontFillsIntoThePopulation(ranking, rankingIndex)) {
             addRankedSolutionsToPopulation(ranking, rankingIndex);
             rankingIndex ++ ;
           } else {
             computeCrowdingDistance(ranking, rankingIndex) ;
             addLastRankedSolutions(ranking, rankingIndex);
           }
         }
       }

       tearDown() ;

       return getNonDominatedSolutions() ;
     }
   }
```
### Fluent interface
We apply the Fluent Interface to the creation of operators, the configuration of algorithms, and
the reporting of output information. As an example, the configure and run NSGA-II the `NSGAIIRunner`
program looks like this code:

``` java

package jmetal.runner;

public class NSGAIIRunner {
  private static Logger logger_;
  private static FileHandler fileHandler_;

  /**
   * @param args Command line arguments.
   * @throws jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException Usage: three options
   *                           - jmetal.metaheuristics.nsgaII.NSGAIIRunner
   *                           - jmetal.metaheuristics.nsgaII.NSGAIIRunner problemName
   *                           - jmetal.metaheuristics.nsgaII.NSGAIIRunner problemName paretoFrontFile
   */
  public static void main(String[] args) throws
    JMetalException,
    SecurityException,
    IOException,
    ClassNotFoundException {

    Problem problem;
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

    QualityIndicator indicators;

    // Logger object and file to store log messages
    logger_ = Configuration.logger_;
    fileHandler_ = new FileHandler("NSGAII_main.log");
    logger_.addHandler(fileHandler_);

    indicators = null;
    if (args.length == 1) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
    } else if (args.length == 2) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
      indicators = new QualityIndicator(problem, args[1]);
    } else {
      problem = new Kursawe("Real", 3);
      /*
        Examples:
        problem = new Water("Real");
        problem = new ZDT3("ArrayReal", 30);
        problem = new ConstrEx("Real");
        problem = new DTLZ1("Real");
        problem = new OKA2("Real")
      */
    }

    /*
     * Alternatives:
     * - "NSGAII"
     * - "SteadyStateNSGAII"
     */
    String nsgaIIVersion = "NSGAII" ;

    /*
     * Alternatives:
     * - evaluator = new SequentialSolutionSetEvaluator() // NSGAII
     * - evaluator = new MultithreadedSolutionSetEvaluator(threads, problem) // parallel NSGAII
     */
    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator() ;

    crossover = new SBXCrossover.Builder()
      .distributionIndex(20.0)
      .probability(0.9)
      .build() ;

    mutation = new PolynomialMutation.Builder()
      .distributionIndex(20.0)
      .probability(1.0/problem.getNumberOfVariables())
      .build();

    selection = new BinaryTournament2.Builder()
      .build();

    algorithm = new NSGAIITemplate.Builder(problem, evaluator)
      .crossover(crossover)
      .mutation(mutation)
      .selection(selection)
      .maxEvaluations(25000)
      .populationSize(100)
      .build(nsgaIIVersion) ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute() ;

    SolutionSet population = algorithmRunner.getSolutionSet() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionSetOutput.Printer(population)
      .separator("\t")
      .varFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
      .funFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
      .print();

    logger_.info("Total execution time: " + computingTime + "ms");
    logger_.info("Objectives values have been written to file FUN.tsv");
    logger_.info("Variables values have been written to file VAR.tsv");

    if (indicators != null) {
      logger_.info("Quality indicators");
      logger_.info("Hypervolume: " + indicators.getHypervolume(population));
      logger_.info("GD         : " + indicators.getGD(population));
      logger_.info("IGD        : " + indicators.getIGD(population));
      logger_.info("Spread     : " + indicators.getSpread(population));
      logger_.info("Epsilon    : " + indicators.getEpsilon(population));
    }
  }
}
```



