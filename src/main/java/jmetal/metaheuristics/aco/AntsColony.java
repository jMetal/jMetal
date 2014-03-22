package jmetal.metaheuristics.aco;

import java.io.IOException;
import java.util.HashMap;


public class AntsColony {
	
	public int alpha;		// influence of pheromone on direction
	public int beta;		// influence of adjacent node distance
	public double rho;		// pheromone decrease factor
	public double Q;			// pheromone increase factor
	
	public int numAnts;
	public int maxTime;

	
	public AntsColony(HashMap<String, Object> parameters){		
		//readDataColony("dataColony.csv");
		alpha = (Integer) parameters.get("alpha");
		beta = (Integer) parameters.get("beta");
		rho = (Double) parameters.get("rho");
		Q = (Double) parameters.get("Q");
		numAnts = (Integer) parameters.get("numAnts");
		maxTime = (Integer) parameters.get("maxTime");
		
	}
	

	public int[][] InitAnts(int numAnts, int numCities)throws IOException{
		int[][] antsI = new int[numAnts][];
		
		for(int k=0;k<numAnts;k++)
		{
			int start = (int)Math.random() * numCities; 
			antsI[k] = RamdomTrail(start,numCities);
		}
		return antsI;
	}
	
	public int[] RamdomTrail(int start, int numCities)
	{
		int[] trail = new int[numCities];
		for (int i=0;i<numCities;i++)
		{
			trail[i] = i;
		}
		
		for (int j=0;j<numCities;j++)
		{
			int r = j + (int)Math.random() * (numCities - j);
			int tmp = trail[r];
			trail[r] = trail[j];
			trail[j] = tmp;
		}
		
		int idx = IndexofTarget(trail,start);
		int temp = trail[0];
		trail[0] = trail[idx];
		trail[idx] = trail[temp];
		return trail;
	}
	
	
	
	private int IndexofTarget(int[] trail, int target) {
		// TODO Auto-generated method stub
		for(int i=0;i<trail.length;++i)
		{
			if (trail[i] == target)
				return i;
		}
		throw new IllegalArgumentException("Target not found in IndexOfTarget");
	}
	
	public double Length(int[] trail, double[][] dists) //total length of a trail
	{
		double result = 0.0;
		for(int i=0;i<trail.length-1;++i)
			result += distance(trail[i], trail[i+1], dists);
		return result;
	}
	
	public double[][] initPheromones(int numCities)
	{
		double[][] pheromones = new double[numCities][];
		for(int i=0;i<numCities;i++)
			pheromones[i] = new double[numCities];
		for(int i=0;i<pheromones.length;i++)
			for(int j=0;j < pheromones[i].length;j++)
				pheromones[i][j] = rho;
		return pheromones;
	}


	public void updateAnts(int[][] ants, double[][] pheromones, double[][] dists){
		int numCities = pheromones.length;
		for(int i=0;i<ants.length;i++)
		{
			int start = (int)Math.random() * numCities; 
			int[] newTrail = BuilTrail(i,start,pheromones,dists);
			ants[i] = newTrail;
		}
		
	}



	public int[] BuilTrail(int idx, int start, double[][] pheromonesL,
			double[][] dists) {
		// TODO Auto-generated method stub
		int numCities = pheromonesL.length;
		int[] trail = new int[numCities];
		boolean[] visited = new boolean[numCities];
		
		trail[0] = start;
		visited[start] = true;
		for(int i=0;i<numCities-1;++i)
		{
			int cityX = trail[i];
			int next = NextCity(idx, cityX, visited, pheromonesL, dists); 
			trail[i+1] = next;
			visited[next] = true;
		}
		
		return trail;
	}


	private int NextCity(int idx, int cityX, boolean[] visited,
			double[][] pheromonesL, double[][] dists) {
		// TODO Auto-generated method stub
		double[] probs = MoveProbs(idx,cityX,visited,pheromonesL,dists);
		
		double[] cumul = new double[probs.length + 1];
		for(int i=0;i<probs.length;++i)
			cumul[i+1] = cumul[i] + probs[i];
		
		double p = Math.random(); 
		
		for(int i=0;i<cumul.length-1;++i)
			if ((p>=cumul[i]) && (p<cumul[i+1]))		
				return i;
		throw new IllegalArgumentException("Failure to return valid city in NextCity");
	}


