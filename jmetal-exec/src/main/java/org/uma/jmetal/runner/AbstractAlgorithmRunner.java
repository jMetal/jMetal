package org.uma.jmetal.runner;

import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by ajnebro on 17/7/15.
 */
public abstract class AbstractAlgorithmRunner {
  public static void printFinalSolutionSet(List<? extends Solution<?>> population) {

    new SolutionSetOutput.Printer(population)
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
  }

  public static void printQualityIndicators(List<? extends Solution<?>> population, String paretoFrontFile)
      throws FileNotFoundException {
    Front referenceFront = new ArrayFront(paretoFrontFile);
    FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront) ;

    Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront) ;
    Front normalizedFront = frontNormalizer.normalize(new ArrayFront(population)) ;
    List<DoubleSolution> normalizedPopulation = FrontUtils
        .convertFrontToSolutionList(normalizedFront) ;

    JMetalLogger.logger.info("Hypervolume (N) : " +
        new Hypervolume<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
    JMetalLogger.logger.info("Hypervolume     : " +
        new Hypervolume<List<DoubleSolution>>(referenceFront).evaluate(population));
    JMetalLogger.logger.info("Epsilon (N)     : " +
        new Epsilon<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
    JMetalLogger.logger.info("Epsilon         : " +
        new Epsilon<List<DoubleSolution>>(referenceFront).evaluate(population));
    JMetalLogger.logger.info("GD (N)          : " +
        new GenerationalDistance<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
    JMetalLogger.logger.info("GD              : " +
        new GenerationalDistance<List<DoubleSolution>>(referenceFront).evaluate(population));
    JMetalLogger.logger.info("IGD (N)         : " +
        new InvertedGenerationalDistance<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
    JMetalLogger.logger.info("IGD             : " +
        new InvertedGenerationalDistance<List<DoubleSolution>>(referenceFront).evaluate(population));
    JMetalLogger.logger.info("IGD+ (N)        : " +
        new InvertedGenerationalDistancePlus<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
    JMetalLogger.logger.info("IGD+            : " +
        new InvertedGenerationalDistancePlus<List<DoubleSolution>>(referenceFront).evaluate(population));
    JMetalLogger.logger.info("Spread (N)      : " +
        new Spread<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
    JMetalLogger.logger.info("Spread          : " +
        new Spread<List<DoubleSolution>>(referenceFront).evaluate(population));
    JMetalLogger.logger.info("R2 (N)          : " +
        new R2<List<DoubleSolution>>(normalizedReferenceFront).evaluate(normalizedPopulation));
    JMetalLogger.logger.info("R2              : " +
        new R2<List<DoubleSolution>>(referenceFront).evaluate(population));
    JMetalLogger.logger.info("Error ratio     : " +
        new ErrorRatio<List<DoubleSolution>>(referenceFront).evaluate(population));
    //JMetalLogger.logger.info("SC(pop, ref)    : " +
    //new SetCoverage().evaluate());
    //JMetalLogger.logger.info("SC(ref, pop)    : " + setCoverageRefPop);
  }
}
