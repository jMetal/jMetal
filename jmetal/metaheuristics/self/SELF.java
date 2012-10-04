/**
 * SELF
 * @author Juan J. Durillo, Antonio J. Nebro
 * @version 1.0
 */
package jmetal.metaheuristics.self;


import jmetal.core.*;

import java.util.Comparator;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.*;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.offspring.*;

/**
 * Class representing the MoCell algorithm
 */
public class SELF extends Algorithm {
	int[] contributionCounter_; // contribution per crossover operator
	double[] contribution_; // contribution per crossover operator
	double total = 0.0;


	/******** THIS PIECE OF CODE HAS BEEN ADDED BY JUANJO ******************/
	int [][] angles_;
	/************************************************************************/

	int currentCrossover_;
	int[][] contributionArchiveCounter_;
	public int windowsSize = 10;
	public double mincontribution = 0.30;
	int windowsIndex = 0;

	final boolean TRAZA = true;

	public SELF(Problem problem) {
		super(problem);
	}

	/** Execute the algorithm
	 * @throws JMException
	 * @throws ClassNotFoundException */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int a = problem_.getNumberOfObjectives() ;

		//Init the param
		int populationSize, archiveSize, maxEvaluations, evaluations;
		Operator mutationOperator, crossoverOperator, selectionOperator;
		SolutionSet currentPopulation;
		SolutionSet archive;
		//SolutionSet todas = new SolutionSet(3000000);
		SolutionSet[] neighbors;
		Neighborhood neighborhood;
		boolean change = false;
		int times = 0;
/*
    FileUtils.createEmtpyFile("HV.evol") ;
    FileUtils.createEmtpyFile("EPSILON.evol") ;
    FileUtils.createEmtpyFile("SPREAD.evol") ;
    FileUtils.createEmtpyFile("IGD.evol") ;
    FileUtils.createEmtpyFile("HV") ;
    FileUtils.createEmtpyFile("EPSILON") ;
    FileUtils.createEmtpyFile("SPREAD") ;
    FileUtils.createEmtpyFile("IGD") ;
    FileUtils.createEmtpyFile("cont0.evol") ;
    FileUtils.createEmtpyFile("cont1.evol") ;
    FileUtils.createEmtpyFile("cont2.evol") ;
    FileUtils.createEmtpyFile("de.evol") ;
    FileUtils.createEmtpyFile("sbx.evol") ;
    FileUtils.createEmtpyFile("pol.evol") ;
    FileUtils.createEmtpyFile("cont0.real.evol") ;
    FileUtils.createEmtpyFile("cont1.real.evol") ;
    FileUtils.createEmtpyFile("cont2.real.evol") ;
*/
    double contrDE = 0;
    double contrSBX = 0;
    double contrPol = 0;
    double contrTotalDE = 0;
    double contrTotalSBX = 0;
    double contrTotalPol = 0;
    
    double contrReal [] = new double[3] ;
    contrReal[0] = contrReal[1] = contrReal[2] = 0 ;    

		QualityIndicator indicators; // QualityIndicator object

		Comparator dominance = new DominanceComparator();
		Comparator crowdingComparator = new CrowdingComparator();
		Distance distance = new Distance();

		//Read parameter values
		populationSize = ((Integer) getInputParameter("populationSize")).intValue();
		archiveSize = ((Integer) getInputParameter("archiveSize")).intValue();
		maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();


		indicators = (QualityIndicator) getInputParameter("indicators");
		int feedback = ((Integer) getInputParameter("feedback")).intValue();;
		
		//Init the variables
		currentPopulation = new SolutionSet(populationSize);
		archive = new CrowdingArchive(archiveSize, problem_.getNumberOfObjectives());
		evaluations = 0;
		//neighborhood = new Neighborhood(populationSize);
		//neighbors = new SolutionSet[populationSize];

		Offspring[] getOffspring;
		int N_O; // number of offpring objects

		getOffspring = ((Offspring[]) getInputParameter("offspringsCreators"));
		N_O = getOffspring.length;

		contribution_               = new double[N_O];
		contributionCounter_        = new int[N_O];
		contributionArchiveCounter_ = new int[N_O][windowsSize];

		contribution_[0] = (double) (populationSize / (double) N_O) / (double) populationSize;
		for (int i = 1; i < N_O; i++) {
			contribution_[i] = (double) (populationSize / (double) N_O) / (double) populationSize + (double) contribution_[i - 1];
		}


		currentCrossover_ = 0;

		int iterationsWithMinimumContribution = 0;

		//Create the initial population
		for (int i = 0; i < populationSize; i++) {
			Solution individual = new Solution(problem_);
			problem_.evaluate(individual);
			problem_.evaluateConstraints(individual);
			//todas.add(new Solution(individual));
			currentPopulation.add(individual);
			individual.setLocation(i);
			evaluations++;
		}

		for (int i = 0; i < N_O; i++) {
			for (int j = 0; j < windowsSize; j++) {
				contributionArchiveCounter_[i][j] = 0;
			}
		}

