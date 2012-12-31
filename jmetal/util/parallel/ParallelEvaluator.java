//  ParallelEvaluator.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
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

package jmetal.util.parallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.util.JMException;

public class ParallelEvaluator {
	private int numberOfCores_ ;
	private ExecutorService executor_ ;
	private Collection<Callable<Solution>> taskList_ ;

	/**
	 * 
	 * @author antelverde
	 *
	 */
	private class EvaluationTask implements Callable<Solution> {
		private Problem problem_ ;
		private Solution solution_ ;

		public EvaluationTask(Problem problem,  Solution solution) {
			problem_ = problem ;
			solution_ = solution ;
		}

		public Solution call() throws Exception {
			long initTime = System.currentTimeMillis();
			problem_.evaluate(solution_) ;
			problem_.evaluateConstraints(solution_) ;
			long estimatedTime = System.currentTimeMillis() - initTime;
			//System.out.println("Time: "+ estimatedTime) ;
			return solution_ ;
		} 
	}

	Collection<Callable<Solution>> tasks = new ArrayList<Callable<Solution>>();

	public ParallelEvaluator() {
		numberOfCores_ = Runtime.getRuntime().availableProcessors() ;
		executor_ = Executors.newFixedThreadPool(numberOfCores_) ;
		System.out.println("Cores: "+ numberOfCores_) ;
		taskList_ = null ; 
	}

	public ParallelEvaluator(int cores) {
		numberOfCores_ = cores ;
		executor_ = Executors.newFixedThreadPool(numberOfCores_) ;
		System.out.println("Cores: "+ numberOfCores_) ;
		taskList_ = null ; 
	}

	public void addSolutionForEvaluation(Problem problem, Solution solution) {
		if (taskList_ == null)
			taskList_ = new ArrayList<Callable<Solution>>();
			
		taskList_.add(new EvaluationTask(problem, solution)) ;			
	}

	public List<Solution> parallelEvaluation() {
		List<Future<Solution>> future = null ;
		try {
			future = executor_.invokeAll(taskList_);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<Solution> solutionList = new Vector<Solution>() ;

		for(Future<Solution> result : future){
			Solution solution = null ;
			try {
				solution = result.get();
				solutionList.add(solution) ;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 			evaluations++;
			// 			population.add(solution);
		}
		taskList_ = null ;
		return solutionList ;
	}

	public void shutdown() {
		executor_.shutdown() ;
	}

/*	
	
	
	public List<Future<Solution>> evaluate(Collection<Callable<Solution>> tasks) {
		List<Future<Solution>> solutions = null ;
		try {
			solutions = executor_.invokeAll(tasks);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return solutions ;
	}

	public Future<Solution> evaluate(final Problem problem, final Solution solution) {
		Future<Solution> future = null ;
		future = executor_.submit(new Callable<Solution>() {
			public Solution call() {
				//Solution sol = new Solution(solution) ;
				try {
					problem.evaluate(solution) ;
				} catch (JMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return solution ;
			}});

		return future ;
	}


	public int getNumberOfCores() {
		return numberOfCores_ ;
	}
	public void setNumberOfCores(int cores) {
		numberOfCores_ = cores ;
		idleCores_ = numberOfCores_ ;
	}   
	*/
}
