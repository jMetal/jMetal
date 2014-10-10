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

package org.uma.test.metaheuristic.multiobjective.nsgaII;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAIITemplate;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Antonio J. Nebro on 05/06/14.
 */
public class NSGAIITemplateTest extends NSGAIITemplate {
  NSGAIITemplateTest algorithm;
  SolutionSetEvaluator evaluator;

  public NSGAIITemplateTest() {
    super(new SequentialSolutionSetEvaluator()) ;
    evaluator = new SequentialSolutionSetEvaluator() ;
  }

  @Before
  public void startup(){
    algorithm = new NSGAIITemplateTest() ;
    algorithm.setProblem(new Kursawe("Real", 3));
  }

  @Test
  public void createInitialPopulationTest() throws ClassNotFoundException {
    algorithm.populationSize = 100;

    algorithm.createInitialPopulation();
    assertEquals(100, algorithm.population.size()) ;
  }

  @Test
  public void stoppingConditionTest() {
    algorithm.evaluations = 125 ;
    algorithm.maxEvaluations = 125 ;

    assertTrue(algorithm.stoppingCondition());

    algorithm.evaluations = 124 ;
    assertFalse(algorithm.stoppingCondition());

    algorithm.evaluations = 126 ;
    assertTrue(algorithm.stoppingCondition());
  }

  @Test
  public void populationIsNotFullTest() throws ClassNotFoundException {
    algorithm.populationSize = 25 ;
    algorithm.createInitialPopulation() ;

    assertFalse(algorithm.populationIsNotFull()) ;

    algorithm.population = new SolutionSet(20) ;
    assertTrue(algorithm.populationIsNotFull()) ;
  }

  @After
  public void tearDown(){
    algorithm = null ;
  }


  @Override public SolutionSet execute() throws JMetalException, ClassNotFoundException, IOException {
    return null;
  }
}