		while (evaluations < maxEvaluations) {
			for (int ind = 0; ind < currentPopulation.size(); ind++) {
				Solution individual = new Solution(currentPopulation.get(ind));

				Solution[] parents = new Solution[2];
				Solution offSpring = null;

				boolean found = false;

				int selected = 0;
				double rnd = PseudoRandom.randDouble();
				for (selected = 0; selected < N_O; selected++) {

					if (!found && (rnd <= contribution_[selected])) {
						if ("DE".equals(getOffspring[selected].id())) {
							offSpring = getOffspring[selected].getOffspring(currentPopulation, individual, ind) ;
            	contrDE++;
						} else if ("SBX_Polynomial".equals(getOffspring[selected].id())) {
							offSpring = getOffspring[selected].getOffspring(currentPopulation);
            	contrSBX++;
						} else if ("Polynomial".equals(getOffspring[selected].id())) {
							offSpring = ((PolynomialOffspringGenerator)getOffspring[selected]).getOffspring(individual);
              contrPol++;
						}

						offSpring.setFitness((int) selected);
						currentCrossover_ = selected;
						found = true;
					} // if
				} // for

				// Evaluate individual an its constraints
				problem_.evaluate(offSpring);
				//problem_.evaluateConstraints(offSpring);

				evaluations++;
                               // if (evaluations%100==0) {
                               //     System.out.println(evaluations + " " + indicators.getHypervolume(archive));
                               // }
                                    
                                
				if (evaluations  %  25000 == 0)
					change = true;

				offSpring.setLocation(-1);
				SolutionSet ss = new SolutionSet(currentPopulation.size()+1);
				for (int i = 0; i < currentPopulation.size();i++) {
					ss.add(currentPopulation.get(i));
				}
				ss.add(offSpring);

				Ranking rank = new Ranking(ss);
				for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
					distance.crowdingDistanceAssignment(rank.getSubfront(j),
							problem_.getNumberOfObjectives());
				}
								
				ss.sort(crowdingComparator);
				Solution worst = ss.get(ss.size() - 1);

				if (worst.getLocation() == -1) { //The worst is the offspring
					archive.add(new Solution(offSpring));
				} else {
					offSpring.setLocation(worst.getLocation());
					currentPopulation.replace(offSpring.getLocation(), offSpring);
					archive.add(new Solution(offSpring));
				}
			}

			for (int i = 0; i < N_O; i++) {
				contributionCounter_[i] = 0;
				contributionArchiveCounter_[i][windowsIndex] = 0;
			}

			/************************************************************************/

			for (int i = 0; i < archive.size(); i++) {
				if ((int) archive.get(i).getFitness() != -1) {
					contributionArchiveCounter_[(int) archive.get(i).getFitness()][windowsIndex] += 1;
					contrReal[(int) archive.get(i).getFitness()]++ ;
				}
				archive.get(i).setFitness(-1);
			}
			windowsIndex = (windowsIndex + 1) % windowsSize;

			int minimumContribution             = 0;
			int totalContributionCounter        = 0;
			int totalcontributionArchiveCounter = 0;

			for (int i = 0; i < N_O; i++) {
				for (int j = 0; j < windowsSize; j++) {
					if (contributionCounter_[i] < minimumContribution) {
						contributionCounter_[i] = minimumContribution;
					}
					if (contributionArchiveCounter_[i][j] < minimumContribution) {
						contributionArchiveCounter_[i][j] = minimumContribution;
					}
					totalContributionCounter += contributionCounter_[i];
					totalcontributionArchiveCounter += contributionArchiveCounter_[i][j];
				}
			}

			boolean minimum = true;
			for (int i = 0; i < N_O;i++) {
				minimum = minimum && (contributionCounter_[i] == minimumContribution);
			}
			if (minimum)
				times ++;
			//
			//      if (minimum) {
				//    	  times--;
				//    	  if (times == 0) {
			//    		  System.out.println("A cambiarrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
			//    		  for (int i = 0; i < N_O; i++) {
			//    		//	  contributionCounter_[i] = PseudoRandom.randInt(minimumContribution,archive.size()-1);
			//    		//	  contributionArchiveCounter_[i] = contributionCounter_[i];
			//    		  }
			//    		  times = 10;
			//    	  }
			//      } else {
			//    	  times = 10;
			//      }
			//


			int aux = 0;
			for (int j = 0; j < windowsSize; j++) {
				aux += contributionArchiveCounter_[0][j];
			}
			//      contribution_[0] = (double) aux/ (double) totalcontributionArchiveCounter;
			//      System.out.print(contribution_[0]+" ");
			//      for (int i = 1; i < N_O; i++) {
			//          aux = 0;
			//          for (int j = 0; j < windowsSize; j++) {
			//            aux += contributionArchiveCounter_[i][j];
			//          }
			//        contribution_[i] = contribution_[i - 1] + (double) aux / (double) totalcontributionArchiveCounter;
			//        System.out.print(((double) aux / (double) totalcontributionArchiveCounter)+" ");
			//
			//      }
			//      System.out.println();

