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

package org.uma.jmetal.algorithm.multiobjective.cdg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

/**
 * Abstract class for implementing versions of the CDG algorithm.
 *
 * @author  Feng Zhang
 * @version 1.0
 */
@SuppressWarnings("serial")
public abstract class AbstractCDG<S extends Solution<?>> implements Algorithm<List<S>> {
	
	protected enum NeighborType {
		NEIGHBOR, POPULATION
	}
	
	protected Problem<S> problem;

	/** Z vector in Zhang & Li paper */
	protected double[] idealPoint;
	
	// nadir point
	protected double[] nadirPoint;
	
	protected int[][] neighborhood;
	protected int[] neighborhoodNum;
	
	protected int childGrid_;
	protected int childGridNum_;
	protected int[][] subP;
	protected int[] subPNum;
	protected List<List<Integer>> team = new ArrayList<>();

	
	/** Delta in Zhang & Li paper */
	protected double neighborhoodSelectionProbability;
	
	protected double[] d_;
	protected double sigma_;
	protected int k_;
	protected int t_;
	protected int subproblemNum_;
	
	protected int borderLength;
	
	protected int slimDetal_;
	protected int badSolutionNum;
	protected int[] badSolution;
	protected int[] gridDetal_;
	protected double[][] gridDetalSum_; 

	protected List<S> population;
	protected List<S> badPopulation;
	
	protected List<S> specialPopulation;
	protected List<Integer> spPopulationOrder;
	
	protected List<List<S>> subproblem = new ArrayList<>();
	
	protected List<List<S>> tempBorder = new ArrayList<>();
	
	protected List<List<S>> border = new ArrayList<>();

	protected int populationSize;
	protected int resultPopulationSize;

	protected int evaluations;
	protected int maxEvaluations;
	
	protected JMetalRandom randomGenerator;

	protected CrossoverOperator<S> crossoverOperator;

	public AbstractCDG(Problem<S> problem, int populationSize, int resultPopulationSize, int maxEvaluations,
			CrossoverOperator<S> crossoverOperator,double neighborhoodSelectionProbability, 
			double sigma_, int k_, int t_, int subproblemNum_, int childGrid_, int childGridNum_) {
		this.problem = problem;
		this.populationSize = populationSize;
		this.resultPopulationSize = resultPopulationSize;
		this.maxEvaluations = maxEvaluations;
		this.crossoverOperator = crossoverOperator;
		this.neighborhoodSelectionProbability = neighborhoodSelectionProbability;
		this.sigma_ = sigma_;
		this.k_ = k_;
		this.t_ = t_;
		this.subproblemNum_ = subproblemNum_;
		this.childGrid_ = childGrid_;
		this.childGridNum_ = childGridNum_;
		
		randomGenerator = JMetalRandom.getInstance();

		population = new ArrayList<>(populationSize);
		
		badPopulation = new ArrayList<>(2 * populationSize);
		
		specialPopulation = new ArrayList<>(2 * populationSize);
		spPopulationOrder = new ArrayList<>(2 * populationSize); 

		neighborhood = new int[populationSize][populationSize];
		neighborhoodNum = new int[populationSize];
		
		idealPoint = new double[problem.getNumberOfObjectives()];
		nadirPoint = new double[problem.getNumberOfObjectives()];

		d_ = new double[problem.getNumberOfObjectives()];
		
		subproblem = new ArrayList<>(subproblemNum_);

		for(int i = 0;i < subproblemNum_;i++){
			List<S> list = new ArrayList<>(populationSize);
			subproblem.add(list);
		}

		int subPLength = (int) Math.pow(3, problem.getNumberOfObjectives());
		subP = new int[childGridNum_][subPLength];
		subPNum = new int[childGridNum_];
		team = new ArrayList<>(childGridNum_);
		for(int i = 0;i < childGridNum_;i++){
			List<Integer> list = new ArrayList<>(populationSize);
			team.add(list);
		}

		slimDetal_ = k_ - 3;
		badSolution = new int[2 * populationSize];
		gridDetal_ = new int[k_];
		gridDetalSum_ = new double[problem.getNumberOfObjectives()][k_]; 
		
		tempBorder = new ArrayList<>(problem.getNumberOfObjectives());
		border = new ArrayList<>(problem.getNumberOfObjectives());
		borderLength = 2 * populationSize * (problem.getNumberOfObjectives() - 1);
		for(int i = 0;i < problem.getNumberOfObjectives();i++){
			List<S> list = new ArrayList<>(borderLength);
			tempBorder.add(list);
		}
		for(int i = 0;i < problem.getNumberOfObjectives();i++){
			List<S> list = new ArrayList<>(borderLength);
			border.add(list);
		}
		
	}

