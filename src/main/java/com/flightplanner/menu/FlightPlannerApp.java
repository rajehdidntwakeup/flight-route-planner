package com.flightplanner.menu;

import com.flightplanner.algorithm.RouteComparators;
import com.flightplanner.algorithm.RouteFinder;
import com.flightplanner.algorithm.RouteSorter;
import com.flightplanner.graph.FlightGraph;
import com.flightplanner.model.Flight;
import com.flightplanner.model.Route;
import com.flightplanner.search.FlightSearch;
import com.flightplanner.util.DataLoader;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main application class for the Flight Route Planner.
 * Provides an interactive console menu for all functionality.
 */
public class FlightPlannerApp {
    private final Scanner scanner;
    private final DataLoader dataLoader;
    private RouteFinder routeFinder;
    private FlightSearch flightSearch;

    public FlightPlannerApp() {
        this.scanner = new Scanner(System.in);
        this.dataLoader = new DataLoader();
    }

    /**
     * Main application loop.
     */
    public void start() {
        printWelcome();

        boolean running = true;
        while (running) {
            try {
                printMainMenu();
                int choice = getIntInput("Enter your choice: ");

                switch (choice) {
                    case 1 -> handleRouteCalculation();
                    case 2 -> handleReadAirports();
                    case 3 -> handleReadFlights();
                    case 4 -> handleReadRoutes();
                    case 5 -> handleSaveRoutes();
                    case 6 -> handleSorting();
                    case 7 -> handleSearch();
                    case 8 -> {
                        System.out.println("\nThank you for using Flight Route Planner. Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid choice. Please enter a number between 1 and 8.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
            }
        }

        scanner.close();
    }

    /**
     * Prints welcome message.
     */
    private void printWelcome() {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║       FLIGHT ROUTE PLANNER - Java 24 Application       ║");
        System.out.println("║     Graph-Based Route Optimization & Search System     ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * Prints the main menu.
     */
    private void printMainMenu() {
        System.out.println("\n═══════════════ MAIN MENU ═══════════════");
        System.out.println("1. Route Calculation");
        System.out.println("2. Read Airports from CSV");
        System.out.println("3. Read Flights from CSV");
        System.out.println("4. Read Routes from CSV");
        System.out.println("5. Save Routes to CSV");
        System.out.println("6. Sorting");
        System.out.println("7. Search");
        System.out.println("8. Exit");
        System.out.println("═════════════════════════════════════════");
    }

    /**
     * Handles route calculation functionality.
     */
    private void handleRouteCalculation() {
        if (routeFinder == null) {
            System.out.println("\nError: Please load airports and flights first (options 2 and 3).");
            return;
        }

        System.out.println("\n─── Route Calculation ───");
        String origin = getStringInput("Enter origin IATA code (e.g., VIE): ").toUpperCase();
        String destination = getStringInput("Enter destination IATA code (e.g., JFK): ").toUpperCase();

        System.out.println("\nSelect criterion:");
        System.out.println("1. Cheapest");
        System.out.println("2. Fastest");
        System.out.println("3. Fewest Stopovers");
        System.out.println("4. Slowest");
        int criterion = getIntInput("Enter criterion (1-4): ");

        System.out.println("\nSearching for route...");
        Route result = routeFinder.findRoute(origin, destination, criterion);

        if (result == null) {
            System.out.println("No route found between " + origin + " and " + destination);
        } else {
            System.out.println("\n" + result.toDetailedString(getFlightsForRoute(result)));
        }
    }

    /**
     * Handles reading airports from CSV.
     */
    private void handleReadAirports() {
        System.out.println("\n─── Read Airports from CSV ───");
        String filename = getStringInput("Enter CSV filename (default: airports.csv): ");
        if (filename.isEmpty()) {
            filename = "airports.csv";
        }

        try {
            dataLoader.readAirportsFromCSV(filename);
            System.out.println("✓ Airports loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error reading airports: " + e.getMessage());
        }
    }

    /**
     * Handles reading flights from CSV.
     */
    private void handleReadFlights() {
        if (dataLoader.getAirports().isEmpty()) {
            System.out.println("\nError: Please load airports first (option 2).");
            return;
        }

        System.out.println("\n─── Read Flights from CSV ───");
        String filename = getStringInput("Enter CSV filename (default: flights.csv): ");
        if (filename.isEmpty()) {
            filename = "flights.csv";
        }

        try {
            dataLoader.readFlightsFromCSV(filename);
          FlightGraph graph = dataLoader.buildGraph();
            routeFinder = new RouteFinder(graph);
            flightSearch = new FlightSearch(new ArrayList<>(dataLoader.getFlights().values()), 
                                           dataLoader.getAirports());
            System.out.println("✓ Flights loaded successfully.");
            System.out.println("✓ Graph built: " + graph.toString());
        } catch (IOException e) {
            System.err.println("Error reading flights: " + e.getMessage());
        }
    }

    /**
     * Handles reading routes from CSV.
     */
    private void handleReadRoutes() {
        if (dataLoader.getFlights().isEmpty()) {
            System.out.println("\nError: Please load flights first (option 3).");
            return;
        }

        System.out.println("\n─── Read Routes from CSV ───");
        String filename = getStringInput("Enter CSV filename (default: routes.csv): ");
        if (filename.isEmpty()) {
            filename = "routes.csv";
        }

        try {
            dataLoader.readRoutesFromCSV(filename);
            System.out.println("✓ Routes loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error reading routes: " + e.getMessage());
        }
    }

    /**
     * Handles saving routes to CSV.
     */
    private void handleSaveRoutes() {
        if (dataLoader.getRoutes().isEmpty()) {
            System.out.println("\nError: No routes to save. Load or calculate routes first.");
            return;
        }

        System.out.println("\n─── Save Routes to CSV ───");
        String filename = getStringInput("Enter CSV filename (default: routes_output.csv): ");
        if (filename.isEmpty()) {
            filename = "routes_output.csv";
        }

        try {
            List<Route> routeList = new ArrayList<>(dataLoader.getRoutes().values());
            dataLoader.saveRoutesToCSV(filename, routeList);
            System.out.println("✓ Routes saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving routes: " + e.getMessage());
        }
    }

    /**
     * Handles sorting functionality.
     */
    private void handleSorting() {
        if (dataLoader.getRoutes().isEmpty()) {
            System.out.println("\nError: No routes available. Load routes first (option 4).");
            return;
        }

        System.out.println("\n─── Sorting ───");
        String routeIdsInput = getStringInput("Enter route IDs (comma-separated, or 'all' for all routes): ");

        List<Route> routesToSort;
        if (routeIdsInput.equalsIgnoreCase("all")) {
            routesToSort = new ArrayList<>(dataLoader.getRoutes().values());
        } else {
            routesToSort = parseRouteIds(routeIdsInput);
            if (routesToSort.isEmpty()) {
                System.out.println("No valid routes found.");
                return;
            }
        }

        System.out.println("\nChoose sorting algorithm:");
        System.out.println("1. Stable Sort (Merge Sort)");
        System.out.println("2. Unstable Sort (Quick Sort)");
        int algo = getIntInput("Enter algorithm (1-2): ");

        System.out.println("\nChoose comparator:");
        System.out.println("1. Price");
        System.out.println("2. Duration");
        System.out.println("3. Stopovers");
        System.out.println("4. Combined (Price → Duration → Stopovers)");
        int comp = getIntInput("Enter comparator (1-4): ");

        Comparator<Route> comparator = RouteComparators.getComparator(comp);
        if (comparator == null) {
            System.out.println("Invalid comparator choice.");
            return;
        }

        System.out.println("\nSorting routes using " + 
                         (algo == 1 ? "Merge Sort" : "Quick Sort") + 
                         " with " + RouteComparators.getComparatorName(comp) + " comparator...");

        if (algo == 1) {
            RouteSorter.stableSort(routesToSort, comparator);
        } else {
            RouteSorter.unstableSort(routesToSort, comparator);
        }

        System.out.println("\n✓ Sorted Routes:");
        System.out.println("─────────────────────────────────────────────────────────");
        for (int i = 0; i < routesToSort.size(); i++) {
            System.out.println((i + 1) + ". " + routesToSort.get(i).toString());
        }
    }

    /**
     * Handles search functionality.
     */
    private void handleSearch() {
        if (flightSearch == null) {
            System.out.println("\nError: Please load flights first (option 3).");
            return;
        }

        System.out.println("\n─── Search ───");
        System.out.println("Search by:");
        System.out.println("1. Origin");
        System.out.println("2. Destination");
        System.out.println("3. Airline");
        System.out.println("4. Flight Number");
        int searchType = getIntInput("Enter search type (1-4): ");

        String query = getStringInput("Enter search term: ");

        FlightSearch.SearchResult result = switch (searchType) {
            case 1 -> flightSearch.searchByOrigin(query);
            case 2 -> flightSearch.searchByDestination(query);
            case 3 -> flightSearch.searchByAirline(query);
            case 4 -> flightSearch.searchByFlightNumber(query);
            default -> {
                System.out.println("Invalid search type.");
                yield null;
            }
        };

        if (result != null) {
            System.out.println("\n" + result.toString());
        }
    }

    /**
     * Parses route IDs from comma-separated input.
     */
    private List<Route> parseRouteIds(String input) {
        List<Route> routes = new ArrayList<>();
        String[] parts = input.split(",");

        for (String part : parts) {
            try {
                int id = Integer.parseInt(part.trim());
                Route route = dataLoader.getRoutes().get(id);
                if (route != null) {
                    routes.add(route);
                } else {
                    System.out.println("Warning: Route ID " + id + " not found.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Warning: Invalid route ID: " + part);
            }
        }

        return routes;
    }

    /**
     * Gets flights for a route.
     */
    private List<Flight> getFlightsForRoute(Route route) {
        return route.getFlightIds().stream()
                .map(id -> dataLoader.getFlights().get(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Gets integer input from user with validation.
     */
    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        return value;
    }

    /**
     * Gets string input from the user.
     */
    private String getStringInput(String input) {
        System.out.print(input);
        return scanner.nextLine().trim();
    }
}
