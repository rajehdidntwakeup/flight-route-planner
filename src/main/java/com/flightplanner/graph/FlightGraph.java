package com.flightplanner.graph;

import com.flightplanner.model.Airport;
import com.flightplanner.model.Flight;

import java.util.*;

/**
 * Models a graph representing flights between airports, where airports are nodes
 * and flights are directed edges. The graph supports operations for adding airports
 * and flights, retrieving flights from specific airports, and querying airports by
 * their IATA codes.
 */
public class FlightGraph {
  // Adjacency list: Airport -> List of outgoing flights
  private final Map<Airport, List<Flight>> airportFlightsMap;

  // Quick lookup maps
  private final Map<String, Airport> airportsByIata;

  /**
   * Constructs an empty FlightGraph.
   */
  public FlightGraph() {
    this.airportFlightsMap = new HashMap<>();
    this.airportsByIata = new HashMap<>();
  }

  /**
   * Adds an airport (node) to the graph.
   *
   * @param airport The airport to add
   */
  public void addAirport(Airport airport) {
    airportFlightsMap.putIfAbsent(airport, new ArrayList<>());
    airportsByIata.put(airport.getIata(), airport);
  }

  /**
   * Adds a flight (edge) to the graph.
   * Automatically adds origin and destination airports if they don't exist.
   *
   * @param flight        The flight to add
   * @param originAirport The origin airport object
   */
  public void addFlight(Flight flight, Airport originAirport) {
    if (originAirport == null) {
      throw new IllegalArgumentException("Origin airport cannot be null for flight: " + flight.getId());
    }

    addAirport(originAirport);
    airportFlightsMap.get(originAirport).add(flight);
  }

  /**
   * Gets all outgoing flights from a specific airport.
   *
   * @param airport The airport to query
   * @return List of flights departing from the airport, or empty list if none
   */
  public List<Flight> getFlightsFrom(Airport airport) {
    return airportFlightsMap.getOrDefault(airport, new ArrayList<>());
  }

  /**
   * Gets all outgoing flights from an airport by IATA code.
   *
   * @param iata The IATA code of the airport
   * @return List of flights departing from the airport, or empty list if none
   */
  public List<Flight> getFlightsFrom(String iata) {
    Airport airport = airportsByIata.get(iata);
    if (airport == null) {
      return new ArrayList<>();
    }
    return getFlightsFrom(airport);
  }

  /**
   * Gets an airport by its IATA code.
   *
   * @param iata The IATA code
   * @return The Airport object, or null if not found
   */
  public Airport getAirport(String iata) {
    return airportsByIata.get(iata);
  }


  /**
   * Gets all flights in the graph.
   *
   * @return List of all flights
   */
  public List<Flight> getAllFlights() {
    List<Flight> allFlights = new ArrayList<>();
    for (List<Flight> flights : airportFlightsMap.values()) {
      allFlights.addAll(flights);
    }
    return allFlights;
  }

  /**
   * Gets the number of airports in the graph.
   *
   * @return Number of airports
   */
  public int getAirportCount() {
    return airportFlightsMap.size();
  }

  /**
   * Gets the number of flights in the graph.
   *
   * @return Number of flights
   */
  public int getFlightCount() {
    return getAllFlights().size();
  }

  @Override
  public String toString() {
    return String.format("FlightGraph: %d airports, %d flights", getAirportCount(), getFlightCount());
  }
}
