# jMetal Development Site

**jMetal** is an object-oriented Java-based framework for multi-objective optimization with metaheuristics
(http://jmetal.sourceforge.net).

The current jMetal development version is hosted in this repository; this way, interested users can take a look to
the new incoming features in advance.

Suggestions and comments are welcome.

After eight years since the first release of jMetal, we have decided it's time to make a deep redesign of the
software. Some of the ideas we are elaborating are:
1. Maven is used as the tool for development, testing, packaging and deployment.
2. The encoding takes into account the recommendations provided in “Clean code: A Handbook of Agile Software Craftsmanship" (Robert C. Martin)
3. The Fluent Interface (http://martinfowler.com/bliki/FluentInterface.html) is applied intensively.
4. We will incorporate progressively unit tests to all the clases.

## Clean code
After applying clean code, the main code of NSGA-II is now as follows:
```
   public class NSGAII extends NSGAIITemplate {

     /**
      * Runs the NSGA-II algorithm.
      *
      * @return a <code>SolutionSet</code> that is a set of non dominated solutions
      * as a result of the algorithm execution
      * @throws jmetal.util.JMetalException
      */
     public SolutionSet execute() throws JMetalException, ClassNotFoundException {
       //readParameterSettings();
       createInitialPopulation();
       evaluatePopulation(population_);

       // Main loop
       while (!stoppingCondition()) {
         offspringPopulation_ = new SolutionSet(populationSize_);
         for (int i = 0; i < (populationSize_ / 2); i++) {
           if (!stoppingCondition()) {
             Solution[] parents = new Solution[2];
             parents[0] = (Solution) selectionOperator_.execute(population_);
             parents[1] = (Solution) selectionOperator_.execute(population_);

             Solution[] offSpring = (Solution[]) crossoverOperator_.execute(parents);

             mutationOperator_.execute(offSpring[0]);
             mutationOperator_.execute(offSpring[1]);

             offspringPopulation_.add(offSpring[0]);
             offspringPopulation_.add(offSpring[1]);
           }
         }

         evaluatePopulation(offspringPopulation_);
         evaluations_ += offspringPopulation_.size() ;

         Ranking ranking = new Ranking(population_.union(offspringPopulation_));

         population_.clear();
         int rankingIndex = 0 ;
         while (populationIsNotFull()) {
           if (subfrontFillsIntoThePopulation(ranking, rankingIndex)) {
             addRankedSolutionsToPopulation(ranking, rankingIndex);
             rankingIndex ++ ;
           } else {
             computeCrowdingDistance(ranking, rankingIndex) ;
             addLastRankedSolutions(ranking, rankingIndex);
           }
         }
       }

       tearDown() ;

       return getNonDominatedSolutions() ;
     }
   }
```




