/*
 * The MIT License
 *
 * Copyright 2018 Jackson Antonio do Prado Lima <jacksonpradolima at gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.uma.jmetal.solution.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.problem.impl.AbstractFlexibleVariableProblem;
import org.uma.jmetal.solution.IFlexibleVariableSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

/**
 * Abstract class representing a generic dynamic variable solution
 *
 * @author Jackson Antonio do Prado Lima <jacksonpradolima at gmail.com>
 * @version 1.0
 * @param <T> Solution
 * @param <P> Problem
 */
public abstract class AbstractFlexibleVariableSolution<T, P extends AbstractFlexibleVariableProblem> implements IFlexibleVariableSolution<T> {

    protected Map<Object, Object> attributes;
    protected NumberOfViolatedConstraints<IFlexibleVariableSolution<T>> numberOfViolatedConstraints;
    private double[] objectives;
    protected OverallConstraintViolation<IFlexibleVariableSolution<T>> overallConstraintViolationDegree;
    protected P problem;
    protected final JMetalRandom randomGenerator;
    private List<T> variables;

    public AbstractFlexibleVariableSolution(P problem) {
        this.problem = problem;
        this.attributes = new HashMap();
        this.randomGenerator = JMetalRandom.getInstance();

        this.objectives = new double[problem.getNumberOfObjectives()];
        this.variables = new ArrayList<>();

        this.overallConstraintViolationDegree = new OverallConstraintViolation<>();
        this.numberOfViolatedConstraints = new NumberOfViolatedConstraints<>();
    }

    @Override
    public boolean addVariable(T variable) {
        return this.variables.add(variable);
    }

    @Override
    public boolean containsVariable(T variable) {
        return this.variables.contains(variable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractFlexibleVariableSolution<?, ?> other = (AbstractFlexibleVariableSolution<?, ?>) o;

        if (!this.attributes.equals(other.attributes)) {
            return false;
        }
        if (!Arrays.equals(this.objectives, other.objectives)) {
            return false;
        }
        if (!this.variables.equals(other.variables)) {
            return false;
        }

        return true;
    }

    @Override
    public Object getAttribute(Object id) {
        return this.attributes.get(id);
    }

    @Override
    public int getMaxVariables() {
        return this.problem.getMaxVariables();
    }

    @Override
    public int getMinVariables() {
        return this.problem.getMinVariables();
    }

    @Override
    public int getNumberOfObjectives() {
        return this.objectives.length;
    }

    @Override
    public int getNumberOfVariables() {
        return this.variables.size();
    }

    public NumberOfViolatedConstraints<IFlexibleVariableSolution<T>> getNumberOfViolatedConstraints() {
        return this.numberOfViolatedConstraints;
    }

    @Override
    public double getObjective(int index) {
        return this.objectives[index];
    }

    public OverallConstraintViolation<IFlexibleVariableSolution<T>> getOverallConstraintViolationDegree() {
        return this.overallConstraintViolationDegree;
    }

    @Override
    public T getVariableValue(int index) {
        return this.variables.get(index);
    }

    @Override
    public List<T> getVariables() {
        return this.variables;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(this.objectives);
        result = 31 * result + this.variables.hashCode();
        result = 31 * result + this.attributes.hashCode();
        return result;
    }

    protected void initializeObjectiveValues() {
        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            objectives[i] = 0.0;
        }
    }

    @Override
    public void clearVariables(){
        this.variables.clear();
    }
    
    @Override
    public boolean removeVariable(T variable) {
        return this.variables.remove(variable);
    }

    @Override
    public T removeVariable(int index) {
        return this.variables.remove(index);
    }

    @Override
    public void setAttribute(Object id, Object value) {
        this.attributes.put(id, value);
    }

    public void setNumberOfViolatedConstraints(IFlexibleVariableSolution<T> solution, int violatedConstraints) {
        this.numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
    }

    @Override
    public void setObjective(int index, double value) {
        this.objectives[index] = value;
    }

    public void setOverallConstraintViolationDegree(IFlexibleVariableSolution<T> solution, double overallConstraintViolation) {
        this.overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
    }

    @Override
    public void setVariableValue(int index, T value) {
        this.variables.set(index, value);
    }

    @Override
    public String toString() {
        String result = "Variables: ";
        for (T var : variables) {
            result += "" + var + " ";
        }
        result += "Objectives: ";
        for (Double obj : objectives) {
            result += "" + obj + " ";
        }
        result += "\t";
        result += "AlgorithmAttributes: " + attributes + "\n";

        return result;
    }
}