	protected void initialCDGAttributes(S individual){
	    int[] g_ = new int[problem.getNumberOfObjectives()] ; 
	    int[] rank_ = new int[problem.getNumberOfObjectives()] ; 	
	    for(int i = 0;i < problem.getNumberOfObjectives();i++){
	    	g_[i] = 0;
	    	rank_[i] = 0;
	    }
		int order_ = 0;
		
		individual.setAttribute("g_", g_);
		individual.setAttribute("rank_", rank_);
		individual.setAttribute("order_", order_);
	}
	
	protected int getG(S individual,int index){
		int[] g_ = (int [])individual.getAttribute("g_");
		return g_[index];
	}
	
	protected int getRank(S individual,int index){
		int[] rank_ = (int [])individual.getAttribute("rank_");
		return rank_[index];
	}
	
	protected int getOrder(S individual){
		int order_ = (int) individual.getAttribute("order_");
		return order_;
	}
	
	protected void setG(S individual,int index,int value){
		int[] g_ = (int [])individual.getAttribute("g_");
		g_[index] = value;
		individual.setAttribute("g_", g_);
	}
	
	protected void setRank(S individual,int index,int value){
		int[] rank_ = (int [])individual.getAttribute("rank_");
		rank_[index] = value;
		individual.setAttribute("rank_", rank_);
	}
	
	protected void setOrder(S individual,int value){
		int order_ = (int)individual.getAttribute("order_");
		order_ = value;
		individual.setAttribute("order_", order_);
	}
	
	protected void updateNeighborhood(){
		if(problem.getNumberOfObjectives() == 2){
			initializeSubP2();
			group2();
			initializeNeighborhoodGrid();
		}
		else if(problem.getNumberOfObjectives() == 3){
			initializeSubP3();
			group3();
			initializeNeighborhoodGrid();
		}
		else{
			initializeNeighborhood();
		}
	}
	
	/**
	 * Initialize cdg neighborhoods
	 */
	protected void initializeNeighborhood() {

		for(int i = 0;i < populationSize;i++){
			neighborhoodNum[i] = 0;
		}

		for(int i = 0;i < populationSize;i++)
			for(int j = 0;j < populationSize;j++){
				int gridDistance = 0;
				for(int k = 0;k < problem.getNumberOfObjectives();k++){
					int g1 = getG(population.get(i),k); 
					int g2 = getG(population.get(j),k);
					
					int tempGridDistance = Math.abs(g1 - g2);
					if(tempGridDistance > gridDistance)
						gridDistance = tempGridDistance;
				}
				if(gridDistance < t_){
					neighborhood[i][neighborhoodNum[i]] = j;
					neighborhoodNum[i]++;
				}
			}
				
	}
	
	protected void initializeSubP2(){
		int[] left = new int[problem.getNumberOfObjectives()]; 
		int[] right = new int[problem.getNumberOfObjectives()];
		int s = 0;
		int ns = 0;
		
		for(int i = 1;i < childGridNum_;i++)
			subPNum[i] = 0;
		
		for(int i = 1;i <= childGrid_;i++)
			for(int j = 1;j <= childGrid_;j++){
				
				s = getPos(i,j,1);

				left[0] = i - t_;
				right[0] = i + t_;
				left[1] = j - t_;
				right[1] = j + t_;
				
				for(int d = 0;d < problem.getNumberOfObjectives();d++){
					if(left[d] < 1){
						left[d] = 1;
						right[d] = 2 * t_ + 1;
					}
					if(right[d] > childGrid_){
						right[d] = childGrid_;
						left[d] = childGrid_ - 2 * t_;
					}
				}
				for(int ni = left[0];ni <= right[0];ni++)
					for(int nj = left[1];nj <= right[1];nj++){

						ns = getPos(ni,nj,1);
						
						subP[s][subPNum[s]] = ns;
						subPNum[s]++;
					}
				
			}
	}
	
