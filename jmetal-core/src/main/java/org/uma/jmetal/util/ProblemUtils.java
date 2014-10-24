package org.uma.jmetal.util;

import org.uma.jmetal.problem.Problem;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Antonio J. Nebro on 21/10/14.
 */
public class ProblemUtils {

  /**
   * Create an instance of problem passed as argument
   * @param problemName A full qualified problem name
   * @return An instance of the problem
   */
  public static Problem loadProblem(String problemName) {
    Problem problem ;
    try {
      problem = (Problem)Class.forName(problemName).getConstructor().newInstance() ;
    } catch (InstantiationException e) {
      throw new JMetalException("newInstance() cannot instantiate (abstract class)", e) ;
    } catch (IllegalAccessException e) {
      throw new JMetalException("newInstance() is not usable (uses restriction)", e) ;
    } catch (InvocationTargetException e) {
      throw new JMetalException("an exception was thrown during the call of newInstance()", e) ;
    } catch (NoSuchMethodException e) {
      throw new JMetalException("getConstructor() was not able to find the constructor without arguments", e) ;
    } catch (ClassNotFoundException e) {
      throw new JMetalException("Class.forName() did not recognized the name of the class", e) ;
    }

    return problem ;
  }
}
