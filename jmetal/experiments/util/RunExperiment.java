//  run Experiment.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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

package jmetal.experiments.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;
import jmetal.qualityIndicator.* ;
/**
 * Class implementing the steps to run an experiment
 */
public class RunExperiment extends Thread {

	public Experiment experiment_ ;
	public int id_ ;
	public HashMap<String, Object> map_ ;
	public int numberOfThreads_ ;
	public int numberOfProblems_ ;

	int first_;
	int last_;

	String experimentName_;
	String[] algorithmNameList_; // List of the names of the algorithms to be executed
	String[] problemList_; // List of problems to be solved
	String[] paretoFrontFile_; // List of the files containing the pareto fronts
	// corresponding to the problems in problemList_
	String[] indicatorList_; // List of the quality indicators to be applied
	String experimentBaseDirectory_; // Directory to store the results
	String latexDirectory_; // Directory to store the latex files
	String rDirectory_; // Directory to store the generated R scripts
	String paretoFrontDirectory_; // Directory containing the Pareto front files
	String outputParetoFrontFile_; // Name of the file containing the output
	// Pareto front
	String outputParetoSetFile_; // Name of the file containing the output
	// Pareto set
	int independentRuns_; // Number of independent runs per algorithm
	Settings[] algorithmSettings_; // Paremeter settings of each algorithm

	public RunExperiment(Experiment experiment, 
			HashMap<String, Object> map,
			int id,
			int numberOfThreads,
			int numberOfProblems) {
		experiment_ = experiment ;
		id_ = id ;
		map_ = map ;
		numberOfThreads_ = numberOfThreads  ;
		numberOfProblems_ = numberOfProblems;

		int partitions = numberOfProblems / numberOfThreads;

		first_ = partitions * id;
		if (id == (numberOfThreads - 1)) {
			last_ = numberOfProblems - 1;
		} else {
			last_ = first_ + partitions - 1;
		}

		System.out.println("Id: " + id + "  Partitions: " + partitions +
				" First: " + first_ + " Last: " + last_);
	}

	public void run() {
		Algorithm[] algorithm; // jMetal algorithms to be executed

		String experimentName = (String) map_.get("name");
		experimentBaseDirectory_ = (String) map_.get("experimentDirectory");
		algorithmNameList_ = (String[]) map_.get("algorithmNameList");
		problemList_ = (String[]) map_.get("problemList");
		indicatorList_ = (String[]) map_.get("indicatorList");
		paretoFrontDirectory_ = (String) map_.get("paretoFrontDirectory");
		paretoFrontFile_ = (String[]) map_.get("paretoFrontFile");
		independentRuns_ = (Integer) map_.get("independentRuns");
		outputParetoFrontFile_ = (String) map_.get("outputParetoFrontFile");
		outputParetoSetFile_ = (String) map_.get("outputParetoSetFile");

		int numberOfAlgorithms = algorithmNameList_.length;
		System.out.println("Experiment: Number of algorithms: " + numberOfAlgorithms) ;
		System.out.println("Experiment: runs: " + independentRuns_) ;
		algorithm = new Algorithm[numberOfAlgorithms] ;

		System.out.println("Nombre: " + experimentName);
		System.out.println("experimentDirectory: " + experimentBaseDirectory_);
		System.out.println("numberOfThreads_: " + numberOfThreads_);
		System.out.println("numberOfProblems_: " + numberOfProblems_);
		System.out.println("first: " + first_);
		System.out.println("last: " + last_);

		SolutionSet resultFront = null;  


		for (int problemId = first_; problemId <= last_; problemId++) {
			Problem problem   ; // The problem to solve
			String problemName;   

			// STEP 2: get the problem from the list
			problemName = problemList_[problemId] ;

			// STEP 3: check the file containing the Pareto front of the problem
			synchronized(experiment_) {
				if (indicatorList_.length > 0) {
					File pfFile = new File(paretoFrontDirectory_ + "/" + paretoFrontFile_[problemId]);

					if (pfFile.exists()) {
						paretoFrontFile_[problemId] = paretoFrontDirectory_ + "/" + paretoFrontFile_[problemId];
					} else {
						paretoFrontFile_[problemId] = "";
					}
				} // if
			}
			try {
				experiment_.algorithmSettings(problemName, problemId, algorithm);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			problem = algorithm[0].getProblem() ;
			for (int runs = 0; runs < independentRuns_; runs++) {
				System.out.println("Iruns: " + runs) ;
				// STEP 4: configure the algorithms

				// STEP 5: run the algorithms
				for (int i = 0; i < numberOfAlgorithms; i++) {
					System.out.println(algorithm[i].getClass()) ;
					// STEP 6: create output directories
					File experimentDirectory;
					String directory;

					directory = experimentBaseDirectory_ + "/data/" + algorithmNameList_[i] + "/" +
					problemList_[problemId];

					experimentDirectory = new File(directory);
					if (!experimentDirectory.exists()) {
						boolean result = new File(directory).mkdirs();
						System.out.println("Creating " + directory);
					}

					// STEP 7: run the algorithm
					System.out.println("Running algorithm: " + algorithmNameList_[i] +
							", problem: " + problemList_[problemId] +
							", run: " + runs);
					try {
						try {
							resultFront= algorithm[i].execute();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (JMException ex) {
						Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
					}

					// STEP 8: put the results in the output directory
					resultFront.printObjectivesToFile(directory + "/" + outputParetoFrontFile_ + "." + runs);
					resultFront.printVariablesToFile(directory + "/" + outputParetoSetFile_ + "." + runs);

					// STEP 9: calculate quality indicators
					if (indicatorList_.length > 0) {
						QualityIndicator indicators;
						//System.out.println("PF file: " + paretoFrontFile_[problemId]);
						indicators = new QualityIndicator(problem, paretoFrontFile_[problemId]);

						for (int j = 0; j < indicatorList_.length; j++) {
							if (indicatorList_[j].equals("HV")) {
								double value = indicators.getHypervolume(resultFront);
								FileWriter os;
								try {
									os = new FileWriter(experimentDirectory + "/HV", true);
									os.write("" + value + "\n");
									os.close();
								} catch (IOException ex) {
									Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
								}
							}
							if (indicatorList_[j].equals("SPREAD")) {
								FileWriter os = null;
								try {
									double value = indicators.getSpread(resultFront);
									os = new FileWriter(experimentDirectory + "/SPREAD", true);
									os.write("" + value + "\n");
									os.close();
								} catch (IOException ex) {
									Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
								} finally {
									try {
										os.close();
									} catch (IOException ex) {
										Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
									}
								}
							}
							if (indicatorList_[j].equals("IGD")) {
								FileWriter os = null;
								try {
									double value = indicators.getIGD(resultFront);
									os = new FileWriter(experimentDirectory + "/IGD", true);
									os.write("" + value + "\n");
									os.close();
								} catch (IOException ex) {
									Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
								} finally {
									try {
										os.close();
									} catch (IOException ex) {
										Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
									}
								}
							}
							if (indicatorList_[j].equals("EPSILON")) {
								FileWriter os = null;
								try {
									double value = indicators.getEpsilon(resultFront);
									os = new FileWriter(experimentDirectory + "/EPSILON", true);
									os.write("" + value + "\n");
									os.close();
								} catch (IOException ex) {
									Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
								} finally {
									try {
										os.close();
									} catch (IOException ex) {
										Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
									}
								}
							}
						} // for
					} // if
				} // for
			} // for
		} //for
	}
}
