package jmetal.metaheuristics.aco;

import jmetal.core.Problem;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.HashMap;

//import jmetal.metaheuristics.aco.CsvReader;


public class ACO_Main {
		
	public static void main(String [] args) throws  JMException,  SecurityException, 
    IOException,  ClassNotFoundException {
	
	Problem problem = new BTSP("Permutation", "kroA100.tsp","kroB100.tsp");
	
	HashMap parameters = new HashMap() ;
	parameters.put("alpha", 3) ;
	parameters.put("beta", 2) ;
	parameters.put("rho", 0.01) ;
	parameters.put("Q", 2.0) ;
	parameters.put("numAnts", 4) ;
	parameters.put("maxTime", 1000) ;	
	AntsColony antColony = new AntsColony(parameters);
	
	
	// initialize ants to random trails
	int[][] ants = antColony.InitAnts(antColony.numAnts, ((BTSP)problem).numCities); //ver problema con numero de hormigas
	
	// determine the best initial trail
	int[] bestTrail = antColony.BestTrail(ants, ((BTSP)problem).distMatrix);
	// the length of the best trail
	double bestLength = antColony.Length(bestTrail, ((BTSP)problem).distMatrix); 
	
	double[][] pheromones = antColony.initPheromones(((BTSP)problem).numCities);
	
	int time = 0;
	while (time < antColony.maxTime)
    {
		antColony.updateAnts(ants, pheromones, ((BTSP)problem).distMatrix);
        antColony.updatePheromones(pheromones, ants, ((BTSP)problem).distMatrix); 
        
        int[] currBestTrail = antColony.BestTrail(ants, ((BTSP)problem).distMatrix);
        double currBestLength = antColony.Length(currBestTrail, ((BTSP)problem).distMatrix);
        
        if (currBestLength < bestLength)
        {
          bestLength = currBestLength;
          bestTrail = currBestTrail;                   
        }
        ++time;
    }
	 
	 System.out.println("Best length " + bestLength);
	 System.out.println("Best trail ");
	 for(int i=0;i<bestTrail.length;i++)
		 System.out.print(" " + bestTrail[i]);	
	}
	
	/*public void readDataColony(String file)
	{		
		CsvReader reader = null;
		try {
			reader = new CsvReader(file);			
			/*alpha = Integer.parseInt(reader.get(0)) ;
			beta = Integer.parseInt(reader.get(1));
			rho = Double.valueOf(reader.get(2)).doubleValue();
			Q = Float.parseFloat(reader.get(3)) ;
			numAnts = Integer.parseInt(reader.get(4));
			maxTime = Integer.parseInt(reader.get(5));
			System.out.println(reader.getColumnCount());
			String al = reader.get(0);		
			System.out.println("al=" + al);						
					
			} catch (IOException e) {
			e.printStackTrace();
			} finally {
				reader.close();
			}
	}*/
	
}
