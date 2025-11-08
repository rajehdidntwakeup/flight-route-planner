package com.flightplanner.search;

import com.flightplanner.model.Airport;
import com.flightplanner.model.Flight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides linear search functionality for flights.
 * Supports searching by origin, destination, airline, and flight number.
 */
public class FlightSearch {
    private final List<Flight> flights;
    private final Map<String, Airport> airports;

    /**
     * Constructs a FlightSearch with the given flights and airports.
     *
     * @param flights  List of all flights
     * @param airports Map of IATA codes to Airport objects
     */
    public FlightSearch(List<Flight> flights, Map<String, Airport> airports) {
        this.flights = flights;
        this.airports = airports;
    }

    /**
     * Searches for flights by origin airport IATA code.
     * Uses linear search to find all matching flights.
     *
     * @param iata IATA code of the origin airport
     * @return SearchResult containing the airport and matching flights
     */
    public SearchResult searchByOrigin(String iata) {
        Airport airport = airports.get(iata.toUpperCase());
        List<Flight> matchingFlights = new ArrayList<>();

        // Linear search through all flights
        for (Flight flight : flights) {
            if (flight.getOrigin().equalsIgnoreCase(iata)) {
                matchingFlights.add(flight);
            }
        }

        return new SearchResult(airport, matchingFlights, "Origin: " + iata);
    }

    /**
     * Searches for flights by destination airport IATA code.
     * Uses linear search to find all matching flights.
     *
     * @param iata IATA code of the destination airport
     * @return SearchResult containing the airport and matching flights
     */
    public SearchResult searchByDestination(String iata) {
        Airport airport = airports.get(iata.toUpperCase());
        List<Flight> matchingFlights = new ArrayList<>();

        // Linear search through all flights
        for (Flight flight : flights) {
            if (flight.getDestination().equalsIgnoreCase(iata)) {
                matchingFlights.add(flight);
            }
        }

        return new SearchResult(airport, matchingFlights, "Destination: " + iata);
    }

    /**
     * Searches for flights by airline name.
     * Uses linear search with case-insensitive partial matching.
     *
     * @param airline Airline name (or partial name)
     * @return SearchResult containing matching flights
     */
    public SearchResult searchByAirline(String airline) {
        List<Flight> matchingFlights = new ArrayList<>();

        // Linear search through all flights
        for (Flight flight : flights) {
            if (flight.getAirline().toLowerCase().contains(airline.toLowerCase())) {
                matchingFlights.add(flight);
            }
        }

        return new SearchResult(null, matchingFlights, "Airline: " + airline);
    }

    /**
     * Searches for flights by flight number.
     * Uses linear search with case-insensitive exact matching.
     *
     * @param flightNumber Flight number to search for
     * @return SearchResult containing matching flights
     */
    public SearchResult searchByFlightNumber(String flightNumber) {
        List<Flight> matchingFlights = new ArrayList<>();

        // Linear search through all flights
        for (Flight flight : flights) {
            if (flight.getFlightNumber().equalsIgnoreCase(flightNumber)) {
                matchingFlights.add(flight);
            }
        }

        return new SearchResult(null, matchingFlights, "Flight Number: " + flightNumber);
    }

    /**
     * Container class for search results.
     */
    public static class SearchResult {
        private final Airport airport;
        private final List<Flight> flights;
        private final String searchCriteria;

        public SearchResult(Airport airport, List<Flight> flights, String searchCriteria) {
            this.airport = airport;
            this.flights = flights;
            this.searchCriteria = searchCriteria;
        }

        public Airport getAirport() {
            return airport;
        }

        public List<Flight> getFlights() {
            return flights;
        }

        public String getSearchCriteria() {
            return searchCriteria;
        }

        public boolean hasResults() {
            return !flights.isEmpty();
        }

        public int getResultCount() {
            return flights.size();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Search Results ===\n");
            sb.append("Search Criteria: ").append(searchCriteria).append("\n");
            
            if (airport != null) {
                sb.append("Airport: ").append(airport.toString()).append("\n");
            }
            
            sb.append("Found ").append(flights.size()).append(" flight(s)\n");
            sb.append("─────────────────────────────────────────────────────────\n");
            
            if (flights.isEmpty()) {
                sb.append("No flights found matching the criteria.\n");
            } else {
                for (int i = 0; i < flights.size(); i++) {
                    sb.append(String.format("%d. %s\n", i + 1, flights.get(i).toString()));
                }
            }
            return sb.toString();
        }
    }
}
