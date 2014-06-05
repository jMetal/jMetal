//  NSGAIITemplateTest.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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

package test.metaheuristics.nsgaII;

import jmetal.core.SolutionSet;
import jmetal.metaheuristics.nsgaIIb.NSGAIITemplate;
import jmetal.problems.Kursawe;
import jmetal.util.JMException;
import jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import jmetal.util.evaluator.SolutionSetEvaluator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Antonio J. Nebro on 05/06/14.
 */
public class NSGAIITemplateTest extends NSGAIITemplate {
  NSGAIITemplateTest algorithm_ ;
  SolutionSetEvaluator evaluator_ ;

  public NSGAIITemplateTest() {
    super(new SequentialSolutionSetEvaluator()) ;
    evaluator_ = new SequentialSolutionSetEvaluator() ;
  }

  @Before
  public void startup(){
    algorithm_ = new NSGAIITemplateTest() ;
    algorithm_.setProblem(new Kursawe("Real", 3));
  }

  @Test
  public void createInitialPopulationTest() throws ClassNotFoundException {
    int populationSize = 100 ;

    SolutionSet returnedSolutionSet = algorithm_.createInitialPopulation(populationSize);
    assertEquals(populationSize, returnedSolutionSet.size()) ;
  }

  @Test
  public void stoppingConditionTest() {
    algorithm_.evaluations_ = 125 ;
    algorithm_.maxEvaluations_ = 125 ;
    assertTrue(algorithm_.stoppingCondition());

    algorithm_.evaluations_ = 124 ;
    assertFalse(algorithm_.stoppingCondition());

    algorithm_.evaluations_ = 126 ;
    assertFalse(algorithm_.stoppingCondition());
  }

  @After
  public void tearDown(){
    algorithm_ = null ;
  }


  @Override public SolutionSet execute() throws JMException, ClassNotFoundException, IOException {
    return null;
  }
}