	protected void initializeSubP3(){
		int[] left = new int[problem.getNumberOfObjectives()]; 
		int[] right = new int[problem.getNumberOfObjectives()];
		int s = 0;
		int ns = 0;
		
		for(int i = 1;i < childGridNum_;i++)
			subPNum[i] = 0;
		
		for(int i = 1;i <= childGrid_;i++)
			for(int j = 1;j <= childGrid_;j++){
				for(int k = 1;k <= childGrid_;k++){

					s = getPos(i,j,k);
				
					left[0] = i - t_;
					right[0] = i + t_;
					left[1] = j - t_;
					right[1] = j + t_;
					left[2] = k - t_;
					right[2] = k + t_;
				
					for(int d = 0;d < problem.getNumberOfObjectives();d++){
						if(left[d] < 1){
							left[d] = 1;
							right[d] = 2 * t_ + 1;
						}
						if(right[d] > childGrid_){
							right[d] = childGrid_;
							left[d] = childGrid_ - 2 * t_;
						}
					}
					for(int ni = left[0];ni <= right[0];ni++)
						for(int nj = left[1];nj <= right[1];nj++)
							for(int nk = left[2];nk <= right[2];nk++){
								ns = getPos(ni,nj,nk);
								subP[s][subPNum[s]] = ns;
								subPNum[s]++;
							}
				}
			}
	}
	
	protected int getPos(int i, int j, int k){
		int s = 0;
		int l = (int) Math.pow(childGrid_, 2);
		l = l * (k - 1);
		if(i >= j){
			s = (i - 1) * (i - 1) + j;
		}
		else{
			s = j * j - i + 1;
		}
		s = s + l;
		return s;
	}
	
	protected void group2(){
		double[] childDelta = new double[problem.getNumberOfObjectives()];
		double[] maxFunValue = new double[problem.getNumberOfObjectives()];
		
		for(int i = 0;i < problem.getNumberOfObjectives();i++)
			maxFunValue[i] = 0;
		
		for(int i = 0;i < population.size();i++){
			for(int j = 0;j < problem.getNumberOfObjectives();j++){
				if(population.get(i).getObjective(j) > maxFunValue[j])
					maxFunValue[j] = population.get(i).getObjective(j);
			}
		}
		
		for(int i = 0;i < problem.getNumberOfObjectives();i++){
			childDelta[i] = (maxFunValue[i] - idealPoint[i]) / childGrid_;
		}
		
		for(int i = 1;i < childGridNum_;i++)
			team.get(i).clear();
		
		int grid;
		double childSigma = 1e-10;
		int[] pos = new int[problem.getNumberOfObjectives()];
		
		for(int i = 0;i < populationSize;i++){
			for(int j = 0;j < problem.getNumberOfObjectives();j++){
				double nornalObj = population.get(i).getObjective(j) - idealPoint[j];
				pos[j] = (int) Math.ceil(nornalObj / childDelta[j] - childSigma);
			}
			grid = 0;
			for(int j = 0;j < problem.getNumberOfObjectives();j++)
				if(pos[j] < 1)
					pos[j] = 1;

			grid = getPos(pos[0],pos[1],1);
			
			if(grid < 1)
				grid = 1;
			if(grid > childGridNum_ - 1)
				grid = childGridNum_ - 1;
			team.get(grid).add(i);
		}
	}
	
	protected void group3(){
		double[] childDelta = new double[problem.getNumberOfObjectives()];
		double[] maxFunValue = new double[problem.getNumberOfObjectives()];
		
		for(int i = 0;i < problem.getNumberOfObjectives();i++)
			maxFunValue[i] = 0;
		
		for(int i = 0;i < population.size();i++){
			for(int j = 0;j < problem.getNumberOfObjectives();j++){
				if(population.get(i).getObjective(j) > maxFunValue[j])
					maxFunValue[j] = population.get(i).getObjective(j);
			}
		}
		
		for(int i = 0;i < problem.getNumberOfObjectives();i++){
			childDelta[i] = (maxFunValue[i] - idealPoint[i]) / childGrid_;
		}
		
		for(int i = 1;i < childGridNum_;i++)
			team.get(i).clear();
		
		int grid;
		double childSigma = 1e-10;
		int[] pos = new int[problem.getNumberOfObjectives()];
		
		for(int i = 0;i < populationSize;i++){
			for(int j = 0;j < problem.getNumberOfObjectives();j++){
				double nornalObj = population.get(i).getObjective(j) - idealPoint[j];
				pos[j] = (int) Math.ceil(nornalObj / childDelta[j] - childSigma);
			}
			grid = 0;
			for(int j = 0;j < problem.getNumberOfObjectives();j++)
				if(pos[j] < 1)
					pos[j] = 1;
			
			grid = getPos(pos[0],pos[1],pos[2]);
			
			if(grid < 1)
				grid = 1;
			if(grid > childGridNum_ - 1)
				grid = childGridNum_ - 1;
			team.get(grid).add(i);
		}
	}
	
