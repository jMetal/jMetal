//  SMSEMOA_main.java
//
//  Author:
//       Simon Wessing
//
//  Copyright (c) 2011 Simon Wessing
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
/**
 * SMSEMOA_main.java
 *
 * @author Simon Wessing
 * @version 1.0
 *   This implementation of SMS-EMOA makes use of a QualityIndicator object
 *   to obtained the convergence speed of the algorithm.
 *   
 */
package jmetal.metaheuristics.smsemoa;

import jmetal.core.*;
import jmetal.operators.crossover.*;
import jmetal.operators.mutation.*;
import jmetal.operators.selection.*;
import jmetal.problems.*;
import jmetal.problems.DTLZ.*;
import jmetal.problems.ZDT.*;
import jmetal.problems.WFG.*;
import jmetal.problems.LZ09.*;

import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.io.IOException;

import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import jmetal.qualityIndicator.QualityIndicator;

/**
 * Class for configuring and running the SMS-EMOA algorithm. This 
 * implementation of SMS-EMOA makes use of a QualityIndicator object
 * to obtained the convergence speed of the algorithm.
 */
public class SMSEMOA_main {

	public static Logger logger_;      // Logger object
	public static FileHandler fileHandler_; // FileHandler object

	/**
	 * @param args Command line arguments.
	 * @throws JMException
	 * @throws IOException
	 * @throws SecurityException
	 * Usage: three options
	 *      - jmetal.metaheuristics.smsemoa.SMSEMOA_main
	 *      - jmetal.metaheuristics.smsemoa.SMSEMOA_main problemName
	 *      - jmetal.metaheuristics.smsemoa.SMSEMOA_main problemName paretoFrontFile
	 */
	public static void main(String[] args) throws
	JMException,
	SecurityException,
	IOException,
	ClassNotFoundException {
		Problem problem;         // The problem to solve
		Algorithm algorithm;         // The algorithm to use
		Operator crossover;         // Crossover operator
		Operator mutation;         // Mutation operator
		Operator selection;         // Selection operator

		QualityIndicator indicators; // Object to get quality indicators

		HashMap  parameters ; // Operator parameters

		// Logger object and file to store log messages
		logger_ = Configuration.logger_;
		fileHandler_ = new FileHandler("SMSEMOA_main.log");
		logger_.addHandler(fileHandler_);

		indicators = null;
		if (args.length == 1) {
			Object[] params = {"Real"};
			problem = (new ProblemFactory()).getProblem(args[0], params);
		} // if
		else if (args.length == 2) {
			Object[] params = {"Real"};
			problem = (new ProblemFactory()).getProblem(args[0], params);
			indicators = new QualityIndicator(problem, args[1]);
		} // if
		else { // Default problem
			problem = new Kursawe("Real", 3);
			//problem = new Kursawe("BinaryReal", 3);
			//problem = new Water("Real");
			//problem = new ZDT1("ArrayReal", 100);
			//problem = new ConstrEx("Real");
			//problem = new DTLZ1("Real");
			//problem = new OKA2("Real") ;
		} // else

		algorithm = new SMSEMOA(problem);

		// Algorithm parameters
		algorithm.setInputParameter("populationSize", 100);
		algorithm.setInputParameter("maxEvaluations", 25000);
		algorithm.setInputParameter("offset", 100.0);

    // Mutation and Crossover for Real codification 
    parameters = new HashMap() ;
    parameters.put("probability", 0.9) ;
    parameters.put("distributionIndex", 20.0) ;
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
    parameters.put("distributionIndex", 20.0) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

		// Selection Operator
    parameters = null ;
		selection = SelectionFactory.getSelectionOperator("RandomSelection", parameters);
		// also possible
		//selection = SelectionFactory.getSelectionOperator("BinaryTournament2");

		// Add the operators to the algorithm
		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);

		// Add the indicator object to the algorithm
		algorithm.setInputParameter("indicators", indicators);

		// Execute the Algorithm 
		long initTime = System.currentTimeMillis();
		SolutionSet population = algorithm.execute();
		long estimatedTime = System.currentTimeMillis() - initTime;

		// Result messages
		logger_.info("Total execution time: " + estimatedTime + "ms");
		logger_.info("Variables values have been written to file VAR");
		population.printVariablesToFile("VAR");
		logger_.info("Objectives values have been written to file FUN");
		population.printObjectivesToFile("FUN");

		if (indicators != null) {
			logger_.info("Quality indicators");
			logger_.info("Hypervolume: " + indicators.getHypervolume(population));
			logger_.info("GD         : " + indicators.getGD(population));
			logger_.info("IGD        : " + indicators.getIGD(population));
			logger_.info("Spread     : " + indicators.getSpread(population));
			logger_.info("Epsilon    : " + indicators.getEpsilon(population));

			int evaluations = ((Integer) algorithm.getOutputParameter("evaluations")).intValue();
			logger_.info("Speed      : " + evaluations + " evaluations");
		} // if
	} //main
} // SMSEMOA_main
