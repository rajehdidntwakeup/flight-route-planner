package com.flightplanner.algorithm;

import com.flightplanner.graph.FlightGraph;
import com.flightplanner.model.Airport;
import com.flightplanner.model.Flight;
import com.flightplanner.model.Route;

import java.util.*;

/**
 * Implements route-finding algorithms using graph search techniques.
 * Supports finding optimal routes based on different criteria:
 * - Cheapest route (minimum total price)
 * - Fastest route (minimum total duration)
 * - Fewest stopovers (minimum number of connections)
 * - Slowest route (maximum total duration, limited to 3 stopovers)
 */
public class RouteFinder {
  private final FlightGraph graph;

  // Route finding criteria
  public static final int CRITERION_CHEAPEST = 1;
  public static final int CRITERION_FASTEST = 2;
  public static final int CRITERION_FEWEST_STOPOVERS = 3;
  public static final int CRITERION_SLOWEST = 4;

  // Maximum stopovers for slowest route search
  private static final int MAX_STOPOVERS = 3;

  /**
   * Constructs a RouteFinder with the given graph and flight map.
   *
   * @param graph The flight graph
   */
  public RouteFinder(FlightGraph graph) {
    this.graph = graph;
  }

  /**
   * Finds a route between two airports based on the specified criterion.
   *
   * @param originIata      IATA code of the origin airport
   * @param destinationIata IATA code of the destination airport
   * @param criterion       Route finding criterion (1-4)
   * @return The optimal Route, or null if no route exists
   */
  public Route findRoute(String originIata, String destinationIata, int criterion) {
    Airport origin = graph.getAirport(originIata);
    Airport destination = graph.getAirport(destinationIata);

    if (origin == null || destination == null) {
      System.err.println("Error: Invalid origin or destination airport");
      return null;
    }

    return switch (criterion) {
      case CRITERION_CHEAPEST -> findCheapestRoute(origin, destination);
      case CRITERION_FASTEST -> findFastestRoute(origin, destination);
      case CRITERION_FEWEST_STOPOVERS -> findFewestStopoversRoute(origin, destination);
      case CRITERION_SLOWEST -> findSlowestRoute(origin, destination);
      default -> {
        System.err.println("Error: Invalid criterion");
        yield null;
      }
    };
  }

  /**
   * Finds the cheapest route using Dijkstra's algorithm with price as edge weight.
   *
   * @param origin      Origin airport
   * @param destination Destination airport
   * @return The cheapest Route, or null if no route exists
   */
  private Route findCheapestRoute(Airport origin, Airport destination) {
    return dijkstra(origin, destination, Flight::getPrice);
  }

  /**
   * Finds the fastest route using Dijkstra's algorithm with duration as edge weight.
   * Includes stopover time (20 minutes) between connecting flights.
   *
   * @param origin      Origin airport
   * @param destination Destination airport
   * @return The fastest Route, or null if no route exists
   */
  private Route findFastestRoute(Airport origin, Airport destination) {
    return dijkstra(origin, destination, (flight) -> (double) (flight.getDuration() + Route.STOPOVER_TIME));
  }

  /**
   * Finds the route with fewest stopovers using Dijkstra's algorithm.
   * Each flight has weight 1, so the shortest path has the fewest flights.
   *
   * @param origin      Origin airport
   * @param destination Destination airport
   * @return The route with fewest stopovers, or null if no route exists
   */
  private Route findFewestStopoversRoute(Airport origin, Airport destination) {
    return dijkstra(origin, destination, (flight) -> 1.0);
  }

  /**
   * Finds the slowest route using modified depth-limited DFS.
   * Limited to maximum 3 stopovers (4 flights total).
   *
   * @param origin      Origin airport
   * @param destination Destination airport
   * @return The slowest Route, or null if no route exists
   */
  private Route findSlowestRoute(Airport origin, Airport destination) {
    List<Flight> longestPath = new ArrayList<>();
    Set<String> visited = new HashSet<>();
    List<Flight> currentPath = new ArrayList<>();

    dfsForSlowest(origin.getIata(), destination.getIata(), visited, currentPath, longestPath, 0);

    if (longestPath.isEmpty()) {
      return null;
    }

    List<Integer> flightIds = longestPath.stream()
        .map(Flight::getId)
        .toList();

    return new Route(0, flightIds, longestPath);
  }

