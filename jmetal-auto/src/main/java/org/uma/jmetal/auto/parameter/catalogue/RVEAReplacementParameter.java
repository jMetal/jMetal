package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RVEAReplacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.IRVEAEnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RVEAEnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RVEAStarEnvironmentalSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Replacement component of the RVEA family. The value selects the environmental selection of
 * RVEA ({@code rvea}), RVEA* ({@code rveaStar}) or iRVEA ({@code iRVEA}). The global parameters
 * are {@code alpha} and {@code fr}; iRVEA has the specific parameters {@code numberOfSubregions},
 * {@code lateStageFraction} and {@code epsilonKappa}. The non-configurable parameters
 * {@code numberOfObjectives}, {@code maxGenerations} and {@code referenceVectors} must be set
 * before calling {@link #getParameter()}.
 */
public class RVEAReplacementParameter extends CategoricalParameter {
  public RVEAReplacementParameter(List<String> variants) {
    super("replacement", variants);
  }

  /** Creates a new (stateful) replacement each time it is invoked. */
  @SuppressWarnings("unchecked")
  public <S extends Solution<?>> RVEAReplacement<S> getParameter() {
    Integer numberOfObjectives = (Integer) getNonConfigurableParameter("numberOfObjectives");
    Integer maxGenerations = (Integer) getNonConfigurableParameter("maxGenerations");
    List<double[]> vectors = (List<double[]>) getNonConfigurableParameter("referenceVectors");
    Check.notNull(numberOfObjectives);
    Check.notNull(maxGenerations);
    Check.notNull(vectors);

    double alpha = (double) findGlobalParameter("alpha").value();
    double fr = (double) findGlobalParameter("fr").value();

    RVEAEnvironmentalSelection<S> environmentalSelection =
        switch (value()) {
          case "rvea" ->
              new RVEAEnvironmentalSelection<>(
                  numberOfObjectives, maxGenerations, alpha, fr, vectors);
          case "rveaStar" ->
              new RVEAStarEnvironmentalSelection<>(
                  numberOfObjectives, maxGenerations, alpha, fr, vectors);
          case "iRVEA" ->
              new IRVEAEnvironmentalSelection<>(
                  numberOfObjectives,
                  maxGenerations,
                  alpha,
                  fr,
                  vectors,
                  (Integer) findSpecificParameter("numberOfSubregions").value(),
                  (Double) findSpecificParameter("lateStageFraction").value(),
                  (Double) findSpecificParameter("epsilonKappa").value());
          default -> throw new JMetalException("RVEA replacement unknown: " + value());
        };

    return new RVEAReplacement<>(environmentalSelection);
  }

  /** Returns the algorithm name associated with the current value. */
  public String algorithmName() {
    return switch (value()) {
      case "rvea" -> "RVEA";
      case "rveaStar" -> "RVEA*";
      case "iRVEA" -> "iRVEA";
      default -> throw new JMetalException("RVEA replacement unknown: " + value());
    };
  }
}
