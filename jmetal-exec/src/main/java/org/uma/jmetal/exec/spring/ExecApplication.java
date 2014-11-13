package org.uma.jmetal.exec.spring;

import java.io.FileNotFoundException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAIITemplate;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.PMXCrossover;
import org.uma.jmetal.operator.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.BitFlipMutation;
import org.uma.jmetal.operator.mutation.SwapMutation;
import org.uma.jmetal.operator.selection.BinaryTournament2;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.multiobjective.MultiObjectiveTSP;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;
import org.uma.jmetal.util.fileoutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;

/**
 * Created by jmateos on 12/11/2014.
 *
 * Run it with intellij: Params: nsgaII binary Kursawe BinaryReal
 */
public class ExecApplication {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext application = new AnnotationConfigApplicationContext(JMetalApplication.class);

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor((Algorithm) application.getBean(args[0], args[1], args))
                .execute();

        SolutionSet population = algorithmRunner.getSolutionSet();
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

        @Autowired
        ApplicationContext ctx;

        @Bean(name = "nsgaII")
        @Scope("prototype")
        public Algorithm nsgII(String algorithmVersion, String[] args) throws FileNotFoundException {

            switch (NsgaIIType.valueOf(algorithmVersion)) {
            case binary:
                return (Algorithm) ctx.getBean("nsgaIIBinary", args[2], args);
            case tsp:
                return nsgIITsp();
            }

            throw new JMetalException("NSGII version not implemented");
        }

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

        @Bean(name = "nsgaIITsp")
        @Scope("prototype")
        @Lazy
        public Algorithm nsgIITsp() throws FileNotFoundException {
            return new NSGAIITemplate.Builder(multiObjectiveTSP(), evaluator()).setPopulationSize(100).setMaxEvaluations(1000000)
                    .setCrossover(crossover(0.95)).setMutation(swapMutation()).setSelection(selection()).build("NSGAII");
        }

        @Bean
        @Lazy
        public SolutionSetEvaluator evaluator() {
            return new SequentialSolutionSetEvaluator();
        }

        @Bean
        @Scope("prototype")
        @Lazy
        public Operator crossover(Double probability) {
            return new PMXCrossover.Builder().setProbability(probability).build();
        }

        @Bean
        @Lazy
        public ProblemFactory problemFactory() {
            return new ProblemFactory();
        }

        @Bean(name = "problem")
        @Scope("prototype")
        public Problem problem(String name, String[] args) {
            return problemFactory().getProblem(name, args);
        }

        @Bean(name = "singlePointCrossover")
        @Scope("prototype")
        public Crossover singlePointCrossover(Double probability) {
            return new SinglePointCrossover.Builder().setProbability(probability).build();
        }

        @Bean
        @Lazy
        public Problem multiObjectiveTSP() throws FileNotFoundException {
            return new MultiObjectiveTSP("Permutation", "kroA100.tsp", "kroB100.tsp");
        }

        @Bean(name = "swapMutation")
        @Lazy
        public Operator swapMutation() {
            return new SwapMutation.Builder().setProbability(0.2).build();
        }

        @Bean(name = "flipMutation")
        @Scope("prototype")
        public Operator flipMutation(Double probability) {
            return new BitFlipMutation.Builder().setProbability(probability).build();
        }

        @Bean
        public Operator selection() {
            return new BinaryTournament2.Builder().build();

        }
    }

    public static enum NsgaIIType {
        binary, tsp
    }
}