	protected void initializeNeighborhoodGrid(){
		
		for(int i = 0;i < populationSize;i++){
			neighborhoodNum[i] = 0;
		}

		for(int i = 1;i < childGridNum_;i++){
			for(int j = 0;j < team.get(i).size();j++){
				int parentIndex = team.get(i).get(j);
				if(subP.length < i || subPNum[i] == 0){
					neighborhoodNum[parentIndex] = 0;
				}
				else{
					for(int ni = 0;ni < subPNum[i];ni++){
						for(int nj = 0;nj < team.get(subP[i][ni]).size();nj++){
							neighborhood[parentIndex][neighborhoodNum[parentIndex]] = team.get(subP[i][ni]).get(nj);
							neighborhoodNum[parentIndex]++;
						}
					}
				}
			}
		}
	}

	protected void updateBorder(){
		getBorder();

		paretoFilter();

		double coefficient = 1 + (1 - evaluations / maxEvaluations) * 0.15;	
		double borderCoef = 1 + (coefficient - 1) / 4;	
		for(int i = 0;i < problem.getNumberOfObjectives();i++)
			for(int j = 0;j < border.get(i).size();j++)
				for(int k = 0;k < problem.getNumberOfObjectives();k++)
					if(i != k){
						double funValue = border.get(i).get(j).getObjective(k);
						border.get(i).get(j).setObjective(k, funValue * borderCoef);
					}
		
	}
	
	protected void getBorder(){
		int[] flag = new int[problem.getNumberOfObjectives()]; 
		double[] minFunValue = new double[problem.getNumberOfObjectives()]; 
		
		for(int i = 0;i < problem.getNumberOfObjectives();i++){
			tempBorder.get(i).clear();
			minFunValue[i] = 1.0e+30;
		}
		
		for(int i = 0;i < population.size();i++)
			for(int j = 0;j < problem.getNumberOfObjectives();j++)
				if(population.get(i).getObjective(j) < minFunValue[j])
					minFunValue[j] = population.get(i).getObjective(j);
			
		int sum;
		int od;
		for(int i = 0;i < population.size();i++){
			sum = 0;
			for(int j = 0;j < problem.getNumberOfObjectives();j++){
				if(population.get(i).getObjective(j) < minFunValue[j] + nadirPoint[j] / 100)
					flag[j] = 1;
				else
					flag[j] = 0;
				sum = sum + flag[j];
			}
			if(sum == 1){
				od = 0;
				for(int j = 0;j < problem.getNumberOfObjectives();j++)
					if(flag[j] == 1){
						od = j;
						break;
					}
				tempBorder.get(od).add(population.get(i));
			}
		}
	}
	
	protected void paretoFilter(){
		for(int i = 0;i < problem.getNumberOfObjectives();i++)
			border.get(i).clear();
		
		boolean tag;
		int nmbOfObjs = problem.getNumberOfObjectives() - 1;
		int sum1,sum2;
		
		for(int i = 0;i < problem.getNumberOfObjectives();i++)
			for(int p = 0;p < tempBorder.get(i).size();p++){
				tag = false;
				for(int q = 0;q < tempBorder.get(i).size();q++){
					sum1 = 0;
					sum2 = 0;
					for(int j = 0;j < problem.getNumberOfObjectives();j++){
						if(i != j){
							if(tempBorder.get(i).get(p).getObjective(j) 
									<= tempBorder.get(i).get(q).getObjective(j))
								sum1++;
							if(tempBorder.get(i).get(p).getObjective(j) 
									< tempBorder.get(i).get(q).getObjective(j))
								sum2++;
						}
					}
					if(sum1 ==  nmbOfObjs && sum2 > 0){
						tag = true;
						break;
					}
				}
				if(tag){
					border.get(i).add(tempBorder.get(i).get(p));
				}
			}
					
	}
	
	protected void initializeIdealPoint() {
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			idealPoint[i] = 1.0e+30;
		}

