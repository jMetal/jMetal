//  Threads.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2012 Antonio J. Nebro
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

package jmetal.util.threads;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.util.JMException;

public class Threads {
  private int numberOfCores_ ;
  private int idleCores_ ;
  private ExecutorService executor_ ;
   
   public Threads() {
  	 numberOfCores_ = Runtime.getRuntime().availableProcessors() ;
  	 idleCores_ = numberOfCores_ ;
  	 executor_ = Executors.newFixedThreadPool(numberOfCores_) ;
  	 System.out.println("Cores: "+ numberOfCores_) ;
   }
   
   public Threads(int cores) {
  	 numberOfCores_ = cores ;
  	 idleCores_ = numberOfCores_ ;
  	 executor_ = Executors.newFixedThreadPool(numberOfCores_) ;
  	 System.out.println("Cores: "+ numberOfCores_) ;
   }

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
    		 Solution sol = new Solution(solution) ;
      	 try {
	        problem.evaluate(sol) ;
        } catch (JMException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
        return sol ;
     }});
  	 
  	 return future ;
   }
   
   public void shutdown() {
  	 executor_.shutdown() ;
   }
   
   /**
    * Returns the number of cores of the computer
    * @return the number of cores of the computer
    */
   public int getNumberOfCores() {
  	 return numberOfCores_ ;
   }

   /**
    * Sets the number of cores
    */
   public void setNumberOfCores(int cores) {
  	 numberOfCores_ = cores ;
  	 idleCores_ = numberOfCores_ ;
   }

   /**
    * Returns the number of idle cores 
    * @return the number of idle cores
    */
   public int getNumberOfIdleCores() {
  	 return idleCores_ ;
   }
   
   /**
    * Returns true if there are idle cores
    * @return true if there are idle cores; false otherwise
    */
   public boolean idleCores() {
  	 return idleCores_ > 0 ;
   }
}