			double [] miContribucion = new double[N_O];
			if (totalcontributionArchiveCounter > 0)
				miContribucion[0] = (double) aux/ (double) totalcontributionArchiveCounter;
			for (int i = 1; i < N_O; i++) {
				aux = 0;
				for (int j = 0; j < windowsSize; j++) {
					aux += contributionArchiveCounter_[i][j];
				}
				if (totalcontributionArchiveCounter > 0)
					miContribucion[i] = (double) aux / (double) totalcontributionArchiveCounter;
			}

			for (int i = 0; i < N_O; i++) {
				if (miContribucion[i] < this.mincontribution) {
					miContribucion[i] = this.mincontribution;
				}
			}
			
			total = 0.0;
			for (int i = 0; i < N_O; i++) {
				total +=  miContribucion[i];
			}

			//System.out.println(total);

			if (total == 0.3) {
				//System.out.println("Hemos llegado a 0.3!!!!!");
				for (int i = 0; i < N_O; i++) {
					miContribucion[i] = 1.0 / (double) N_O;
				}
				total = 1.0;
			} else {
				for (int i = 0; i < N_O; i++) {
					miContribucion[i] = miContribucion[i] / total;
				}
			}

			contribution_[0] = miContribucion[0];
			//System.out.print(contribution_[0]+" ");
			for (int i = 1; i < N_O; i++) {
				contribution_[i] = contribution_[i - 1] + miContribucion[i];
				//System.out.print(((double) miContribucion[i])+" ");

			}
			//System.out.print(indicators.getHypervolume(archive));
			//System.out.println();


			//contrDE = miContribucion[0] ;
			//contrSBX = miContribucion[1] ;
			//contrPol = miContribucion[2] ;

			//Feedback!!!!
			//     int realimentacion = 0;//(int)(feedback * 0.01) * populationSize;
			//     for (int j = 0; j < realimentacion; j++){
			//         if (archive.size() > j){
			//           int r = PseudoRandom.randInt(0,currentPopulation.size()-1);
			//           if (r < currentPopulation.size()){
			//             Solution individual = archive.get(j);
			//             individual.setLocation(r);
			//             currentPopulation.replace(r,new Solution(individual));
			//           }
			//         }
			//       }
		/*	
      double HV = indicators.getHypervolume(archive);
      double epsilon = indicators.getEpsilon(archive);
      double IGD = indicators.getIGD(archive);
      double spread = indicators.getSpread(archive);
      
      //System.out.println("Evaluations: " + evaluations) ;
      contrTotalDE += contrDE ;
      contrTotalSBX += contrSBX;
      contrTotalPol += contrPol;
      
      jmetal.util.FileUtils.appendObjectToFile("HV.evol", HV) ;
      jmetal.util.FileUtils.appendObjectToFile("EPSILON.evol", epsilon) ;
      jmetal.util.FileUtils.appendObjectToFile("IGD.evol", IGD) ;
      jmetal.util.FileUtils.appendObjectToFile("SPREAD.evol", spread) ;
      jmetal.util.FileUtils.appendObjectToFile("cont0.evol", miContribucion[0]) ;
      jmetal.util.FileUtils.appendObjectToFile("cont1.evol", miContribucion[1]) ;
      jmetal.util.FileUtils.appendObjectToFile("cont2.evol", miContribucion[2]) ;
      jmetal.util.FileUtils.appendObjectToFile("de.evol", contrDE) ;
      jmetal.util.FileUtils.appendObjectToFile("sbx.evol", contrSBX) ;
      jmetal.util.FileUtils.appendObjectToFile("pol.evol", contrPol) ;
      jmetal.util.FileUtils.appendObjectToFile("cont0.real.evol", contrReal[0]) ;
      jmetal.util.FileUtils.appendObjectToFile("cont1.real.evol", contrReal[1]) ;
      jmetal.util.FileUtils.appendObjectToFile("cont2.real.evol", contrReal[2]) ;
      */
      contrDE =0;
      contrSBX=0;
      contrPol=0;
		}
		/*
    FileUtils.createEmtpyFile("contTotalDE") ;
    FileUtils.createEmtpyFile("contTotalSBX") ;
    FileUtils.createEmtpyFile("contTotalPol") ;
    jmetal.util.FileUtils.appendObjectToFile("contTotalDE", contrTotalDE) ;
    jmetal.util.FileUtils.appendObjectToFile("contTotalSBX", contrTotalSBX) ;
    jmetal.util.FileUtils.appendObjectToFile("contTotalPol", contrTotalPol) ;

		
    jmetal.util.FileUtils.appendObjectToFile("HV", indicators.getHypervolume(archive)) ;
    jmetal.util.FileUtils.appendObjectToFile("SPREAD", indicators.getSpread(archive)) ;
    jmetal.util.FileUtils.appendObjectToFile("EPSILON", indicators.getEpsilon(archive)) ;
    jmetal.util.FileUtils.appendObjectToFile("IGD", indicators.getIGD(archive)) ;
*/
		//System.out.println(evaluations);
		currentPopulation.printObjectivesToFile("malla");
		//todas.printObjectivesToFile("todas");
		//todas.printVariablesToFile("todas.var");
		//System.out.println(populationSize);
		return archive;
	}
} // CellDE2
