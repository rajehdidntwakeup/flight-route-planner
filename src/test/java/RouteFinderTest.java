import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import at.hochschule.burgenland.bswe.algo.algorithm.RouteFinder;
import at.hochschule.burgenland.bswe.algo.model.Airport;
import at.hochschule.burgenland.bswe.algo.model.Flight;
import at.hochschule.burgenland.bswe.algo.model.Route;
import at.hochschule.burgenland.bswe.algo.model.graph.FlightGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RouteFinderTest {

    private FlightGraph graph;
    private RouteFinder routeFinder;

    private Airport vie; // Vienna
    private Airport jfk; // New York
    private Airport lhr; // London
    private Airport cdg; // Paris

    @BeforeEach
    void setUp() {
        graph = new FlightGraph();

        vie = new Airport(1, "VIE", "Vienna", "Austria", 48.11, 16.56);
        jfk = new Airport(2, "JFK", "New York", "USA", 40.64, -73.78);
        lhr = new Airport(3, "LHR", "London", "UK", 51.47, -0.45);
        cdg = new Airport(4, "CDG", "Paris", "France", 49.00, 2.55);

        graph.addAirport(vie);
        graph.addAirport(jfk);
        graph.addAirport(lhr);
        graph.addAirport(cdg);

        // Direct flight VIE -> JFK: Expensive, Fast
        graph.addFlight(new Flight(1, "VIE", "JFK", "OS", "OS1", 540, 800.0, LocalTime.of(10, 0)), vie);

        // Multi-leg: VIE -> LHR -> JFK: Cheaper, Slower
        graph.addFlight(new Flight(2, "VIE", "LHR", "BA", "BA1", 120, 150.0, LocalTime.of(11, 0)), vie);
        graph.addFlight(new Flight(3, "LHR", "JFK", "BA", "BA2", 480, 400.0, LocalTime.of(15, 0)), lhr);

        // Another multi-leg: VIE -> CDG -> LHR -> JFK: Most stopovers, Slowest
        graph.addFlight(new Flight(4, "VIE", "CDG", "AF", "AF1", 100, 100.0, LocalTime.of(8, 0)), vie);
        graph.addFlight(new Flight(5, "CDG", "LHR", "AF", "AF2", 60, 80.0, LocalTime.of(12, 0)), cdg);

        routeFinder = new RouteFinder(graph);
    }

    @Test
    void testFindCheapestRoute() {
        // VIE -> JFK
        // Option 1: OS1 (800)
        // Option 2: BA1 + BA2 (150 + 400 = 550)
        // Option 3: AF1 + AF2 + BA2 (100 + 80 + 400 = 580)
        Route route = routeFinder.findRoute("VIE", "JFK", RouteFinder.CRITERION_CHEAPEST);
        assertNotNull(route);
        assertEquals(2, route.getFlightIds().size());
        assertTrue(route.getFlightIds().contains(2));
        assertTrue(route.getFlightIds().contains(3));
    }

    @Test
    void testFindFastestRoute() {
        // Duration includes 20 min stopover
        // Option 1: OS1 (540)
        // Option 2: BA1 + BA2 (120 + 20 + 480 = 620)
        // Option 3: AF1 + AF2 + BA2 (100 + 20 + 60 + 20 + 480 = 680)
        Route route = routeFinder.findRoute("VIE", "JFK", RouteFinder.CRITERION_FASTEST);
        assertNotNull(route);
        assertEquals(1, route.getFlightIds().size());
        assertEquals(1, route.getFlightIds().get(0));
    }

    @Test
    void testFindFewestStopoversRoute() {
        Route route = routeFinder.findRoute("VIE", "JFK", RouteFinder.CRITERION_FEWEST_STOPOVERS);
        assertNotNull(route);
        assertEquals(1, route.getFlightIds().size());
        assertEquals(1, route.getFlightIds().get(0));
    }

    @Test
    void testFindSlowestRoute() {
        // Slowest route is VIE -> CDG -> LHR -> JFK
        Route route = routeFinder.findRoute("VIE", "JFK", RouteFinder.CRITERION_SLOWEST);
        assertNotNull(route);
        assertEquals(3, route.getFlightIds().size());
        assertEquals(List.of(4, 5, 3), route.getFlightIds());
    }

    @Test
    void testFindRouteInvalidAirports() {
        assertNull(routeFinder.findRoute("XYZ", "VIE", RouteFinder.CRITERION_CHEAPEST));
        assertNull(routeFinder.findRoute("VIE", "XYZ", RouteFinder.CRITERION_CHEAPEST));
        assertNull(routeFinder.findRoute(null, "VIE", RouteFinder.CRITERION_CHEAPEST));
    }

    @Test
    void testFindRouteNoPath() {
        // JFK has no outgoing flights in our setup
        assertNull(routeFinder.findRoute("JFK", "VIE", RouteFinder.CRITERION_CHEAPEST));
    }

    @Test
    void testFindRouteInvalidCriterion() {
        assertNull(routeFinder.findRoute("VIE", "JFK", 99));
    }

    @Test
    void testFindRouteSameOriginAndDestination() {
        // VIE to VIE
        // In Dijkstra implementation, if currentIata.equals(destination.getIata()), it returns the route.
        // For same origin and destination, it should probably return an empty route or null depending on implementation.
        // Looking at RouteFinder.java:195, priorityQueue starts with origin.
        // At 205, it checks if currentIata equals destination.
        // It should return a Route with 0 flights.
        Route route = routeFinder.findRoute("VIE", "VIE", RouteFinder.CRITERION_CHEAPEST);
        assertNotNull(route);
        assertEquals(0, route.getFlightIds().size());
    }

    @Test
    void testSlowestRouteAtStopoverLimit() {
        // MAX_STOPOVERS is 3, which means 4 flights total.
        // Create a path with exactly 4 flights: VIE -> CDG -> LHR -> JFK -> VIE (wait, JFK has no outgoing)
        // Let's add a flight JFK -> VIE to make it possible to have more legs.
        Airport dummy = new Airport(5, "DUM", "Dummy", "Dummy", 0, 0);
        graph.addAirport(dummy);
        // VIE (4) -> CDG (5) -> LHR (3) -> JFK (2) -> DUM (none)
        // Current setup:
        // 4: VIE -> CDG
        // 5: CDG -> LHR
        // 3: LHR -> JFK
        // Let's add JFK -> DUM
        graph.addFlight(new Flight(6, "JFK", "DUM", "XX", "XX1", 100, 100.0, LocalTime.of(20, 0)), jfk);

        // Path VIE -> CDG -> LHR -> JFK -> DUM has 4 flights (3 stopovers).
        Route route = routeFinder.findRoute("VIE", "DUM", RouteFinder.CRITERION_SLOWEST);
        assertNotNull(route);
        assertEquals(4, route.getFlightIds().size());
        assertEquals(List.of(4, 5, 3, 6), route.getFlightIds());
    }

    @Test
    void testSlowestRouteExceedingStopoverLimit() {
        // MAX_STOPOVERS is 3 (4 flights max).
        Airport d1 = new Airport(5, "D1", "D1", "D", 0, 0);
        Airport d2 = new Airport(6, "D2", "D2", "D", 0, 0);
        Airport d3 = new Airport(7, "D3", "D3", "D", 0, 0);
        Airport d4 = new Airport(8, "D4", "D4", "D", 0, 0);
        Airport d5 = new Airport(9, "D5", "D5", "D", 0, 0);
        graph.addAirport(d1);
        graph.addAirport(d2);
        graph.addAirport(d3);
        graph.addAirport(d4);
        graph.addAirport(d5);

        // Path: VIE -> D1 -> D2 -> D3 -> D4 -> D5 (5 segments, 4 stopovers)
        graph.addFlight(new Flight(10, "VIE", "D1", "XX", "X1", 100, 100.0, LocalTime.of(1, 0)), vie);
        graph.addFlight(new Flight(11, "D1", "D2", "XX", "X2", 100, 100.0, LocalTime.of(2, 0)), d1);
        graph.addFlight(new Flight(12, "D2", "D3", "XX", "X3", 100, 100.0, LocalTime.of(3, 0)), d2);
        graph.addFlight(new Flight(13, "D3", "D4", "XX", "X4", 100, 100.0, LocalTime.of(4, 0)), d3);
        graph.addFlight(new Flight(14, "D4", "D5", "XX", "X5", 100, 100.0, LocalTime.of(5, 0)), d4);

        // D5 is only reachable via 5 segments. Limit is 4.
        Route route = routeFinder.findRoute("VIE", "D5", RouteFinder.CRITERION_SLOWEST);
        assertNull(route);
    }

    @Test
    void testCycleInGraph() {
        // Add a cycle: JFK -> VIE
        graph.addFlight(new Flight(6, "JFK", "VIE", "OS", "OS2", 500, 500.0, LocalTime.of(22, 0)), jfk);

        // Cheapest route VIE -> JFK should still be the same
        Route route = routeFinder.findRoute("VIE", "JFK", RouteFinder.CRITERION_CHEAPEST);
        assertNotNull(route);
        assertEquals(2, route.getFlightIds().size());

        // Slowest route should not get stuck in infinite loop
        Route slowest = routeFinder.findRoute("VIE", "JFK", RouteFinder.CRITERION_SLOWEST);
        assertNotNull(slowest);
    }

    @Test
    void testMultipleRoutesSameWeight() {
        // Two routes with same price:
        // Path 1: VIE -> LHR -> JFK (150 + 400 = 550) - already in setup
        // Path 2: VIE -> CDG -> JFK (let's add this)
        graph.addFlight(new Flight(20, "CDG", "JFK", "AF", "AF3", 400, 450.0, LocalTime.of(15, 0)), cdg);
        // VIE -> CDG is flight 4 (100 price).
        // Total price VIE -> CDG -> JFK = 100 + 450 = 550.
        
        // Both are 550.
        Route route = routeFinder.findRoute("VIE", "JFK", RouteFinder.CRITERION_CHEAPEST);
        assertNotNull(route);
        assertEquals(550.0, route.getTotalPrice());
        // The implementation picks the first one found.
    }
}
