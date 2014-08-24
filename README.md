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
* We will incorporate progressively unit tests to all the classes.

### Clean code
After applying clean coding, the `NSGAII.java" program currently looks like this:
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
     population = evaluatePopulation(population);

     // Main loop
     while (!stoppingCondition()) {
       offspringPopulation = new SolutionSet(populationSize);
       for (int i = 0; i < (populationSize / 2); i++) {
         if (!stoppingCondition()) {
           Solution[] parents = new Solution[2];
           parents[0] = (Solution) selectionOperator.execute(population);
           parents[1] = (Solution) selectionOperator.execute(population);

           Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);

           mutationOperator.execute(offSpring[0]);
           mutationOperator.execute(offSpring[1]);

           offspringPopulation.add(offSpring[0]);
           offspringPopulation.add(offSpring[1]);
         }
       }

       offspringPopulation = evaluatePopulation(offspringPopulation);

       Ranking ranking = new Ranking(population.union(offspringPopulation));
       crowdingDistanceSelection(ranking);
     }

     tearDown() ;

     return getNonDominatedSolutions() ;
   }
```

The list of metaheuristics that have been redesigned using clean coding is:
* NSGA-II
* GDE3
* SMPSO
* PAES
* MOCHC
* MOCell
* OMOPSO
* SMPSO
* AbYSS

### Fluent interface
We apply the Fluent Interface to the creation of operators, the configuration of algorithms, and
the reporting of output information. As an example, the `NSGAIIRunner` program to configure and run
NSGA-II looks like this code:

``` java
//  NSGAIIRunner.java

/**
 * Class to configure and execute the NSGA-II algorithm (including Steady State and parallel versions)
 */
public class NSGAIIRunner {
  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException
   * @throws java.lang.ClassNotFoundException
   * Usage: three options
   *           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAIIRunner
   *           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAIIRunner problemName
   *           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAIIRunner problemName paretoFrontFile
   */
  public static void main(String[] args) throws
          JMetalException, SecurityException, IOException, ClassNotFoundException {

    Problem problem;
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

    QualityIndicatorGetter indicators;

    indicators = null;
    if (args.length == 1) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
    } else if (args.length == 2) {
      Object[] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0], params);
      indicators = new QualityIndicatorGetter(problem, args[1]);
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
            .setDistributionIndex(20.0)
            .setProbability(0.9)
            .build() ;

    mutation = new PolynomialMutation.Builder()
            .setDistributionIndex(20.0)
            .setProbability(1.0 / problem.getNumberOfVariables())
            .build();

    selection = new BinaryTournament2.Builder()
            .build();

    algorithm = new NSGAIITemplate.Builder(problem, evaluator)
            .setCrossover(crossover)
            .setMutation(mutation)
            .setSelection(selection)
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
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

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    if (indicators != null) {
      JMetalLogger.logger.info("Quality indicators");
      JMetalLogger.logger.info("Hypervolume: " + indicators.getHypervolume(population));
      JMetalLogger.logger.info("GD         : " + indicators.getGD(population));
      JMetalLogger.logger.info("IGD        : " + indicators.getIGD(population));
      JMetalLogger.logger.info("Spread     : " + indicators.getSpread(population));
      JMetalLogger.logger.info("Epsilon    : " + indicators.getEpsilon(population));
    }
  }
}

```

The list of operators adapted to use the fluent interface is:
* Crossover: `SBXCrossover`, `DifferentialEvolutionCrossover`, `BLXAlphaCrossover`, `SinglePointCrossover`
* Mutation: `PolynomialMutation`, `BitFlipMutation`, `UniformMutation`, `NonUniformMutation`
* Selection: `BinaryTournament2`, `DifferentialEvolutionSelection`, `RandomSelection`, `RankingAndCrowdingSelection`

