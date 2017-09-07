package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class PreferenceComparator <S extends Solution<?>> implements Comparator<S>, Serializable {
    private List<Double> referencePoint;
    private int comparationCase;
    private Vector<Integer> indexOfGoalsSatisfied;
    private Vector<Integer> indexOfGoalsNotSatisfied;
    public PreferenceComparator(List<Double> referencePoint){
        this.referencePoint = referencePoint;
        indexOfGoalsNotSatisfied = new Vector<>();
        indexOfGoalsSatisfied = new Vector<>();
    }
    private int numberOfGoalsSatisfied(Solution s)
    {
        int result=0;

        for (int indexOfGoal=0; indexOfGoal < this.referencePoint.size(); indexOfGoal++)
        {
            if (s.getObjective(indexOfGoal) <= referencePoint.get(indexOfGoal))
                result++;
        }

        return result;
    }

    private int numberOfIndexedGoalsSatisfied(Vector<Integer> indexs, Solution s)
    {
        int result=0;

        for (int index=0; index < indexs.size(); index++)
        {
            if (s.getObjective(indexs.get(index)) <= referencePoint.get(indexs.get(index)))
                result++;
        }

        return result;
    }
    private void comparationCase(Solution s)
    {
        this.indexOfGoalsSatisfied.clear();
        this.indexOfGoalsNotSatisfied.clear();

        for (int indexOfGoal=0; indexOfGoal < this.referencePoint.size(); indexOfGoal++)
        {
            if (s.getObjective(indexOfGoal) <= referencePoint.get(indexOfGoal))
                this.indexOfGoalsSatisfied.add(new Integer(indexOfGoal));
            else
                this.indexOfGoalsNotSatisfied.add(new Integer(indexOfGoal));
        }

        if (this.indexOfGoalsSatisfied.isEmpty())
            this.comparationCase = 0;  //case B: satisfies none of the goals
        else if (this.indexOfGoalsSatisfied.size() >= referencePoint.size())
            this.comparationCase = 1;  //case C: meet all the goals
        else
            this.comparationCase = -1; //case A: one o more (but not all) goals are satisfied
    }

    private int checkParetoDominance (Vector<Double> v1, Vector<Double> v2)
    {
        int dominate1 ; // dominate1 indicates if some objective of solution1
        // dominates the same objective in solution2. dominate2
        int dominate2 ; // is the complementary of dominate1.

        dominate1 = 0 ;
        dominate2 = 0 ;

        int flag; //stores the result of the comparison

        double value1, value2;
        for (int i = 0; i < v1.size(); i++) {
            value1 = v1.get(i);
            value2 = v2.get(i);
            if (value1 < value2) {
                flag = -1;
            } else if (value1 > value2) {
                flag = 1;
            } else {
                flag = 0;
            }

            if (flag == -1) {
                dominate1 = 1;
            }

            if (flag == 1) {
                dominate2 = 1;
            }
        }

        if (dominate1 == dominate2) {
            return 0; //No one dominate the other
        }
        if (dominate1 == 1) {
            return -1; // solution1 dominate
        }
        return 1;    // solution2 dominate
    }

    private Vector<Double> solutionObjectivesToVector(Solution s)
    {
        Vector<Double> v = new Vector<Double>();

        for (int indexOfObjective = 0; indexOfObjective<referencePoint.size(); indexOfObjective++)
        {
            v.add(s.getObjective(indexOfObjective));
        }

        return v;
    }

    private Vector<Double> getIndexedObjectivesFromSolution(Vector<Integer> indexs, Solution s)
    {
        Vector<Double> result = new Vector<Double>();

        for (int index = 0; index < indexs.size(); index++)
        {
            result.add(s.getObjective(indexs.get(index)));
        }

        return result;
    }
    @Override
    public int compare(S object1, S object2) {
        int result;

        if (object1==null)
            return 1;
        else if (object2 == null)
            return -1;

        Solution solution1 = (Solution)object1;
        Solution solution2 = (Solution)object2;



        // Equal number of violated constraints. Applying a dominance Test then
        comparationCase(solution1);

        Vector<Double> objectives1 = new Vector<Double>();
        Vector<Double> objectives2 = new Vector<Double>();

        if (this.comparationCase == 0) //case B: satisfies none of the goals
        {
            objectives1 = solutionObjectivesToVector(solution1);
            objectives2 = solutionObjectivesToVector(solution2);

            result = checkParetoDominance(objectives1, objectives2);
        }
        else if (this.comparationCase == 1) //case C: meet all the goals
        {
            objectives1 = solutionObjectivesToVector(solution1);
            objectives2 = solutionObjectivesToVector(solution2);

            int goalsDominance = checkParetoDominance(objectives1, objectives2);

            if (
                    goalsDominance == -1
                            ||
                            !(numberOfGoalsSatisfied(solution2) >= referencePoint.size())
                    )
                result = -1;
            else
            {
                result = goalsDominance;
            }
        }
        else //case A: one o more (but not all) goals are satisfied
        {
            int notSatisfiedGoalsDominance = checkParetoDominance(getIndexedObjectivesFromSolution(indexOfGoalsNotSatisfied, solution1), getIndexedObjectivesFromSolution(indexOfGoalsNotSatisfied, solution2));
            int satisfiedGoalsDominance = checkParetoDominance(getIndexedObjectivesFromSolution(indexOfGoalsSatisfied, solution1), getIndexedObjectivesFromSolution(indexOfGoalsSatisfied, solution2));

            if (
                    notSatisfiedGoalsDominance == -1
                            ||
                            (
                                    //notSatisfiedGoalsDominance == 0
                                    getIndexedObjectivesFromSolution(indexOfGoalsNotSatisfied, solution1).equals(getIndexedObjectivesFromSolution(indexOfGoalsNotSatisfied, solution2))
                                            &&
                                            (
                                                    satisfiedGoalsDominance == -1 || !(numberOfIndexedGoalsSatisfied(indexOfGoalsSatisfied, solution2) >= indexOfGoalsSatisfied.size())
                                            )
                            )
                    )
                result = -1;
            else
            {
                result = checkParetoDominance(solutionObjectivesToVector(solution1),solutionObjectivesToVector(solution2));
            }
        }

        return result;
    }
}