		for (int i = 0; i < populationSize; i++) {
			updateIdealPoint(population.get(i));
		}
	}

	// initialize the nadir point
	protected void initializeNadirPoint() {
		for (int i = 0; i < problem.getNumberOfObjectives(); i++)
			nadirPoint[i] = -1.0e+30;
		updateNadirPoint();
	}

	// update the current nadir point
	void updateNadirPoint() {
	    Ranking<S> ranking = new DominanceRanking<S>();
	    ranking.computeRanking(population);
	    List<S> nondominatedPopulation = ranking.getSubfront(0);
	    
		for(int i = 0;i < nondominatedPopulation.size();i++){
			S individual = nondominatedPopulation.get(i);
			for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
				if (individual.getObjective(j) > nadirPoint[j]) {
					nadirPoint[j] = individual.getObjective(j);
				}
			}
		}
	}

	protected void updateIdealPoint(S individual) {
		for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
			if (individual.getObjective(n) < idealPoint[n]) {
				idealPoint[n] = individual.getObjective(n);
			}
		}
	}

	protected void gridSystemSetup(){
		double coefficient = 1 + (1 - evaluations / maxEvaluations) * 0.15;	
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			d_[i] = (nadirPoint[i] - idealPoint[i]) * coefficient / k_;
		}
		for (int i = 0; i < population.size(); i++){
			for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
				int g = (int) Math.ceil((population.get(i).getObjective(j) - idealPoint[j]) / d_[j]);
				if(g < 0)
					g = 0;
				if(g >= k_)
					g = k_ -1;
				setG(population.get(i),j,g);
			}
		}
	}
	
	protected void gridSystemSetup3(){
		initialGridDetal();
		for (int i = 0; i < population.size(); i++){
			for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
				int g = getGridPos(j,population.get(i).getObjective(j));
				setG(population.get(i),j,g);
			}
		}
	}
	
	protected void initialGridDetal(){
		int detalSum = 0;
		gridDetal_[0] = -1;
		for(int i = 0;i < problem.getNumberOfObjectives();i++)
			gridDetalSum_[i][0] = (double) gridDetal_[0];
		
		for(int i = 1;i < slimDetal_;i++){
			gridDetal_[i] = 2;
			detalSum = detalSum + gridDetal_[i];
			for(int j = 0;j < problem.getNumberOfObjectives();j++)
				gridDetalSum_[j][i] = (double) detalSum;
		}
		
		for(int i = slimDetal_;i < k_;i++){
			gridDetal_[i] = 1;
			detalSum = detalSum + gridDetal_[i];
			for(int j = 0;j < problem.getNumberOfObjectives();j++)
				gridDetalSum_[j][i] = (double) detalSum;
		}

		double coefficient = 1 + (1 - evaluations / maxEvaluations) * 0.15;	
		
		for (int i = 0; i < problem.getNumberOfObjectives(); i++)
			d_[i] = (nadirPoint[i] - idealPoint[i]) * coefficient / k_;
		
		for(int i = 0;i < k_;i++)
			for(int j = 0;j < problem.getNumberOfObjectives();j++)
				gridDetalSum_[j][i] = gridDetalSum_[j][i] / detalSum * (d_[j] * k_);

	}
	
	protected int getGridPos(int j,double funValue){
		int g = 0;
		for(int i = 0;i < k_;i++)
			if(funValue > gridDetalSum_[j][i])
				g++;
		if(g == k_)
			g = k_ - 1;
		return g;
	}
	
	protected void chooseSpecialPopulation(){
		spPopulationOrder.clear();
		specialPopulation.clear();
		
		Map<Integer, S> specialSolution =new HashMap<Integer, S>();
		
		for(int i = 0;i < problem.getNumberOfObjectives();i++){
			for(int j = 0;j < population.size();j++){
				if(population.get(j).getObjective(i) == idealPoint[i]){
					if(!specialSolution.containsKey(j)){
						specialSolution.put(j, population.get(j));
					}
				}
			}  
		}
		
		for(int i = 0;i < problem.getNumberOfObjectives();i++){
			for(int j = 0;j < population.size();j++){
				if(population.get(j).getObjective(i) == nadirPoint[i]){
					if(!specialSolution.containsKey(j)){
						specialSolution.put(j, population.get(j));
					}
				}
			}  
		}
		
		
		for (Integer j : specialSolution.keySet()) {
			specialPopulation.add(specialSolution.get(j));
			spPopulationOrder.add(j);
		}
		
		if(specialPopulation.size() > 2 * problem.getNumberOfObjectives()){
			for(int i = specialPopulation.size() - 1;i > (2 * problem.getNumberOfObjectives() - 1);i--){
				specialPopulation.remove(i);
				spPopulationOrder.remove(i);
			}
		}
		
	}
	
	protected void excludeBadSolution(){
		badPopulation.clear();
		int length = population.size();
		for(int i = length - 1;i >= 0;i--){
			S individual = population.get(i);
			for(int j = 0;j < problem.getNumberOfObjectives();j++){
				if(individual.getObjective(j) > nadirPoint[j]){
					badPopulation.add(individual);
					population.remove(i);
					break;
				}
			}
		}
	}
	
	protected void excludeBadSolution3(){
		badSolutionNum = 0;
		for(int i = 0;i < population.size();i++){
			S individual = population.get(i);
			if(individual.getObjective(0) > nadirPoint[0] ||
				individual.getObjective(1) > nadirPoint[1] ||
				individual.getObjective(2) > nadirPoint[2] ||
				!isInner(individual)){
				 	badSolution[badSolutionNum] = i;
					badSolutionNum++;
			}
		}

		if(population.size() - badSolutionNum < populationSize){
			excludeBadSolution();
		}
		else{
			for(int i = badSolutionNum - 1;i >= 0;i--){
				population.remove(badSolution[i]);
			}
		}
	}
	
	protected boolean isInner(S individual){
		boolean flag = true;
		for(int i = 0;i < problem.getNumberOfObjectives();i++){
			if(border.get(i).size() == 0){
				flag = false;
				break;
			}
			if(paretoDom(individual,i)){
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	protected boolean paretoDom(S individual, int i){
		boolean flag = false;
		int m = problem.getNumberOfObjectives() - 1;
		int sum1, sum2;
		for(int j = 0;j < border.get(i).size();j++){
			sum1 = 0;
			sum2 = 0;
			for(int k = 0;k < problem.getNumberOfObjectives();k++)
				if(k != i){
					if(border.get(i).get(j).getObjective(k) <= individual.getObjective(k))
						sum1++;
					if(border.get(i).get(j).getObjective(k) < individual.getObjective(k))
						sum2++;
				}
			if(sum1 == m && sum2 > 0){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	protected void supplyBadSolution(){
		Random rand = new Random();
		do{
			int i = rand.nextInt(badPopulation.size());
			population.add(badPopulation.get(i));
		}while(population.size() < populationSize);
	}
	
	protected void rankBasedSelection(){
		for(int i = 0;i < subproblemNum_;i++)
			subproblem.get(i).clear();
		
		allocateSolution();

		subproblemSortl();

		setIndividualObjRank();

		setSpIndividualRank();
		
		individualObjRankSort();

		lexicographicSort();

		chooseSolution();

	}
	
	protected void allocateSolution(){
		for(int i = 0;i < population.size();i++){
			setOrder(population.get(i),i);
			
			for(int j = 0;j < problem.getNumberOfObjectives();j++){
				int objBasedAddress = (int) Math.pow(k_, problem.getNumberOfObjectives() - 1);
				objBasedAddress = j * objBasedAddress;
				int objOffset = 0;
				int bitIndex = 0;
				int bitWeight = 0;
				for(int k = problem.getNumberOfObjectives() - 1;k >= 0 ;k--){
					if(j != k){
						bitWeight = (int) Math.pow(k_, bitIndex);
						int g = getG(population.get(i),k);
						objOffset = objOffset + g * bitWeight;
						bitIndex++;
					}
				}

				subproblem.get(objBasedAddress + objOffset).add(population.get(i));
				
			}
		}
	}
	
	protected void subproblemSortl(){
		for(int i = 0;i < subproblemNum_;i++){
			int perObjSubproblemNum = (int) Math.pow(k_, problem.getNumberOfObjectives() - 1);
			int objD = (int) (i / perObjSubproblemNum);
			
	        Collections.sort(subproblem.get(i),new Comparator<S>(){
				public int compare(S o1,S o2){
					double x = o1.getObjective(objD);
					double y = o2.getObjective(objD);
					return (x == y)?0:(y < x)?1:(-1);
				}
	        });
		}
	}
	
	protected void setIndividualObjRank(){
		for(int i = 0;i < subproblemNum_;i++){
			
			int perObjSubproblemNum = (int) Math.pow(k_, problem.getNumberOfObjectives() - 1);
			int objD = (int) (i / perObjSubproblemNum);
			
			for(int j = 0;j < subproblem.get(i).size();j++){
				int order = getOrder(subproblem.get(i).get(j));
				
				double objValue = subproblem.get(i).get(j).getObjective(objD);
				double firstValue = subproblem.get(i).get(0).getObjective(objD);
				
				int gridRank = (int) Math.ceil((objValue - firstValue) / d_[objD]);
				int objRank = Math.max(gridRank + 1, j + 1);
				setRank(population.get(order),objD,objRank);
				
			}
		}
	}
	
	protected void setSpIndividualRank(){
		for(int i = 0;i < spPopulationOrder.size();i++){
			for(int j = 0;j < problem.getNumberOfObjectives();j++){
				setRank(population.get(spPopulationOrder.get(i)),j,1000);
			}
		}
	}
	
	protected void individualObjRankSort(){
		for(int i = 0;i < population.size();i++){
			List<Integer> list = new ArrayList<Integer>();
			for(int j = 0;j < problem.getNumberOfObjectives();j++){
				int rank = getRank(population.get(i),j);
				list.add(rank);
			}
			
			Collections.sort(list,new Comparator<Integer>(){
				@Override
				public int compare(Integer x,Integer y){
					return (x == y)?0:(y < x)?1:(-1);
				}
			});
			
			for(int j = 0;j < problem.getNumberOfObjectives();j++){
				setRank(population.get(i),j,list.get(j));
			}
		}
	}
	
	protected void lexicographicSort(){
		Collections.sort(population,new Comparator<S>(){
			@Override
			public int compare(S o1,S o2){
				for(int i = 0;i < problem.getNumberOfObjectives();i++){
					int x = getRank(o1,i);
					int y = getRank(o2,i);
					if(y < x)
						return 1;
					if(x < y)
						return -1;
				}
				return 0;
			}
		});
	}
	
	protected void chooseSolution(){
		if(population.size() < populationSize){
			supplyBadSolution();
		}
		else{
			int length = population.size();
			for(int i = length - 1;i >= populationSize - specialPopulation.size();i--){
				population.remove(i);
			}
			for(int i = 0;i < specialPopulation.size();i++)
				population.add(specialPopulation.get(i));
		}
	}
	
	protected NeighborType chooseNeighborType(int i) {
		double rnd = randomGenerator.nextDouble();
		NeighborType neighborType;
		
		if (rnd < neighborhoodSelectionProbability && neighborhoodNum[i] > 2) {
			neighborType = NeighborType.NEIGHBOR;
		} else {
			neighborType = NeighborType.POPULATION;
		}
		return neighborType;
	}

	protected List<S> parentSelection(int subProblemId, NeighborType neighborType) {
		List<Integer> matingPool = matingSelection(subProblemId, 2, neighborType);

		List<S> parents = new ArrayList<>(3);

		parents.add(population.get(matingPool.get(0)));
		parents.add(population.get(matingPool.get(1)));
		parents.add(population.get(subProblemId));

		return parents;
	}

	/**
	 *
	 * @param subproblemId
	 *            the id of current subproblem
	 * @param neighbourType
	 *            neighbour type
	 */
	protected List<Integer> matingSelection(int subproblemId, int numberOfSolutionsToSelect,
			NeighborType neighbourType) {
		int neighbourSize;
		int selectedSolution;

		List<Integer> listOfSolutions = new ArrayList<>(numberOfSolutionsToSelect);

		neighbourSize = neighborhood[subproblemId].length;
		while (listOfSolutions.size() < numberOfSolutionsToSelect) {
			int random;
			if (neighbourType == NeighborType.NEIGHBOR) {
				neighbourSize = neighborhoodNum[subproblemId];
				random = randomGenerator.nextInt(0, neighbourSize - 1);
				selectedSolution = neighborhood[subproblemId][random];
			} else {
				selectedSolution = randomGenerator.nextInt(0, populationSize - 1);
			}
			boolean flag = true;
			for (Integer individualId : listOfSolutions) {
				if (individualId == selectedSolution) {
					flag = false;
					break;
				}
			}

			if (flag) {
				listOfSolutions.add(selectedSolution);
			}
		}

		return listOfSolutions;
	}

	@Override
	public List<S> getResult() {
		return population;
	}
	
	
}
