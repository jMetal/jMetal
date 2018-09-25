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
package org.uma.jmetal.problem.impl;

import org.uma.jmetal.solution.IFlexibleVariableSolution;

/**
 *
 * @author Jackson Antonio do Prado Lima <jacksonpradolima at gmail.com>
 * @version 1.0
 * @param <S> Solution type
 */
@SuppressWarnings("serial")
public abstract class AbstractFlexibleVariableProblem<S extends IFlexibleVariableSolution<?>> extends AbstractGenericProblem<S> {

    public AbstractFlexibleVariableProblem(int maxVariables, int minVariables) {
        this.maxVariables = maxVariables;
        this.minVariables = minVariables;
    }

    private int maxVariables;
    private int minVariables;

    /**
     * Returns the maximum variable size
     *
     * @return maximum variable size
     */
    public int getMaxVariables() {
        return maxVariables;
    }

    /**
     * @param maxVariables the maxVariables to set
     */
    public void setMaxVariables(int maxVariables) {
        this.maxVariables = maxVariables;
    }

    /**
     * Returns the minimum variable size
     *
     * @return minimum variable size
     */
    public int getMinVariables() {
        return minVariables;
    }

    /**
     * @param minVariables the minVariables to set
     */
    public void setMinVariables(int minVariables) {
        this.minVariables = minVariables;
    }
}
