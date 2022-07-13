package org.uma.jmetal.component.catalogue.common.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByComputingTime;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

class TerminationByComputingTimeTest {

  private long maxComputingTime ;
  TerminationByComputingTime termination ;

  @BeforeEach
  void setUp() {
    maxComputingTime = 20000;
    termination = new TerminationByComputingTime(maxComputingTime);
  }

  @Test
  void TheConstructorInitializesCorrectlyTheMaxComputingTime() {
    assertThat(termination.getMaxComputingTime()).isEqualTo(maxComputingTime);
  }

  @Test
  void isMetRaisesAnExceptionIfTheEVALUATIONSFieldIsNotPresent() {
    Map<String, Object> algorithmStatusData = new HashMap<>();
    algorithmStatusData.put("COMPUTING_TIME", 1000) ;
    assertThatThrownBy(() -> termination.isMet(algorithmStatusData)).isInstanceOf(
        NullParameterException.class);
  }

  @Test
  void isMetRaisesAnExceptionIfTheCOMPUTING_TIMEFieldIsNotPresent() {
    Map<String, Object> algorithmStatusData = new HashMap<>();
    algorithmStatusData.put("EVALUATIONS", 2500) ;

    assertThatThrownBy(() -> termination.isMet(algorithmStatusData)).isInstanceOf(
        NullParameterException.class);
  }

  @Test
  void isMetRaisesReturnsTrueIfTheConditionIsMet() {
    Map<String, Object> algorithmStatusData = new HashMap<>();
    algorithmStatusData.put("COMPUTING_TIME", maxComputingTime + 1);
    algorithmStatusData.put("EVALUATIONS", 2500) ;

    assertThat(termination.isMet(algorithmStatusData)).isTrue();
    assertThat(termination.getEvaluations()).isEqualTo(2500) ;
  }

  @Test
  void isMetRaisesReturnsFalseIfTheConditionIsNotMet() {
    Map<String, Object> algorithmStatusData = new HashMap<>();
    algorithmStatusData.put("COMPUTING_TIME", maxComputingTime -1);
    algorithmStatusData.put("EVALUATIONS", 2500) ;

    assertThat(termination.isMet(algorithmStatusData)).isFalse();
  }
}