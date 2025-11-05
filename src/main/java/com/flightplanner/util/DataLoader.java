package com.flightplanner.util;

import com.flightplanner.graph.FlightGraph;
import com.flightplanner.model.Airport;
import com.flightplanner.model.Flight;
import com.flightplanner.model.Route;

import java.io.*;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles loading and saving data from/to CSV files.
 * Reads airports, flights, and routes from CSV files in src/main/resources/.
 */
public class DataLoader {
    private final Map<String, Airport> airports;
    private Map<Integer, Flight> flights;
    private Map<Integer, Route> routes;
    private FlightGraph graph;

    public DataLoader() {
        this.airports = new HashMap<>();
        this.flights = new HashMap<>();
        this.routes = new HashMap<>();
        this.graph = new FlightGraph();
    }

    /**
     * Reads airports from a CSV file.
     * Expected format: id,iata,city,country,latitude,longitude
     *
     * @param filename Name of the CSV file in resources directory
     * @return Map of IATA code to Airport
     * @throws IOException If file reading fails
     */
    public Map<String, Airport> readAirportsFromCSV(String filename) throws IOException {
        airports.clear();
        
        try (BufferedReader br = getResourceReader(filename)) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                // Skip header line
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String iata = parts[1].trim();
                        String city = parts[2].trim();
                        String country = parts[3].trim();
                        double latitude = Double.parseDouble(parts[4].trim());
                        double longitude = Double.parseDouble(parts[5].trim());
                        
                        Airport airport = new Airport(id, iata, city, country, latitude, longitude);
                        airports.put(iata, airport);
                        graph.addAirport(airport);
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Skipping invalid airport line: " + line);
                    }
                }
            }
        }
        
        System.out.println("Loaded " + airports.size() + " airports from " + filename);
        return airports;
    }

    /**
     * Reads flights from a CSV file.
     * Expected format: id,origin,destination,airline,flightNumber,duration,price,departureTime
     *
     * @param filename Name of the CSV file in resources directory
     * @return List of Flight objects
     * @throws IOException If file reading fails
     */
    public List<Flight> readFlightsFromCSV(String filename) throws IOException {
        flights.clear();
        List<Flight> flightList = new ArrayList<>();
        
        try (BufferedReader br = getResourceReader(filename)) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                // Skip header line
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String origin = parts[1].trim();
                        String destination = parts[2].trim();
                        String airline = parts[3].trim();
                        String flightNumber = parts[4].trim();
                        int duration = Integer.parseInt(parts[5].trim());
                        double price = Double.parseDouble(parts[6].trim());
                        LocalTime departureTime = LocalTime.parse(parts[7].trim());
                        
                        // Validate that origin and destination airports exist
                        if (!airports.containsKey(origin)) {
                            System.err.println("Warning: Origin airport " + origin + " not found for flight " + id);
                            continue;
                        }
                        if (!airports.containsKey(destination)) {
                            System.err.println("Warning: Destination airport " + destination + " not found for flight " + id);
                            continue;
                        }
                        
                        Flight flight = new Flight(id, origin, destination, airline, flightNumber, 
                                                   duration, price, departureTime);
                        flights.put(id, flight);
                        flightList.add(flight);
                        
                        // Add to graph
                        graph.addFlight(flight, airports.get(origin));
                    } catch (Exception e) {
                        System.err.println("Warning: Skipping invalid flight line: " + line + " - " + e.getMessage());
                    }
                }
            }
        }
        
        System.out.println("Loaded " + flightList.size() + " flights from " + filename);
        return flightList;
    }

    /**
     * Reads routes from a CSV file.
     * Expected format: id,flightIds (separated by hyphens, e.g., "1-47-18")
     *
     * @param filename Name of the CSV file in resources directory
     * @return List of Route objects
     * @throws IOException If file reading fails
     */
    public List<Route> readRoutesFromCSV(String filename) throws IOException {
        routes.clear();
        List<Route> routeList = new ArrayList<>();
        
        try (BufferedReader br = getResourceReader(filename)) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                // Skip header line
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String[] flightIdStrings = parts[1].trim().split("-");
                        
                        List<Integer> flightIds = new ArrayList<>();
                        List<Flight> routeFlights = new ArrayList<>();
                        
                        for (String fidStr : flightIdStrings) {
                            int fid = Integer.parseInt(fidStr.trim());
                            flightIds.add(fid);
                            
                            Flight flight = flights.get(fid);
                            if (flight == null) {
                                System.err.println("Warning: Flight " + fid + " not found for route " + id);
                                continue;
                            }
                            routeFlights.add(flight);
                        }
                        
                        if (!routeFlights.isEmpty()) {
                            Route route = new Route(id, flightIds, routeFlights);
                            routes.put(id, route);
                            routeList.add(route);
                        }
                    } catch (Exception e) {
                        System.err.println("Warning: Skipping invalid route line: " + line + " - " + e.getMessage());
                    }
                }
            }
        }
        
        System.out.println("Loaded " + routeList.size() + " routes from " + filename);
        return routeList;
    }

    /**
     * Saves routes to a CSV file.
     * Format: id,flightIds (separated by hyphens)
     *
     * @param filename Name of the CSV file in resources directory
     * @param routes   List of routes to save
     * @throws IOException If file writing fails
     */
    public void saveRoutesToCSV(String filename, List<Route> routes) throws IOException {
        String resourcePath = "src/main/resources/" + filename;
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(resourcePath))) {
            // Write header
            bw.write("id,flightIds");
            bw.newLine();
            
            // Write routes
            for (Route route : routes) {
                String flightIdsStr = route.getFlightIds().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining("-"));
                bw.write(route.getId() + "," + flightIdsStr);
                bw.newLine();
            }
        }
        
        System.out.println("Saved " + routes.size() + " routes to " + filename);
    }

    /**
     * Builds the flight graph from loaded data.
     *
     * @return The constructed FlightGraph
     */
    public FlightGraph buildGraph() {
        return graph;
    }

    /**
     * Gets a BufferedReader for a resource file.
     *
     * @param filename Name of the resource file
     * @return BufferedReader for the file
     * @throws IOException If file is not found
     */
    private BufferedReader getResourceReader(String filename) throws IOException {
        // Try to read from resources directory
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        
        // If not found in classpath, try reading from src/main/resources/
        if (is == null) {
            String resourcePath = "src/main/resources/" + filename;
            File file = new File(resourcePath);
            if (file.exists()) {
                is = new FileInputStream(file);
            } else {
                throw new IOException("Resource file not found: " + filename);
            }
        }
        
        return new BufferedReader(new InputStreamReader(is));
    }

    // Getters
    public Map<String, Airport> getAirports() {
        return airports;
    }

    public Map<Integer, Flight> getFlights() {
        return flights;
    }

    public Map<Integer, Route> getRoutes() {
        return routes;
    }

    public FlightGraph getGraph() {
        return graph;
    }
}
