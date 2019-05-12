package org.uma.jmetal.algorithm.multiobjective.automoea ;

import static org.uma.jmetal.algorithm.multiobjective.automoea.AutoMOEA.ExtArchiveType.* ;

import java.util.Collection ;
import java.util.function.BiFunction ;
import java.util.function.Function ;
import java.util.function.Supplier ;

import org.uma.jmetal.algorithm.Algorithm ;
import org.uma.jmetal.solution.Solution ;

/**
 * Generic template proposed by:<br>
 * L. C. T. Bezerra, M. López-Ibáñez and T. Stützle, "Automatic Component-Wise Design of
 * Multiobjective Evolutionary Algorithms," in IEEE Transactions on Evolutionary Computation, vol.
 * 20, no. 3, pp. 403-417, June 2016.<br>
 * DOI: 10.1109/TEVC.2015.2474158<br>
 * URL: http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=7226795&isnumber=7479595
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 * @param <S> Type of solution
 * @param <Pop> Type of population
 * @param <Pool> Type of mating pool
 */
@SuppressWarnings("serial")
public class AutoMOEA<S extends Solution<?>, Pop extends Collection<S>, Pool>
    implements Algorithm<Pop> {

  /**
   * Type of the external archive.
   */
  static enum ExtArchiveType {
    /**
     * No external archive is used, only the population.
     */
    NONE,
    /**
     * A bounded external archive is used.
     */
    BOUNDED,
    /**
     * An unbounded external archive is used.
     */
    UNBOUNDED
  }

  private final Supplier<String> name ;
  private final Supplier<String> description ;
  private final Supplier<Pop> initialPopulation ;
  private final Function<Pop, Pool> matingPoolBuilder ;
  private final Function<Pool, Pop> variator ;
  private final Function<Pop, Pop> evaluator ;
  private final BiFunction<Pop, Pop, Pop> replacer ;
  private final Supplier<ExtArchiveType> extArchiveType ;
  private final BiFunction<Pop, Pop, Pop> extReplacer ;
  private final BiFunction<Pop, Pop, Pop> union ;
  private final Supplier<Boolean> terminationStatus ;

  private Pop pop ;
  private Pop popExt ;

  /**
   * Create a specific algorithm based on the {@link AutoMOEA} template.
   * 
   * @param name {@link Supplier} of the name of the algorithm
   * @param description {@link Supplier} of the description of the algorithm
   * @param initialPopulation {@link Supplier} of the population to use at the beginning of the
   *        algorithm
   * @param matingPoolBuilder {@link Function} to build the mating pool from the current population
   * @param variator {@link Function} that transforms the mating pool into a population
   * @param evaluator {@link Function} that evaluates the solutions of a population
   * @param replacer {@link BiFunction} that updates the previous population based on the new one
   * @param extArchiveType {@link Supplier} of the type of external archive, if
   *        {@link ExtArchiveType#NONE} the population used in the algorithm will be returned
   * @param extReplacer {@link BiFunction} that updates the previous external archive based on the
   *        new population, required if the type of external archive is
   *        {@link ExtArchiveType#BOUNDED}
   * @param union {@link BiFunction} that adds the new population to the previous external archive,
   *        required if the type of external archive is {@link ExtArchiveType#UNBOUNDED}
   * @param terminationStatus {@link Supplier} of the termination status, <code>true</code> when the
   *        algorithm should be terminated
   */
  public AutoMOEA(Supplier<String> name, Supplier<String> description,
      Supplier<Pop> initialPopulation, Function<Pop, Pool> matingPoolBuilder,
      Function<Pool, Pop> variator, Function<Pop, Pop> evaluator,
      BiFunction<Pop, Pop, Pop> replacer, Supplier<ExtArchiveType> extArchiveType,
      BiFunction<Pop, Pop, Pop> extReplacer, BiFunction<Pop, Pop, Pop> union,
      Supplier<Boolean> terminationStatus) {
    this.name = name ;
    this.description = description ;
    this.initialPopulation = initialPopulation ;
    this.matingPoolBuilder = matingPoolBuilder ;
    this.variator = variator ;
    this.evaluator = evaluator ;
    this.replacer = replacer ;
    this.extArchiveType = extArchiveType ;
    this.extReplacer = extReplacer ;
    this.union = union ;
    this.terminationStatus = terminationStatus ;
  }

  @Override public void run() {
    ExtArchiveType extType = extArchiveType.get() ;
    pop = initialPopulation.get() ;
    if (extType != NONE) {
      popExt = pop ;
    }
    do {
      Pool pool = matingPoolBuilder.apply(pop) ;
      Pop popNew = variator.apply(pool) ;
      popNew = evaluator.apply(popNew) ;
      pop = replacer.apply(pop, popNew) ;
      if (extType == BOUNDED) {
        popExt = extReplacer.apply(popExt, popNew) ;
      } else if (extType == UNBOUNDED) {
        popExt = union.apply(popExt, pop) ;
      }
    } while (!terminationStatus.get()) ;
  }

  @Override public Pop getResult() {
    return extArchiveType.get() == NONE ? pop : popExt ;
  }

  @Override public String getName() {
    return name.get() ;
  }

  @Override public String getDescription() {
    return description.get() ;
  }
}
