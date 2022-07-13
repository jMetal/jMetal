package org.uma.jmetal.component.catalogue.common.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

class TerminationByEvaluationsTest {
  private  int maximumNumberOfEvaluations ;
  TerminationByEvaluations termination ;

  @BeforeEach
  void setUp() {
    maximumNumberOfEvaluations = 10000;
    termination = new TerminationByEvaluations(maximumNumberOfEvaluations);
  }

  @Test
  void TheConstructorInitializesCorrectlyTheMaximumNumberOfEvaluations() {
    assertThat(termination.getMaximumNumberOfEvaluations()).isEqualTo(maximumNumberOfEvaluations);
  }

  @Test
  void isMetRaisesAnExceptionIfTheEVALUATIONSFieldIsNotPresent() {
    Map<String, Object> algorithmStatusData = new HashMap<>();
    assertThatThrownBy(() -> termination.isMet(algorithmStatusData)).isInstanceOf(
        NullParameterException.class);
  }

  @Test
  void isMetRaisesReturnsTrueIfTheConditionIsMet() {
    Map<String, Object> algorithmStatusData = new HashMap<>();
    algorithmStatusData.put("EVALUATIONS", 10001);

    assertThat(termination.isMet(algorithmStatusData)).isTrue();
  }

  @Test
  void isMetRaisesReturnsFalseIfTheConditionIsNotMet() {
    Map<String, Object> algorithmStatusData = new HashMap<>();
    algorithmStatusData.put("EVALUATIONS", 9000);

    assertThat(termination.isMet(algorithmStatusData)).isFalse();
  }
}