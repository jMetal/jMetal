package org.uma.jmetal.util.pseudorandom.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongFunction;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

/**
 * A {@link PseudoRandomGenerator} with one generator per thread, for programs that run algorithms
 * in parallel threads (an experiment with several cores, a meta-optimizer that evaluates
 * configurations in parallel, a thread pool of the user...). With the default generator of {@link
 * JMetalRandom} all the threads draw from the same {@link java.util.Random}, whose seed is an
 * atomic variable, so the threads contend for it and slow each other down far more than their
 * number explains; the default generators of other types are not even safe to share.
 *
 * <p>It is installed with {@link #install()} (or {@link #install(GeneratorFactory)}):
 *
 * <ul>
 *   <li>The thread that installs it keeps the generator that {@link JMetalRandom} had, so what runs
 *       in that thread draws the same numbers as before.
 *   <li>Every other thread gets its own generator, made by the factory (by default a {@link
 *       JavaRandomGenerator}), the first time it draws. Its seed comes from the seed of the
 *       generator of the installing thread and from the order in which the threads draw for the
 *       first time, scrambled with SplitMix64, so the seeds of the threads differ, but which thread
 *       gets which seed depends on the scheduling: the draws of a thread are not reproducible
 *       unless it sets its own seed.
 *   <li>{@link #setSeed(long)} and {@link #getSeed()} apply to the generator of the calling thread,
 *       so each thread (each run of an algorithm, for instance) can make its draws reproducible
 *       without affecting the others.
 * </ul>
 */
@SuppressWarnings("serial")
public final class ThreadLocalRandomGenerator implements PseudoRandomGenerator {
  /** Builds the generator of a thread from its seed; it must be serializable. */
  @FunctionalInterface
  public interface GeneratorFactory extends LongFunction<PseudoRandomGenerator>, Serializable {}

  private static final long GOLDEN_GAMMA = 0x9E3779B97F4A7C15L;

  private final long baseSeed;
  private final GeneratorFactory factory;
  private final String name;
  private final AtomicLong threadsSeeded = new AtomicLong();
  private transient ThreadLocal<PseudoRandomGenerator> generators;

  /**
   * Creates a generator whose other threads get a {@link JavaRandomGenerator}.
   *
   * @param generatorOfThisThread the generator of the thread that creates this one
   */
  public ThreadLocalRandomGenerator(PseudoRandomGenerator generatorOfThisThread) {
    this(generatorOfThisThread, JavaRandomGenerator::new);
  }

  /**
   * Creates a generator whose other threads get the generators made by a factory.
   *
   * @param generatorOfThisThread the generator of the thread that creates this one
   * @param factory builds the generator of each of the other threads from its seed
   */
  public ThreadLocalRandomGenerator(
      PseudoRandomGenerator generatorOfThisThread, GeneratorFactory factory) {
    Check.notNull(generatorOfThisThread);
    Check.notNull(factory);
    Check.that(
        !(generatorOfThisThread instanceof ThreadLocalRandomGenerator),
        "The generator of the thread is already a ThreadLocalRandomGenerator");

    this.baseSeed = generatorOfThisThread.getSeed();
    this.factory = factory;
    this.name = "ThreadLocalRandomGenerator (" + factory.apply(baseSeed).getName() + ")";
    this.generators = newGenerators();
    this.generators.set(generatorOfThisThread);
  }

  /**
   * Makes {@link JMetalRandom} use one generator per thread, unless it already does; the other
   * threads get a {@link JavaRandomGenerator}.
   *
   * @return the generator that {@link JMetalRandom} uses
   */
  public static ThreadLocalRandomGenerator install() {
    return install(JavaRandomGenerator::new);
  }

  /**
   * Makes {@link JMetalRandom} use one generator per thread, unless it already does.
   *
   * @param factory builds the generator of each thread other than the calling one from its seed
   * @return the generator that {@link JMetalRandom} uses (the one already installed, if any,
   *     whose factory is kept)
   */
  public static synchronized ThreadLocalRandomGenerator install(GeneratorFactory factory) {
    JMetalRandom random = JMetalRandom.getInstance();
    if (random.getRandomGenerator() instanceof ThreadLocalRandomGenerator installed) {
      return installed;
    }
    var generator = new ThreadLocalRandomGenerator(random.getRandomGenerator(), factory);
    random.setRandomGenerator(generator);
    return generator;
  }

  private ThreadLocal<PseudoRandomGenerator> newGenerators() {
    return ThreadLocal.withInitial(
        () -> factory.apply(seedOfThread(threadsSeeded.incrementAndGet())));
  }

  /** The SplitMix64 scrambling of the base seed plus k times the golden gamma. */
  private long seedOfThread(long k) {
    long z = baseSeed + k * GOLDEN_GAMMA;
    z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
    z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
    return z ^ (z >>> 31);
  }

  /** The generator of the calling thread. */
  public PseudoRandomGenerator generatorOfThisThread() {
    return generators.get();
  }

  @Override
  public int nextInt(int lowerBound, int upperBound) {
    return generators.get().nextInt(lowerBound, upperBound);
  }

  @Override
  public double nextDouble(double lowerBound, double upperBound) {
    return generators.get().nextDouble(lowerBound, upperBound);
  }

  @Override
  public double nextDouble() {
    return generators.get().nextDouble();
  }

  /** Sets the seed of the generator of the calling thread. */
  @Override
  public void setSeed(long seed) {
    generators.get().setSeed(seed);
  }

  /** The seed of the generator of the calling thread. */
  @Override
  public long getSeed() {
    return generators.get().getSeed();
  }

  @Override
  public String getName() {
    return name;
  }

  /**
   * The generators of the threads are not serialized: after deserialization, every thread,
   * including the one that deserializes it, gets a new generator from the factory.
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    generators = newGenerators();
  }
}
