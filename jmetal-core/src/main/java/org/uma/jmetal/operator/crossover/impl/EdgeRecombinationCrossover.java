package org.uma.jmetal.operator.crossover.impl;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.*;

/**
 * Edge Recombination Crossover (ERX) operator for permutation-based genetic algorithms. This
 * operator constructs offspring by preserving adjacency (edge) information from the parent
 * permutations. It builds an edge map (also known as an adjacency matrix) of adjacent genes from
 * both parents and constructs offspring by traversing this map, selecting edges that exist in
 * either parent to maintain as much parental adjacency as possible.
 *
 * @author Nicolás R. Uribe
 */
public class EdgeRecombinationCrossover implements CrossoverOperator<PermutationSolution<Integer>> {
  private final double probability;
  private final JMetalRandom randomNumberGenerator = JMetalRandom.getInstance();

  // Constructor to initialize the crossover operator with a given probability
  public EdgeRecombinationCrossover(double probability) {
    Check.probabilityIsValid(probability);
    this.probability = probability;
  }

  @Override
  public List<PermutationSolution<Integer>> execute(List<PermutationSolution<Integer>> solutions) {
    // Ensure there are exactly two parents to perform the crossover
    if (solutions.size() != 2) {
      throw new JMetalException("EdgeRecombinationCrossover requires exactly two parents.");
    }

    // Get the two parent solutions
    PermutationSolution<Integer> parent1 = solutions.get(0);
    PermutationSolution<Integer> parent2 = solutions.get(1);

    // Create offspring as copies of the parents
    List<PermutationSolution<Integer>> offspring = new ArrayList<>(2);
    offspring.add((PermutationSolution<Integer>) parent1.copy());
    offspring.add((PermutationSolution<Integer>) parent2.copy());

    // Perform crossover based on the given probability
    if (randomNumberGenerator.nextDouble() < probability) {
      // Generate offspring using edge recombination for both parents
      List<Integer> child1 = edgeRecombination(parent1.variables(), parent2.variables());
      List<Integer> child2 = edgeRecombination(parent2.variables(), parent1.variables());

      // Set the values in the offspring solutions
      for (int i = 0; i < child1.size(); i++) {
        offspring.get(0).variables().set(i, child1.get(i));
        offspring.get(1).variables().set(i, child2.get(i));
      }
    }

    return offspring;
  }

  // Method to perform edge recombination crossover on two parent solutions
  // This method selects neighbors of the current node, choosing the one with the fewest neighbors
  // If multiple neighbors have the same count, one is selected randomly
  private List<Integer> edgeRecombination(List<Integer> parent1, List<Integer> parent2) {
    // Create an edge map to store neighbors of each element from both parents
    Map<Integer, Set<Integer>> edgeMap = new HashMap<>();

    // Add edges from the first parent to the edge map
    for (int i = 0; i < parent1.size(); i++) {
      int value = parent1.get(i);
      edgeMap.putIfAbsent(value, new HashSet<>());
      // Add the next and previous elements as neighbors
      edgeMap.get(value).add(parent1.get((i + 1) % parent1.size())); // Next element (wrap around)
      edgeMap
          .get(value)
          .add(
              parent1.get(
                  (i - 1 + parent1.size()) % parent1.size())); // Previous element (wrap around)
    }

    // Add edges from the second parent to the edge map
    for (int i = 0; i < parent2.size(); i++) {
      int value = parent2.get(i);
      edgeMap.putIfAbsent(value, new HashSet<>());
      // Add the next and previous elements as neighbors
      edgeMap.get(value).add(parent2.get((i + 1) % parent2.size())); // Next element (wrap around)
      edgeMap
          .get(value)
          .add(
              parent2.get(
                  (i - 1 + parent2.size()) % parent2.size())); // Previous element (wrap around)
    }

    // Initialize the child as an empty list
    List<Integer> child = new ArrayList<>();
    // Start the child with the first element of parent1
    Integer current = parent1.get(0);

    // Continue until the child has all the elements
    while (child.size() < parent1.size()) {
      // Add the current element to the child
      child.add(current);
      // Get the neighbors of the current element
      List<Integer> neighbors = new ArrayList<>(edgeMap.get(current));
      // Remove already visited nodes from the neighbors list
      neighbors.removeAll(child);

      if (neighbors.isEmpty()) {
        // If there are no unvisited neighbors, choose any remaining element
        for (Integer value : edgeMap.keySet()) {
          if (!child.contains(value)) {
            current = value;
            break;
          }
        }
      } else {
        // Select the neighbor with the fewest neighbors
        List<Integer> minNeighbors = new ArrayList<>();
        int minSize = Integer.MAX_VALUE;
        for (Integer neighbor : neighbors) {
          int neighborSize = edgeMap.get(neighbor).size();
          if (neighborSize < minSize) {
            minNeighbors.clear();
            minNeighbors.add(neighbor);
            minSize = neighborSize;
          } else if (neighborSize == minSize) {
            minNeighbors.add(neighbor);
          }
        }
        // Randomly select from neighbors with the minimum number of edges
        current = minNeighbors.get(randomNumberGenerator.nextInt(0, minNeighbors.size() - 1));
      }

      // Remove the current node from the adjacency lists of all nodes to avoid revisiting
      for (Set<Integer> adjacencyList : edgeMap.values()) {
        adjacencyList.remove(current);
      }
    }

    return child;
  }

  @Override
  public double crossoverProbability() {
    return this.probability;
  }

  @Override
  public int numberOfRequiredParents() {
    return 2;
  }

  @Override
  public int numberOfGeneratedChildren() {
    return 2;
  }
}
