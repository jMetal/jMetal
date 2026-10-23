package org.uma.jmetal.component.util;

import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * The progress of a run, in [0, 1], as its termination condition computes it from the status of
 * the algorithm. A builder creates it before the algorithm exists, gives it to the components that
 * need it, and binds it to the termination condition and the status of the algorithm once the
 * algorithm is built. It is {@code NaN} until then, and whenever the termination condition does
 * not know the progress (see {@link Termination#progress(Map)}).
 */
public class RunProgress implements DoubleSupplier {
  private Termination termination;
  private Supplier<Map<String, Object>> status;

  /**
   * Binds the progress to a termination condition and to the status of an algorithm.
   *
   * @param termination the termination condition of the algorithm
   * @param status the status of the algorithm
   */
  public void bind(Termination termination, Supplier<Map<String, Object>> status) {
    Check.notNull(termination);
    Check.notNull(status);
    this.termination = termination;
    this.status = status;
  }

  @Override
  public double getAsDouble() {
    if (termination == null) {
      return Double.NaN;
    }
    return termination.progress(status.get()).orElse(Double.NaN);
  }
}
