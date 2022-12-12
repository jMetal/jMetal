package org.uma.jmetal.qualityindicator;

import java.util.ArrayList;
import java.util.List;
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
  public static List<QualityIndicator> getAvailableIndicators(double[][] referenceFront) {
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
   * @param indicatorName
   * @param indicatorList
   * @return
   */
  public static QualityIndicator getIndicatorFromName(String indicatorName,
      List<QualityIndicator> indicatorList) {
    QualityIndicator result = null;

    for (QualityIndicator indicator : indicatorList) {
      if (indicator.getName().equals(indicatorName)) {
        result = indicator;
        break;
      }
    }

    Check.notNull(result);

    return result;
  }

  public static void printQualityIndicators(double[][] front, double[][] referenceFront) {
    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    double[][] normalizedFront =
        NormalizeUtils.normalize(
            front,
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    List<QualityIndicator> qualityIndicators = getAvailableIndicators(normalizedReferenceFront);
    for (QualityIndicator indicator : qualityIndicators) {
      JMetalLogger.logger.info(
          () -> indicator.getName() + ": " + indicator.compute(normalizedFront));
    }
  }
}
