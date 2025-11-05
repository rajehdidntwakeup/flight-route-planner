package com.flightplanner.model;

/**
 * Represents an airport with its identifying information and geographic coordinates.
 * This class is used as a node in the flight route graph.
 */
public class Airport {
    private final int id;
    private final String iata;
    private final String city;
    private final String country;
    private final double latitude;
    private final double longitude;

    /**
     * Constructs an Airport with the specified details.
     *
     * @param id        Unique identifier for the airport
     * @param iata      Three-letter IATA code (e.g., "VIE", "JFK")
     * @param city      City where the airport is located
     * @param country   Country where the airport is located
     * @param latitude  Geographic latitude coordinate
     * @param longitude Geographic longitude coordinate
     */
    public Airport(int id, String iata, String city, String country, double latitude, double longitude) {
        this.id = id;
        this.iata = iata;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getIata() {
        return iata;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s, %s [ID: %d, Coords: %.2f, %.2f]",
                iata, city, country, city, id, latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return id == airport.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
