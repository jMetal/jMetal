package jmetal.metaheuristics.aco;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.variable.Permutation;

public class BTSP extends Problem{
	
	public int numCities;
	public double [][] distMatrix;
	public int[]       bestTrail ;
	
	 public BTSP(String solutionType, String kroA100, String kroB100) throws IOException {
		    numberOfVariables_  = 2;
		    numberOfObjectives_ = 2;
		    numberOfConstraints_= 0;
		    problemName_        = "BTSP";
		    
		    length_ = new int[numberOfVariables_];
		    distMatrix 	= readProblem(kroA100, kroB100) ;
		    //printMatrix();
		   // departureTown = 0;
		    
		    if (solutionType.compareTo("Permutation") == 0)
		    	solutionType_ = new PermutationSolutionType(this) ;
		    else {
		    	System.out.println("Error: solution type " + solutionType + " invalid") ;
		    	System.exit(-1) ;
		    }

		    
}

	 private StreamTokenizer token1;
	 private StreamTokenizer token2;
   	 private int numCities1;
	 private int numCities2;
	 
	 public double [][] readProblem(String file1, String file2) throws
     IOException {
		double [][] matrix = null;
		Reader inputFile1 = new BufferedReader(
		                  new InputStreamReader(
		                  new FileInputStream(file1)));
		
		Reader inputFile2 = new BufferedReader(
		                  new InputStreamReader(
		                  new FileInputStream(file2)));
		
		token1 = new StreamTokenizer(inputFile1);
		token2 = new StreamTokenizer(inputFile2);

		try {
			//Reading header from file
			numCities1 = countCities(token1);
			numCities2 = countCities(token2);
			numCities = numCities1 + numCities2;
			//numCities = readAmountOfCities();
			matrix = new double[numCities][numCities] ;
			
			//Moving to the Data section of the file
			findData(token1);
			findData(token2);
		   // findStringSection();
		
		   // Read the data
		   double[] c = readDataFromFiles();		   
		
		   double dist ;
		   for (int k = 0; k < numCities; k++) {
			     matrix[k][k] = 0;
			     for (int j = k + 1; j < numCities; j++) {
			     	dist = Math.sqrt(Math.pow((c[k*2]-c[j*2]),2.0) +
			              Math.pow((c[k*2+1]-c[j*2+1]), 2));
			     	dist = (int)(dist + .5);
			     	matrix[k][j] = dist;
			     	matrix[j][k] = dist;
			     } // for
		   } // for
		} // try
		catch (Exception e) { 
		     System.err.println ("TSP.readProblem(): error when reading data file "+e);
		     System.exit(1);
		} // catch
		return matrix;
		} // readProblem
	 
		 
	 private double[] readDataFromFiles() throws IOException {
			double [] c = new double[2*numCities] ;
			StreamTokenizer token = token1;
			for (int i = 0; i < numCities1; i++) {
			    token.nextToken() ;
			    int j = (int)token.nval ;

			    token.nextToken() ;
			    c[2*(j-1)] = token.nval ;
			    token.nextToken() ;
			    c[2*(j-1)+1] = token.nval ;
			} // for
			token = token2;
			for (int i = numCities1; i < numCities; i++) {
			    token.nextToken() ;
			    int j = (int)token.nval + numCities1;

			    token.nextToken() ;
			    c[2*(j-1)] = token.nval ;
			    token.nextToken() ;
			    c[2*(j-1)+1] = token.nval ;
			} // for
			return c;
		}
	 
	 	private void findData(StreamTokenizer tokenCities) throws IOException{
	 		boolean found = false;
	 		tokenCities.nextToken();
	 		while(!found) {
		        if ((tokenCities.sval != null) &&
		           ((tokenCities.sval.compareTo("SECTION") == 0)))
		          found = true ;
		        else
		        	tokenCities.nextToken() ;
		      } // while	 		
	 	}

		
		
		private int countCities(StreamTokenizer tokenCities) throws IOException
		{
			boolean found = false;
			tokenCities.nextToken();
			int count = 0;
			
			while(!found) {
				if ((tokenCities.sval != null) && ((tokenCities.sval.compareTo("DIMENSION") == 0)))
				  found = true ;
				else
					tokenCities.nextToken() ;
	     } // while
			
			tokenCities.nextToken() ;
			tokenCities.nextToken() ;
			     
			count = (int)tokenCities.nval;	
			return count;			
		}
				
		
		
		 public void evaluate(Solution solution) {
			    double fitness1   ;
			    double fitness2   ;
			    
			    fitness1   = 0.0 ;
			    fitness2   = 0.0 ;
			    
			    //Separate the matrix into the two matrices corresponding to each file.
			    int indexWay1 = 0, indexWay2 = 0;
			    int[] matrixWay1 = new int[numCities1];
			    int[] matrixWay2 = new int[numCities2];
			    for (int i = 0; i < numCities; i++) {
			    	if(((Permutation)solution.getDecisionVariables()[0]).vector_[i] <  numCities1) {
			    		matrixWay1[indexWay1] = ((Permutation)solution.getDecisionVariables()[0]).vector_[i];
			    		indexWay1++;
			    	} else {
			    		matrixWay2[indexWay2] = ((Permutation)solution.getDecisionVariables()[0]).vector_[i];
			    		indexWay2++;
			    	}
			    } // for
			    
			    fitness1 = calculateFitness(matrixWay1,numCities1);
			    fitness2 = calculateFitness(matrixWay2,numCities2);
			    
			    
			    fitness1 += distMatrix[matrixWay1[0]][matrixWay1[numCities1 - 1]] ;
			    fitness2 += distMatrix[matrixWay2[0]][matrixWay2[numCities2 - 1]] ;
			    System.out.println("Achieved fitnesses: 1 - " + fitness1 + " || 2 - " + fitness2);
			    
			    solution.setObjective(0, fitness1);            
			    solution.setObjective(1, fitness2);
			  } // evaluate


			  private double calculateFitness(int[] matrix, int size) {
				// TODO Auto-generated method stub
				  double fitness = 0.0;
				  for(int i = 0;i < (size-1);i++) {
					  fitness += distMatrix[matrix[i]][matrix[i+1]];
				  }
				  return fitness;
			}
}