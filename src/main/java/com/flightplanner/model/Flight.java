package com.flightplanner.model;

import java.time.LocalTime;

/**
 * Represents a flight between two airports.
 * This class is used as an edge in the flight route graph.
 */
public class Flight {
    private final int id;
    private final String origin;
    private final String destination;
    private final String airline;
    private final String flightNumber;
    private final int duration; // in minutes
    private final double price;
    private final LocalTime departureTime;

    /**
     * Constructs a Flight with the specified details.
     *
     * @param id            Unique identifier for the flight
     * @param origin        IATA code of the origin airport
     * @param destination   IATA code of the destination airport
     * @param airline       Name of the airline operating the flight
     * @param flightNumber  Flight number (e.g., "AA100")
     * @param duration      Flight duration in minutes
     * @param price         Price of the flight
     * @param departureTime Departure time of the flight
     */
    public Flight(int id, String origin, String destination, String airline, 
                  String flightNumber, int duration, double price, LocalTime departureTime) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.airline = airline;
        this.flightNumber = flightNumber;
        this.duration = duration;
        this.price = price;
        this.departureTime = departureTime;
    }

    public int getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getAirline() {
        return airline;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public int getDuration() {
        return duration;
    }

    public double getPrice() {
        return price;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    @Override
    public String toString() {
        return String.format("Flight %s [ID: %d] - %s → %s | %s | Duration: %d min | Price: $%.2f | Departs: %s",
                flightNumber, id, origin, destination, airline, duration, price, departureTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return id == flight.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
