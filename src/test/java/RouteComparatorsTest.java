import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import at.hochschule.burgenland.bswe.algo.algorithm.RouteComparators;
import at.hochschule.burgenland.bswe.algo.model.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RouteComparatorsTest {

    private Route route1;
    private Route route2;
    private Route route3;

    @BeforeEach
    void setUp() {
        // id, flightIds, totalDuration, totalPrice, stopovers
        route1 = new Route(1, new ArrayList<>(), 100, 200.0, 1);
        route2 = new Route(2, new ArrayList<>(), 150, 150.0, 2);
        route3 = new Route(3, new ArrayList<>(), 100, 200.0, 0);
    }

    @Test
    void testPriceComparator() {
        RouteComparators.PriceComparator comparator = new RouteComparators.PriceComparator();
        // route1 price 200.0, route2 price 150.0
        assertTrue(comparator.compare(route1, route2) > 0);
        assertTrue(comparator.compare(route2, route1) < 0);
        assertEquals(0, comparator.compare(route1, route3));
    }

    @Test
    void testDurationComparator() {
        RouteComparators.DurationComparator comparator = new RouteComparators.DurationComparator();
        // route1 duration 100, route2 duration 150
        assertTrue(comparator.compare(route1, route2) < 0);
        assertTrue(comparator.compare(route2, route1) > 0);
        assertEquals(0, comparator.compare(route1, route3));
    }

    @Test
    void testStopoversComparator() {
        RouteComparators.StopoversComparator comparator = new RouteComparators.StopoversComparator();
        // route1 stopovers 1, route2 stopovers 2
        assertTrue(comparator.compare(route1, route2) < 0);
        assertTrue(comparator.compare(route2, route1) > 0);
        // route1 stopovers 1, route3 stopovers 0
        assertTrue(comparator.compare(route1, route3) > 0);
    }

    @Test
    void testCombinedComparator() {
        RouteComparators.CombinedComparator comparator = new RouteComparators.CombinedComparator();
        
        // Price: route1 (200.0) vs route2 (150.0) -> route2 is smaller
        assertTrue(comparator.compare(route1, route2) > 0);
        
        // Price equal: route1 (200.0) vs route3 (200.0)
        // Duration equal: route1 (100) vs route3 (100)
        // Stopovers: route1 (1) vs route3 (0) -> route3 is smaller
        assertTrue(comparator.compare(route1, route3) > 0);
        assertTrue(comparator.compare(route3, route1) < 0);

        // Test with same values
        Route route4 = new Route(4, new ArrayList<>(), 100, 200.0, 1);
        assertEquals(0, comparator.compare(route1, route4));
    }

    @Test
    void testPriceComparatorEdgeCases() {
        RouteComparators.PriceComparator comparator = new RouteComparators.PriceComparator();
        
        Route cheap = new Route(4, new ArrayList<>(), 100, 0.0, 1);
        Route expensive = new Route(5, new ArrayList<>(), 100, Double.MAX_VALUE, 1);
        
        assertTrue(comparator.compare(cheap, expensive) < 0);
        assertTrue(comparator.compare(expensive, cheap) > 0);
        
        // Null check - standard comparator behavior is to throw NPE
        assertThrows(NullPointerException.class, () -> comparator.compare(null, route1));
        assertThrows(NullPointerException.class, () -> comparator.compare(route1, null));
    }

    @Test
    void testDurationComparatorEdgeCases() {
        RouteComparators.DurationComparator comparator = new RouteComparators.DurationComparator();
        
        Route shortRoute = new Route(4, new ArrayList<>(), 0, 200.0, 1);
        Route longRoute = new Route(5, new ArrayList<>(), Integer.MAX_VALUE, 200.0, 1);
        
        assertTrue(comparator.compare(shortRoute, longRoute) < 0);
        assertTrue(comparator.compare(longRoute, shortRoute) > 0);
        
        assertThrows(NullPointerException.class, () -> comparator.compare(null, route1));
    }

    @Test
    void testStopoversComparatorEdgeCases() {
        RouteComparators.StopoversComparator comparator = new RouteComparators.StopoversComparator();
        
        Route direct = new Route(4, new ArrayList<>(), 100, 200.0, 0);
        Route manyStops = new Route(5, new ArrayList<>(), 100, 200.0, Integer.MAX_VALUE);
        
        assertTrue(comparator.compare(direct, manyStops) < 0);
        assertTrue(comparator.compare(manyStops, direct) > 0);
        
        assertThrows(NullPointerException.class, () -> comparator.compare(null, route1));
    }

    @Test
    void testCombinedComparatorComplexTies() {
        RouteComparators.CombinedComparator comparator = new RouteComparators.CombinedComparator();
        
        // Scenario: Same Price, Different Duration
        Route r1 = new Route(10, new ArrayList<>(), 100, 200.0, 5);
        Route r2 = new Route(11, new ArrayList<>(), 150, 200.0, 1);
        assertTrue(comparator.compare(r1, r2) < 0, "Should favor shorter duration when prices are equal");
        
        // Scenario: Same Price, Same Duration, Different Stopovers
        Route r3 = new Route(12, new ArrayList<>(), 100, 200.0, 1);
        Route r4 = new Route(13, new ArrayList<>(), 100, 200.0, 3);
        assertTrue(comparator.compare(r3, r4) < 0, "Should favor fewer stopovers when price and duration are equal");
    }

    @Test
    void testGetComparator() {
        assertInstanceOf(RouteComparators.PriceComparator.class, RouteComparators.getComparator(1));
        assertInstanceOf(RouteComparators.DurationComparator.class, RouteComparators.getComparator(2));
        assertInstanceOf(RouteComparators.StopoversComparator.class, RouteComparators.getComparator(3));
        assertInstanceOf(RouteComparators.CombinedComparator.class, RouteComparators.getComparator(4));
        assertNull(RouteComparators.getComparator(0));
        assertNull(RouteComparators.getComparator(5));
    }

    @Test
    void testGetComparatorName() {
        assertEquals("Price", RouteComparators.getComparatorName(1));
        assertEquals("Duration", RouteComparators.getComparatorName(2));
        assertEquals("Stopovers", RouteComparators.getComparatorName(3));
        assertEquals("Combined (Price → Duration → Stopovers)", RouteComparators.getComparatorName(4));
        assertEquals("Unknown", RouteComparators.getComparatorName(0));
        assertEquals("Unknown", RouteComparators.getComparatorName(5));
    }
}
