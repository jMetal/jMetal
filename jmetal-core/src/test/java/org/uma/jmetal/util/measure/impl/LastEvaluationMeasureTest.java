package org.uma.jmetal.util.measure.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.impl.LastEvaluationMeasure.Evaluation;

class LastEvaluationMeasureTest {

	@Test
  void testSpecializedPushActLikeParentPush() {
		LastEvaluationMeasure<String, Integer> measure = new LastEvaluationMeasure<>();
		@SuppressWarnings("unchecked")
		final Evaluation<String, Integer>[] lastEvaluation = new Evaluation[] { null };
		measure.register(new MeasureListener<Evaluation<String, Integer>>() {

			@Override
			public void measureGenerated(Evaluation<String, Integer> evaluation) {
				lastEvaluation[0] = evaluation;
			}
		});

		String solution = "individual";
		int value = 3;
		measure.push(solution, value);
		Assertions.assertNotNull(lastEvaluation[0]);
		Assertions.assertEquals(solution, lastEvaluation[0].solution);
		Assertions.assertEquals(value, (Object) lastEvaluation[0].value);

		lastEvaluation[0] = null;
		Evaluation<String, Integer> evaluation = new Evaluation<>();
		evaluation.solution = solution;
		evaluation.value = value;
		measure.push(evaluation);
		Assertions.assertNotNull(lastEvaluation[0]);
		Assertions.assertEquals(solution, lastEvaluation[0].solution);
		Assertions.assertEquals(value, (Object) lastEvaluation[0].value);
	}

}