  /**
   * Depth-first search to find the longest path (slowest route).
   * Uses depth limit to prevent infinite loops and excessive computation.
   *
   * @param currentIata Current airport IATA code
   * @param destIata    Destination airport IATA code
   * @param visited     Set of visited airports in current path
   * @param currentPath Current path of flights
   * @param longestPath Best (longest) path found so far
   * @param depth       Current depth (number of flights)
   */
  private void dfsForSlowest(String currentIata, String destIata, Set<String> visited,
                             List<Flight> currentPath, List<Flight> longestPath, int depth) {
    // Depth limit: maximum 3 stopovers = 4 flights
    if (depth > MAX_STOPOVERS + 1) {
      return;
    }

    // If we reached the destination
    if (currentIata.equals(destIata) && !currentPath.isEmpty()) {
      int currentDuration = calculateTotalDuration(currentPath);
      int longestDuration = calculateTotalDuration(longestPath);

      if (currentDuration > longestDuration) {
        longestPath.clear();
        longestPath.addAll(currentPath);
      }
      return;
    }

    // Mark current airport as visited
    visited.add(currentIata);

    // Explore all outgoing flights
    List<Flight> outgoingFlights = graph.getFlightsFrom(currentIata);
    for (Flight flight : outgoingFlights) {
      String nextIata = flight.getDestination();

      // Avoid cycles
      if (!visited.contains(nextIata)) {
        currentPath.add(flight);
        dfsForSlowest(nextIata, destIata, visited, currentPath, longestPath, depth + 1);
        currentPath.remove(currentPath.size() - 1);
      }
    }

    // Unmark the current airport
    visited.remove(currentIata);
  }

  /**
   * Dijkstra's algorithm implementation for finding optimal routes.
   * Uses a priority queue to efficiently find the shortest path based on custom edge weights.
   *
   * @param origin      Origin airport
   * @param destination Destination airport
   * @param weightFunc  Function to calculate edge weight from a flight
   * @return The optimal Route, or null if no route exists
   */
  private Route dijkstra(Airport origin, Airport destination, WeightFunction weightFunc) {
    // Priority queue: (cumulative weight, current airport IATA, path of flights)
    PriorityQueue<DijkstraNode> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.weight));
    Map<String, Double> distances = new HashMap<>();

    pq.offer(new DijkstraNode(0.0, origin.getIata(), new ArrayList<>()));
    distances.put(origin.getIata(), 0.0);

    while (!pq.isEmpty()) {
      DijkstraNode current = pq.poll();
      String currentIata = current.airportIata;
      double currentWeight = current.weight;
      List<Flight> currentPath = current.path;

      // If we reached the destination, construct and return the route
      if (currentIata.equals(destination.getIata())) {
        List<Integer> flightIds = currentPath.stream()
            .map(Flight::getId)
            .toList();
        return new Route(0, flightIds, currentPath);
      }

      // Skip if we've already found a better path to this airport
      if (currentWeight > distances.getOrDefault(currentIata, Double.MAX_VALUE)) {
        continue;
      }

      // Explore neighboring flights
      List<Flight> outgoingFlights = graph.getFlightsFrom(currentIata);
      for (Flight flight : outgoingFlights) {
        String nextIata = flight.getDestination();
        double edgeWeight = weightFunc.getWeight(flight);
        double newWeight = currentWeight + edgeWeight;

        // If we found a better path to the next airport
        if (newWeight < distances.getOrDefault(nextIata, Double.MAX_VALUE)) {
          distances.put(nextIata, newWeight);

          List<Flight> newPath = new ArrayList<>(currentPath);
          newPath.add(flight);

          pq.offer(new DijkstraNode(newWeight, nextIata, newPath));
        }
      }
    }

    // No route found
    return null;
  }

  /**
   * Calculates total duration of a flight path including stopover times.
   *
   * @param flights List of flights
   * @return Total duration in minutes
   */
  private int calculateTotalDuration(List<Flight> flights) {
    if (flights.isEmpty()) {
      return 0;
    }

    int flightDuration = flights.stream()
        .mapToInt(Flight::getDuration)
        .sum();
    int stopovers = flights.size() - 1;
    return flightDuration + (stopovers * Route.STOPOVER_TIME);
  }

  /**
   * Functional interface for calculating edge weights in Dijkstra's algorithm.
   */
  @FunctionalInterface
  private interface WeightFunction {
    double getWeight(Flight flight);
  }

  /**
   * Helper class for Dijkstra's algorithm priority queue.
   */
  private static class DijkstraNode {
    double weight;
    String airportIata;
    List<Flight> path;

    DijkstraNode(double weight, String airportIata, List<Flight> path) {
      this.weight = weight;
      this.airportIata = airportIata;
      this.path = path;
    }
  }
}
