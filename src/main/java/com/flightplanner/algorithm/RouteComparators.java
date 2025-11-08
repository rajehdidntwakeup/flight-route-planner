package com.flightplanner.algorithm;

import com.flightplanner.model.Route;

import java.util.Comparator;

/**
 * Provides various comparators for sorting Route objects.
 * Includes individual comparators for price, duration, and stopovers,
 * as well as a combined comparator that chains all three.
 */
public class RouteComparators {

  /**
   * Comparator that sorts routes by total price (ascending).
   */
  public static class PriceComparator implements Comparator<Route> {
    @Override
    public int compare(Route r1, Route r2) {
      return Double.compare(r1.getTotalPrice(), r2.getTotalPrice());
    }
  }

  /**
   * Comparator that sorts routes by total duration (ascending).
   */
  public static class DurationComparator implements Comparator<Route> {
    @Override
    public int compare(Route r1, Route r2) {
      return Integer.compare(r1.getTotalDuration(), r2.getTotalDuration());
    }
  }

  /**
   * Comparator that sorts routes by number of stopovers (ascending).
   */
  public static class StopoversComparator implements Comparator<Route> {
    @Override
    public int compare(Route r1, Route r2) {
      return Integer.compare(r1.getStopovers(), r2.getStopovers());
    }
  }

  /**
   * Combined comparator that chains price, duration, and stopovers comparisons.
   * First compares by price, then by duration if prices are equal,
   * then by stopovers if durations are also equal.
   */
  public static class CombinedComparator implements Comparator<Route> {
    private final Comparator<Route> chainedComparator;

    public CombinedComparator() {
      // Chain comparators: Price -> Duration -> Stopovers
      this.chainedComparator = new PriceComparator()
          .thenComparing(new DurationComparator())
          .thenComparing(new StopoversComparator());
    }

    @Override
    public int compare(Route r1, Route r2) {
      return chainedComparator.compare(r1, r2);
    }
  }

  /**
   * Gets a comparator based on the specified type.
   *
   * @param type Comparator type (1=Price, 2=Duration, 3=Stopovers, 4=Combined)
   * @return The corresponding Comparator, or null if invalid type
   */
  public static Comparator<Route> getComparator(int type) {
    return switch (type) {
      case 1 -> new PriceComparator();
      case 2 -> new DurationComparator();
      case 3 -> new StopoversComparator();
      case 4 -> new CombinedComparator();
      default -> null;
    };
  }

  /**
   * Gets the name of a comparator type.
   *
   * @param type Comparator type (1-4)
   * @return Name of the comparator
   */
  public static String getComparatorName(int type) {
    return switch (type) {
      case 1 -> "Price";
      case 2 -> "Duration";
      case 3 -> "Stopovers";
      case 4 -> "Combined (Price → Duration → Stopovers)";
      default -> "Unknown";
    };
  }
}
