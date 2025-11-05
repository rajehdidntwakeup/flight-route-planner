package com.flightplanner.model;

import java.util.List;

/**
 * Represents a complete route consisting of one or more flights.
 * Automatically calculates total duration, total price, and number of stopovers.
 * Includes a minimum 20-minute stopover time between connecting flights.
 */
public class Route {
    private final int id;
    private final List<Integer> flightIds;
    private final int totalDuration; // in minutes, including stopover times
    private final double totalPrice;
    private final int stopovers;
    
    // Minimum stopover time between connecting flights (in minutes)
    public static final int STOPOVER_TIME = 20;

    /**
     * Constructs a Route and automatically calculates totals.
     *
     * @param id        Unique identifier for the route
     * @param flightIds List of flight IDs that make up this route
     * @param flights   List of Flight objects corresponding to the flight IDs
     */
    public Route(int id, List<Integer> flightIds, List<Flight> flights) {
        this.id = id;
        this.flightIds = flightIds;
        this.stopovers = Math.max(0, flights.size() - 1);
        
        // Calculate total price
        this.totalPrice = flights.stream()
                .mapToDouble(Flight::getPrice)
                .sum();
        
        // Calculate total duration including stopover times
        int flightDuration = flights.stream()
                .mapToInt(Flight::getDuration)
                .sum();
        this.totalDuration = flightDuration + (stopovers * STOPOVER_TIME);
    }

    /**
     * Constructs a Route with pre-calculated values (used when loading from CSV).
     *
     * @param id            Unique identifier for the route
     * @param flightIds     List of flight IDs that make up this route
     * @param totalDuration Total duration in minutes
     * @param totalPrice    Total price
     * @param stopovers     Number of stopovers
     */
    public Route(int id, List<Integer> flightIds, int totalDuration, double totalPrice, int stopovers) {
        this.id = id;
        this.flightIds = flightIds;
        this.totalDuration = totalDuration;
        this.totalPrice = totalPrice;
        this.stopovers = stopovers;
    }

    public int getId() {
        return id;
    }

    public List<Integer> getFlightIds() {
        return flightIds;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getStopovers() {
        return stopovers;
    }

    /**
     * Formats duration in hours and minutes.
     *
     * @return Formatted string (e.g., "5h 30m")
     */
    public String getFormattedDuration() {
        int hours = totalDuration / 60;
        int minutes = totalDuration % 60;
        return String.format("%dh %dm", hours, minutes);
    }

    @Override
    public String toString() {
        return String.format("Route %d: Flights %s | Duration: %s | Price: $%.2f | Stopovers: %d",
                id, flightIds, getFormattedDuration(), totalPrice, stopovers);
    }

    /**
     * Returns a detailed string representation with flight information.
     *
     * @param flights List of Flight objects in this route
     * @return Detailed route description
     */
    public String toDetailedString(List<Flight> flights) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== Route %d ===\n", id));
        sb.append(String.format("Total Duration: %s | Total Price: $%.2f | Stopovers: %d\n\n",
                getFormattedDuration(), totalPrice, stopovers));
        
        for (int i = 0; i < flights.size(); i++) {
            Flight f = flights.get(i);
            sb.append(String.format("Flight %d: %s\n", i + 1, f.toString()));
            if (i < flights.size() - 1) {
                sb.append(String.format("  → Stopover: %d minutes\n", STOPOVER_TIME));
            }
        }
        
        return sb.toString();
    }
}
