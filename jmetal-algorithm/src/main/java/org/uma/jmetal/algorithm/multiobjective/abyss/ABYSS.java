package org.uma.jmetal.algorithm.multiobjective.abyss;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.impl.localsearch.MutationLocalSearch;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;


import java.util.Collections;
import java.util.List;

/**
 * Created by cbarba on 25/3/15.
 */
public class ABYSS extends AbstractABYSS<DoubleSolution> {
    public ABYSS(int numberOfSubranges,int solutionSetSize, int refSet1Size, int refSet2Size, int archiveSize, int maxEvaluations,
                 CrowdingDistanceArchive archive,     CrossoverOperator crossoverOperator,MutationLocalSearch improvement, DoubleProblem problem){
        super(numberOfSubranges,solutionSetSize,refSet1Size,refSet2Size,archiveSize,maxEvaluations,archive,crossoverOperator,improvement,problem);
    }
    /**
     * Runs of the AbYSS algorithm.
     * as a result of the algorithm execution
     */
    @Override
    public void run() {

try {

    DoubleSolution solution;
    // STEP 1. Build the initial solutionSet
    initialListSolution();

    // STEP 2. Main loop
    int newSolutions = 0;
    while (evaluations < maxEvaluations) {
        referenceSetUpdate(true);
        newSolutions = subSetGeneration();
        while (newSolutions > 0  ) { // New solutions are created
            referenceSetUpdate(false);
            if (evaluations < maxEvaluations) {
                newSolutions = subSetGeneration();
            }
            else {
                newSolutions = 0;
            }//if
        } // while

        // RE-START
        if (evaluations < maxEvaluations) {
            solutionSet.clear();
            // Add refSet1 to SolutionSet
            for (int i = 0; i < refSet1.size(); i++) {
                solution = refSet1.get(i);
                //solution.unMarked();
                marked.setAttribute(solution,false);
                solution = (DoubleSolution) improvementOperator.execute(solution);
                evaluations += improvementOperator.getEvaluations();
                solutionSet.add(solution);
            }
            // Remove refSet1 and refSet2
            refSet1.clear();
            refSet2.clear();

            // Sort the archive and insert the best solutions
            //CrowdingDistance<DoubleSolution> crowdingDistance = new CrowdingDistance();
            archive.computeDistance();
            Collections.sort(archive.getSolutionList(),crowdingDistanceComparator);

            int insert = solutionSetSize / 2;

           if (insert > archive.getSolutionList().size())
                insert = archive.getSolutionList().size();

            if (insert > (solutionSetSize - solutionSet.size()))
                insert = solutionSetSize - solutionSet.size();

            // Insert solutions
            for (int i = 0; i < insert; i++) {
                solution = archive.getSolutionList().get(i);
                marked.setAttribute(solution,false);
                solutionSet.add(solution);
            }
            // Create the rest of solutions randomly
            while (solutionSet.size() < solutionSetSize) {
                solution = diversificationGeneration();
                if(problem instanceof ConstrainedProblem){
                    ((ConstrainedProblem)problem).evaluateConstraints(solution);
                }

                problem.evaluate(solution);
                evaluations++;
                solution = (DoubleSolution) improvementOperator.execute(solution);
                evaluations += improvementOperator.getEvaluations();
                //solution.unMarked();
                marked.setAttribute(solution,false);
                solutionSet.add(solution);
            } // while
        } // if
       // System.out.println(evaluations);
    } // while
}catch(Exception e){
e.printStackTrace();
    }

        //archive_.printFeasibleFUN("FUN_AbYSS") ;

        // STEP 4. Return the archive
        //return archive_;
    }

    @Override
    public List<? extends Solution> getResult() {
        return archive.getSolutionList();
    }

    private void initialListSolution(){
        try {
            DoubleSolution solution ;
            for (int i = 0; i < this.solutionSetSize; i++) {
                solution = super.diversificationGeneration();
                problem.evaluate(solution);
                if(problem instanceof ConstrainedProblem) {
                    ((ConstrainedProblem)problem).evaluateConstraints(solution);
                }
                evaluations++;
                solution = (DoubleSolution) improvementOperator.execute(solution);
                marked.setAttribute(solution,false);
                if(strenghtRawFitness.getAttribute(solution) == null) {
                    strenghtRawFitness.setAttribute(solution,0.0);
                }

                evaluations += improvementOperator.getEvaluations();
                solutionSet.add(solution);
            } // for
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
