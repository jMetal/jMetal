package org.uma.jmetal.qualityindicator;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GeneralizedSpread;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.errorchecking.Check;

public class QualityIndicatorUtils {
  /**
   * Creates a list with the available indicators (but setCoverage)
   *
   * @param referenceFront
   * @return
   */
  public static @NotNull List<QualityIndicator> getAvailableIndicators(double[][] referenceFront) {
    List<QualityIndicator> list = new ArrayList<>();
    list.add(new Epsilon(referenceFront));
    list.add(new PISAHypervolume(referenceFront));
    list.add(new NormalizedHypervolume(referenceFront));
    list.add(new GenerationalDistance(referenceFront));
    list.add(new InvertedGenerationalDistance(referenceFront));
    list.add(new InvertedGenerationalDistancePlus(referenceFront));
    list.add(new Spread(referenceFront));
    list.add(new GeneralizedSpread(referenceFront));

    return list;
  }

  /**
   * Given an indicator name, finds the indicator in the list of indicator
   *
   * @param name
   * @param list
   * @return
   */
  public static QualityIndicator getIndicatorFromName(String name, List<QualityIndicator> list) {
      QualityIndicator result = null;
      for (@NotNull QualityIndicator indicator : list) {
          if (indicator.getName().equals(name)) {
              result = indicator;
              break;
          }
      }

      Check.notNull(result);

    return result;
  }

  public static void printQualityIndicators(double[][] front, double[][] referenceFront){
      var normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
      var normalizedFront =
            NormalizeUtils.normalize(
                    front,
                    NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                    NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

      var qualityIndicators = getAvailableIndicators(normalizedReferenceFront) ;
    for (@NotNull QualityIndicator indicator: qualityIndicators) {
      JMetalLogger.logger.info(() ->indicator.getName() + ": " + indicator.compute(normalizedFront)) ;
    }
  }
}
