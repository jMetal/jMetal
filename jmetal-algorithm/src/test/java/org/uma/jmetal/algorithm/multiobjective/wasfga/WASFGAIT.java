package org.uma.jmetal.algorithm.multiobjective.wasfga;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.ConstrEx;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class WASFGAIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() throws Exception {
    DoubleProblem problem = new ZDT1() ;
    
    Algorithm<List<DoubleSolution>> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
    List<Double> referencePoint = null;
		
    referencePoint = new ArrayList<>();
    referencePoint.add(0.0);
    referencePoint.add(0.0);
  
    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;
  
    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;
  
    selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());
  
    algorithm = new WASFGA<DoubleSolution>(
            problem,
            100,
            250,
            crossover, mutation, selection,new SequentialSolutionListEvaluator<DoubleSolution>(),referencePoint) ;
    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT1, and WASFGA, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(population.size() >= 98) ;
  }
	
	@Test
	public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
		DoubleProblem problem = new ZDT1() ;
		
		Algorithm<List<DoubleSolution>> algorithm;
		CrossoverOperator<DoubleSolution> crossover;
		MutationOperator<DoubleSolution> mutation;
		SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
		List<Double> referencePoint = null;
		
		referencePoint = new ArrayList<>();
		referencePoint.add(0.0);
		referencePoint.add(0.0);
		
		double crossoverProbability = 0.9 ;
		double crossoverDistributionIndex = 20.0 ;
		crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;
		
		double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
		double mutationDistributionIndex = 20.0 ;
		mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;
		
		selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());
		
		algorithm = new WASFGA<DoubleSolution>(
						problem,
						100,
						250,
						crossover, mutation, selection,new SequentialSolutionListEvaluator<DoubleSolution>(),referencePoint) ;
		algorithm.run();
		
		List<DoubleSolution> population = algorithm.getResult();
		
		QualityIndicator<List<DoubleSolution>, Double> hypervolume = new PISAHypervolume<>("/referenceFronts/ZDT4.pf") ;
		
		// Rationale: the default problem is ZDT1, and WASFGA, configured with standard settings, should
		// return find a front with a hypervolume value higher than 0.64
		
		double hv = (Double)hypervolume.evaluate(population) ;
		
		assertTrue(hv > 0.64) ;
	}
}
