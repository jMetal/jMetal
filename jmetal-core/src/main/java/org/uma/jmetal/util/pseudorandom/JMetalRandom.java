package org.uma.jmetal.util.pseudorandom;

import java.io.Serializable;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

/**
 * The source of random numbers of jMetal: a single instance that delegates every draw to a {@link
 * PseudoRandomGenerator}, by default a {@link JavaRandomGenerator}.
 *
 * <p>The instance is created when the class is loaded, so all the threads get the same one, and
 * the generator can be replaced from any thread: the change is visible to the others. With the
 * default generator all the threads draw from the same sequence; to give each thread its own
 * generator, install a {@link org.uma.jmetal.util.pseudorandom.impl.ThreadLocalRandomGenerator}.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class JMetalRandom implements Serializable {
  private static final JMetalRandom INSTANCE = new JMetalRandom();
  private volatile PseudoRandomGenerator randomGenerator ;

  private JMetalRandom() {
    randomGenerator = new JavaRandomGenerator() ;
  }

  public static JMetalRandom getInstance() {
    return INSTANCE;
  }

  public void setRandomGenerator(PseudoRandomGenerator randomGenerator) {
    Check.notNull(randomGenerator);
    this.randomGenerator = randomGenerator;
  }

  public PseudoRandomGenerator getRandomGenerator() {
    return randomGenerator ;
  }

  public int nextInt(int lowerBound, int upperBound) {
    return randomGenerator.nextInt(lowerBound, upperBound) ;
  }

  public double nextDouble() {
    return randomGenerator.nextDouble() ;
  }

  public double nextDouble(double lowerBound, double upperBound) {
    return randomGenerator.nextDouble(lowerBound, upperBound) ;
  }

  public void setSeed(long seed) {
    randomGenerator.setSeed(seed);
  }

  public long getSeed() {
    return randomGenerator.getSeed() ;
  }

  public String getGeneratorName() {
    return randomGenerator.getName() ;
  }
}
