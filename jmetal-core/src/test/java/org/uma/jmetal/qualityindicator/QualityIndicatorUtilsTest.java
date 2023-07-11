package org.uma.jmetal.qualityindicator;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Unit tests for class QualityIndicatorUtils")
class QualityIndicatorUtilsTest {


  @Nested
  @DisplayName("When calling method getIndicatorFromName()")
  class getIndicatorFromNameTests {

    @Test
    @DisplayName("The method return the required indicator if it exists")
     void anExistingIndicatorNameReturnsTheIndicator() {
      String indicatorName = "EP";
      QualityIndicator obtainedIndicator = QualityIndicatorUtils.getIndicatorFromName(
          indicatorName);

      assertEquals(indicatorName, obtainedIndicator.name());
      assertTrue(obtainedIndicator instanceof Epsilon) ;
    }

    @Test
    @DisplayName("The method raises and exception if the required indicator does not exist")
    void anNonExistingIndicatorNameRaisesAnException() {
      String indicatorName = "VF";
      assertThrows(JMetalException.class, () -> QualityIndicatorUtils.getIndicatorFromName(
          indicatorName));
    }
  }

  @Nested
  @DisplayName("When calling method getIndicatorsFromNames()")
  class getIndicatorFromNamesTests {

    @Test
    @DisplayName("The method return a list with an indicator if the name list contains a name")
    void aListWithAnIndicatorNameReturnsAListWithTheIndicator() {
      String indicatorName = "EP";
      List<QualityIndicator> obtainedIndicators = QualityIndicatorUtils.getIndicatorsFromNames(
          List.of(indicatorName));

      assertEquals(1, obtainedIndicators.size());
      assertTrue(obtainedIndicators.get(0) instanceof Epsilon);
    }

    @Test
    @DisplayName("The method return a list with the required indicators (EP, NHV, IGD+)")
    void aListWithThreeIndicatorNamesReturnsAListWithTheIndicators() {
      List<QualityIndicator> obtainedIndicators = QualityIndicatorUtils.getIndicatorsFromNames(
          List.of("EP", "NHV", "IGD+"));

      assertEquals(3, obtainedIndicators.size());
      assertTrue(obtainedIndicators.get(0) instanceof Epsilon);
      assertTrue(obtainedIndicators.get(1) instanceof NormalizedHypervolume);
      assertTrue(obtainedIndicators.get(2) instanceof InvertedGenerationalDistancePlus);
    }

    @Test
    @DisplayName("The method raises an Exception if an indicator name does not exist")
    void anNonExistingIndicatorNameRaisesAnException() {
      assertThrows(JMetalException.class, () -> QualityIndicatorUtils.getIndicatorsFromNames(
          List.of("EP", "FOO", "IGD+")));
    }
  }
}