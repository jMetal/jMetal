package org.uma.jmetal.auto.old.nsgaiib;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.TournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.wfg.WFG6;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

public interface Parameter<T> {

	// XXX - Definition

	public T getFrom(String[] args);

	// XXX - Factory methods

	public static Parameter<String> onString(String key) {
		return on(key, Function.identity());
	}

	public static Parameter<Integer> onInt(String key) {
		return on(key, Integer::parseInt);
	}

	public static Parameter<Double> onDouble(String key) {
		return on(key, Double::parseDouble);
	}

	public static <T> Parameter<T> onMap(String key, Map<String, Supplier<T>> suppliers) {
		return args -> suppliers.get(retrieve(args, key)).get();
	}

	public static <T> Parameter<T> on(String key, Function<String, T> parser) {
		return args -> parser.apply(retrieve(args, key));
	}

	public static String retrieve(String[] args, String key) {
		int index = Arrays.asList(args).indexOf(key);
		if (index == -1 || index == args.length - 1) {
			throw new MissingParameterException(key);
		} else {
			return args[index + 1];
		}
	}

	// XXX - Specific parameters

	// Simple integers

	public static Parameter<Integer> POPULATION_SIZE = Parameter.onInt("--populationSize");

	public static Parameter<Integer> OFFSPRING_POPULATION_SIZE = Parameter.onInt("--offspringPopulationSize");

	public static Parameter<Integer> MAX_EVALUATIONS = Parameter.onInt("--maxEvaluations");

	// Specific values mapped to specific strings

	public static Parameter<Problem<?>> PROBLEM = Parameter.onMap("--problemName",
			Collections.singletonMap("WFG6", () -> new WFG6())
			// ... more problems ...
	);

	public static Parameter<SolutionListEvaluator<?>> EVALUATOR = Parameter.onMap("--evaluator",
			Collections.singletonMap("sequential", () -> new SequentialSolutionListEvaluator<>())
			// ... more evaluators ...
	);

	public static Parameter<Comparator<?>> DOMINANCE_COMPARATOR = Parameter.onMap("--comparator",
			Collections.singletonMap("dominance", () -> new DominanceComparator<>())
			// ... more comparators ...
	);

	// Fully customized parameters, building on several values

	public static Parameter<SelectionOperator<?, ?>> SELECTION_OPERATOR = args -> {
		String name = Parameter.onString("--selection").getFrom(args);
		if (name.equals("tournament")) {
			int size = Parameter.onInt("--selectionTournamentSize").getFrom(args);
			return new TournamentSelection<>(size);
		} else {
			// ... more selectors ...
			return null;
		}
	};

	public static Parameter<CrossoverOperator<?>> CROSSOVER_OPERATOR = args -> {
		String name = Parameter.onString("--crossover").getFrom(args);
		if (name.equals("BLX_ALPHA")) {
			double probability = Parameter.onDouble("--crossoverProbability").getFrom(args);
			double alpha = Parameter.onDouble("--blxAlphaCrossoverAlphaValue").getFrom(args);
			return new BLXAlphaCrossover(probability, alpha);
		} else {
			// ... more crossover operators ...
			return null;
		}
	};

	public static Parameter<MutationOperator<?>> MUTATION_OPERATOR = args -> {
		String name = Parameter.onString("--mutation").getFrom(args);
		if (name.equals("polynomial")) {
			double probability = Parameter.onDouble("--mutationProbability").getFrom(args);
			double distributionIndex = Parameter.onDouble("--mutationDistributionIndex").getFrom(args);
			return new PolynomialMutation(probability, distributionIndex);
		} else {
			// ... more crossover operators ...
			return null;
		}
	};

}
