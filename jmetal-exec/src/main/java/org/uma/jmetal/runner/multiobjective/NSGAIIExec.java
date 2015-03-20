package org.uma.jmetal.runner.multiobjective;

/**
 * Created by jmateos on 12/11/2014.
 * Modified by ajnebro on 17/12/14.
 */

/**
 * Run it with intellij: Params: nsgaII org.uma.jmetal.problem.multiobjective.Kursawe
 */
public class NSGAIIExec {
  public static void main(String[] args) throws Exception {
    /*
    AnnotationConfigApplicationContext application = new AnnotationConfigApplicationContext(JMetalApplication.class);
    Algorithm algorithm = (Algorithm) application.getBean(args[0], args[1], args) ;
    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute();
    List<Solution> population = (List<Solution>) algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    new SolutionSetOutput.Printer(population).setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv")).print();
    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }

  @Configuration
  public static class JMetalApplication {
    @Autowired ApplicationContext ctx;
    @Bean(name = "nsgaII")
    @Scope("prototype")
    public Algorithm nsgaII(String algorithmVersion, String[] args) throws FileNotFoundException {
      switch (NsgaIIType.valueOf(algorithmVersion)) {
        case real:
          return (Algorithm) ctx.getBean("nsgaIIReal", args[2], args);
        case binary:
          return (Algorithm) ctx.getBean("nsgaIIBinary", args[2], args);
        //case tsp:
        //  return nsgIITsp();
      }
      throw new JMetalException("NSGII version not implemented");
    }

    @Bean(name = "nsgaIIReal")
    @Scope("prototype")
    public Algorithm nsgaIIReal(String problemName, String[] args) {
      String[] subArray = new String[] {};
      if (args.length > 3) {
        subArray = Arrays.copyOfRange(args, 3, args.length);
      }
      Problem problem = (Problem) ctx.getBean("problem", args[2], subArray);
      MutationOperator mutation = (MutationOperator) ctx.getBean("polynomialMutation", 1.0/problem.getNumberOfVariables(), 0.20);
      CrossoverOperator crossover = (CrossoverOperator) ctx.getBean("singlePointCrossover", 0.9);
      return new NSGAIIBuilder(problem).setCrossoverOperator(crossover).setMutationOperator(
          mutation).setSelectionOperator(selection()).setSolutionListEvaluator(evaluator())
          .setMaxIterations(250).setPopulationSize(100).build();
    }
/*
    @Bean(name = "nsgaIIBinary")
    @Scope("prototype")
    public Algorithm nsgaIIBinary(String problemName, String[] args) {
      String[] subArray = new String[] {};
      if (args.length > 3) {
        subArray = Arrays.copyOfRange(args, 3, args.length);
      }
      Problem problem = (Problem) ctx.getBean("problem", args[2], subArray);
      Operator mutation = (Operator) ctx.getBean("flipMutation", 1.0 / problem.getNumberOfBits());
      Crossover crossover = (Crossover) ctx.getBean("singlePointCrossover", 0.9);
      return new NSGAII.Builder(problem, evaluator()).setCrossover(crossover).setMutation(
          mutation).setSelection(selection())
          .setMaxEvaluations(25000).setPopulationSize(100).build("NSGAII");
    }
*/
    //@Bean(name = "nsgaIITsp")
    //@Scope("prototype")
    //@Lazy
    //public Algorithm nsgIITsp() throws FileNotFoundException {
    //  return new NSGAIITemplate.Builder(multiObjectiveTSP(), evaluator()).setPopulationSize(100).setMaxEvaluations(1000000)
    //      .setCrossover(crossover(0.95)).setMutation(swapMutation()).setSelection(selection()).build("NSGAII");
    //}
/*
    @Bean
    @Lazy
    public SolutionListEvaluator evaluator() {
      return new SequentialSolutionListEvaluator();
    }
*/
    //@Bean
    //@Scope("prototype")
    //@Lazy
    //public Operator crossover(Double probability) {
    //  return new PMXCrossover.Builder().setProbability(probability).build();
    //}

    //@Bean
    //@Lazy
    //public ProblemFactory problemFactory() {
    //  return new ProblemFactory();
    //}
    //@Bean(name = "problem")
    //@Scope("prototype")
    //public Problem problem(String name, String[] args) {
    //  return problemFactory().getProblem(name, args);
    //}
    /*
    @Bean(name = "singlePointCrossover")
    @Scope("prototype")
    public CrossoverOperator singlePointCrossover(Double probability) {
      return new SinglePointCrossover(probability);
    }

    //@Bean
    //@Lazy
    //public Problem multiObjectiveTSP() throws FileNotFoundException {
    //  return new MultiObjectiveTSP("Permutation", "kroA100.tsp", "kroB100.tsp");
    //}
    //@Bean(name = "swapMutation")
    //@Lazy
    //public Operator swapMutation() {
    //  return new SwapMutation.Builder().setProbability(0.2).build();
    //}
    @Bean(name = "flipMutation")
    @Scope("prototype")
    public Operator flipMutation(Double probability) {
      return new BitFlipMutation(probability);
    }

    @Bean(name = "polynomialMutation")
    @Scope("prototype")
    public MutationOperator polynomialMutation(double probability, double distributionIndex) {
      return new PolynomialMutation(probability, distributionIndex);
    }

    @Bean
    public SelectionOperator selection() {
      return new BinaryTournamentSelection();
    }
  }

  public static enum NsgaIIType {
    binary, real, permutation
  }
  */
 }
}
