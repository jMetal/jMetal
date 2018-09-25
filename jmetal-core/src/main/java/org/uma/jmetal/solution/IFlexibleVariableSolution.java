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
package org.uma.jmetal.solution;

import java.util.List;

/**
 * Interface representing a solution with dynamic size variables. This interface
 * is used when it is necessary to manipulate the number of variables of a
 * solution. For example, a solution may have 5 variables and other solution 2
 * variables, after add, remove or set some variable.
 *
 * @author Jackson Antonio do Prado Lima <jacksonpradolima at gmail.com>
 * @version 1.0
 * @param <T>
 */
public interface IFlexibleVariableSolution<T> extends Solution<T> {

    /**
     * Appends the specified variable to the end of this list (optional
     * operation).
     *
     * <p>
     * Lists that support this operation may place limitations on what variables
     * may be added to this list. In particular, some lists will refuse to add
     * null variables, and others will impose restrictions on the type of
     * variables that may be added. List classes should clearly specify in their
     * documentation any restrictions on what variables may be added.
     *
     * @param variable variable to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean addVariable(T variable);

    /**
     * Returns <tt>true</tt> if this list contains the specified variable. More
     * formally, returns <tt>true</tt> if and only if this list contains at
     * least one variable <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param variable variable whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified variable
     */
    boolean containsVariable(T variable);

    /**
     * Returns the maximum variable size
     *
     * @return maximum variable size
     */
    public int getMaxVariables();

    /**
     * Returns the minimum variable size
     *
     * @return minimum variable size
     */
    public int getMinVariables();

    /**
     * Returns the variables in this list.
     *
     * @return the variables in this list
     */
    public List<T> getVariables();

    /**
     * Removes the first occurrence of the specified variable from this list, if
     * it is present (optional operation). If this list does not contain the
     * variable, it is unchanged. More formally, removes the variable with the
     * lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an variable exists). Returns <tt>true</tt> if this list
     * contained the specified variable (or equivalently, if this list changed
     * as a result of the call).
     *
     * @param variable variable to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified variable
     */
    public boolean removeVariable(T variable);

    /**
     * Removes the variable at the specified position in this list (optional
     * operation). Shifts any subsequent variables to the left (subtracts one
     * from their indices). Returns the variable that was removed from the list.
     *
     * @param index the index of the variable to be removed
     * @return the variable previously at the specified position
     */
    public T removeVariable(int index);

    /**
     * Removes all of the elements from this list (optional operation). The list
     * will be empty after this call returns.
     *
     */
    public void clearVariables();
}