	private double[] MoveProbs(int idx, int cityX, boolean[] visited,
			double[][] pheromonesL, double[][] dists) {
		// TODO Auto-generated method stub
		int numCities = pheromonesL.length;
		double[] taueta = new double[numCities];
		double sum = 0.0;
		
		for(int i=0;i<taueta.length;++i)
		{
			if (i==cityX)
				taueta[i] = 0.0; //Prob of moving to self is zero
			else if (visited[i] == true)
				taueta[i] = 0.0; //Prob of moving to a visited node is zero
			else {
				taueta[i] = Math.pow(pheromonesL[cityX][i], alpha)*Math.pow(1.0/distance(cityX,i,dists), beta);
				if (taueta[i] < 0.0001)
					taueta[i] = 0.0001;
				else if (taueta[i] > (Double.MAX_VALUE/(numCities*100)))
					taueta[i] = Double.MAX_VALUE/(numCities*100);  
			}
			sum += taueta[i];			
		}
		
		double[] probs = new double[numCities];
		for(int i=0;i<probs.length;++i)
			probs[i] = taueta[i] / sum;
		
		return probs;
	}


	private double distance(int cityX, int cityY, double[][] dists) {
		// TODO Auto-generated method stub
		return dists[cityX][cityY];
	}


	public void updatePheromones(double[][] pheromonesL, int[][] antsL, double[][] dists){		
		for (int i = 0; i < pheromonesL.length; ++i)
	      {
	        for (int j = i + 1; j < pheromonesL[i].length; ++j)
	        {
	          for (int k = 0; k < antsL.length; ++k)
	          {
	        	  double length = Length(antsL[k], dists); // length of ant k trail
	        	  double decrease = (1.0 - rho) * pheromonesL[i][j];
	        	  double increase = 0.0;
	        	  if (EdgeInTrail(i, j, antsL[k]) == true) 
	        		  increase = (Q / length);
	        	  pheromonesL[i][j] = decrease + increase;
	        	  
	        	  if (pheromonesL[i][j] < 0.0001)
	                  pheromonesL[i][j] = 0.0001;
	                else if (pheromonesL[i][j] > 100000.0)
	                  pheromonesL[i][j] = 100000.0;
	        	  
	        	  pheromonesL[j][i] = pheromonesL[i][j];
	          }
	        }
	      }				
	}
	
	
	public int[] BestTrail(int[][] antsL, double[][] dists){
		// best trail has shortest total length
		double bestLength = Length(antsL[0], dists);
	      int idxBestLength = 0;
	      
	      for (int k = 1; k < antsL.length; ++k)
	      {
	        double len = Length(antsL[k], dists);
	        if (len < bestLength)
	        {
	          bestLength = len;
	          idxBestLength = k;
	        }
	      }
	      int numCities = antsL[0].length;
	      int[] bestTrail = new int[numCities];
	     
	      bestTrail = antsL[idxBestLength];
	      
	      return bestTrail;
	}

	private boolean EdgeInTrail(int cityX, int cityY, int[] trail) {		
		// are cityX and cityY adjacent to each other in trail[]?
	   int lastIndex = trail.length - 1;
	   int idx = IndexofTarget(trail, cityX);
	   if (idx == 0 && trail[1] == cityY) 
		   return true;
	   else if (idx == 0 && trail[lastIndex] == cityY) 
		   return true;
	      else if (idx == 0) 
	    	  return false;
	      else if (idx == lastIndex && trail[lastIndex - 1] == cityY) 
	    	  return true;
	      else if (idx == lastIndex && trail[0] == cityY) 
	    	  return true;
	      else if (idx == lastIndex) 
	    	  return false;
	      else if (trail[idx - 1] == cityY)
	    	  return true;
	      else if (trail[idx + 1] == cityY)
	    	  return true;
	      else 
	    	  return false;
	}






	
	
	
	

}
