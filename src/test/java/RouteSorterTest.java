import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import at.hochschule.burgenland.bswe.algo.algorithm.RouteComparators;
import at.hochschule.burgenland.bswe.algo.algorithm.RouteSorter;
import at.hochschule.burgenland.bswe.algo.model.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RouteSorterTest {

  private List<Route> routes;

  @BeforeEach
  void setUp() {
    routes = new ArrayList<>(Arrays.asList(
        new Route(1, Collections.emptyList(), 300, 200.0, 1),
        new Route(2, Collections.emptyList(), 200, 300.0, 0),
        new Route(3, Collections.emptyList(), 400, 100.0, 2),
        new Route(4, Collections.emptyList(), 100, 400.0, 3)
    ));
  }

  @Test
  void testStableSortByPrice() {
    RouteSorter.stableSort(routes, new RouteComparators.PriceComparator());
    assertEquals(100.0, routes.get(0).getTotalPrice());
    assertEquals(200.0, routes.get(1).getTotalPrice());
    assertEquals(300.0, routes.get(2).getTotalPrice());
    assertEquals(400.0, routes.get(3).getTotalPrice());
  }

  @Test
  void testStableSortByDuration() {
    RouteSorter.stableSort(routes, new RouteComparators.DurationComparator());
    assertEquals(100, routes.get(0).getTotalDuration());
    assertEquals(200, routes.get(1).getTotalDuration());
    assertEquals(300, routes.get(2).getTotalDuration());
    assertEquals(400, routes.get(3).getTotalDuration());
  }

  @Test
  void testStableSortByStopovers() {
    RouteSorter.stableSort(routes, new RouteComparators.StopoversComparator());
    assertEquals(0, routes.get(0).getStopovers());
    assertEquals(1, routes.get(1).getStopovers());
    assertEquals(2, routes.get(2).getStopovers());
    assertEquals(3, routes.get(3).getStopovers());
  }

  @Test
  void testStableSortStability() {
    // Add routes with same price but different IDs to test stability
    List<Route> stabilityRoutes = new ArrayList<>(Arrays.asList(
        new Route(1, Collections.emptyList(), 100, 100.0, 0),
        new Route(2, Collections.emptyList(), 100, 100.0, 0),
        new Route(3, Collections.emptyList(), 100, 100.0, 0)
    ));
    
    // They are already in order 1, 2, 3. Stable sort should preserve this.
    RouteSorter.stableSort(stabilityRoutes, new RouteComparators.PriceComparator());
    assertEquals(1, stabilityRoutes.get(0).getId());
    assertEquals(2, stabilityRoutes.get(1).getId());
    assertEquals(3, stabilityRoutes.get(2).getId());
  }

  @Test
  void testStableSortNullAndEmpty() {
    RouteSorter.stableSort(null, new RouteComparators.PriceComparator()); // Should not throw exception
    
    List<Route> emptyList = new ArrayList<>();
    RouteSorter.stableSort(emptyList, new RouteComparators.PriceComparator());
    assertTrue(emptyList.isEmpty());

    List<Route> singleElement = new ArrayList<>(Collections.singletonList(routes.get(0)));
    RouteSorter.stableSort(singleElement, new RouteComparators.PriceComparator());
    assertEquals(1, singleElement.size());
    assertEquals(1, singleElement.get(0).getId());
  }

  @Test
  void testUnstableSortByPrice() {
    RouteSorter.unstableSort(routes, new RouteComparators.PriceComparator());
    assertEquals(100.0, routes.get(0).getTotalPrice());
    assertEquals(200.0, routes.get(1).getTotalPrice());
    assertEquals(300.0, routes.get(2).getTotalPrice());
    assertEquals(400.0, routes.get(3).getTotalPrice());
  }

  @Test
  void testUnstableSortByDuration() {
    RouteSorter.unstableSort(routes, new RouteComparators.DurationComparator());
    assertEquals(100, routes.get(0).getTotalDuration());
    assertEquals(200, routes.get(1).getTotalDuration());
    assertEquals(300, routes.get(2).getTotalDuration());
    assertEquals(400, routes.get(3).getTotalDuration());
  }

  @Test
  void testUnstableSortNullAndEmpty() {
    RouteSorter.unstableSort(null, new RouteComparators.PriceComparator()); // Should not throw exception

    List<Route> emptyList = new ArrayList<>();
    RouteSorter.unstableSort(emptyList, new RouteComparators.PriceComparator());
    assertTrue(emptyList.isEmpty());

    List<Route> singleElement = new ArrayList<>(Collections.singletonList(routes.get(0)));
    RouteSorter.unstableSort(singleElement, new RouteComparators.PriceComparator());
    assertEquals(1, singleElement.size());
    assertEquals(1, singleElement.get(0).getId());
  }

  @Test
  void testUnstableSortLargeList() {
    List<Route> largeList = new ArrayList<>();
    for (int i = 100; i >= 1; i--) {
      largeList.add(new Route(i, Collections.emptyList(), i, i * 10.0, 0));
    }
    RouteSorter.unstableSort(largeList, new RouteComparators.PriceComparator());
    for (int i = 0; i < 100; i++) {
      assertEquals((i + 1) * 10.0, largeList.get(i).getTotalPrice());
    }
  }
}
