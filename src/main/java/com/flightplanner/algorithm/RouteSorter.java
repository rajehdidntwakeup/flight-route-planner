package com.flightplanner.algorithm;

import com.flightplanner.model.Route;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Provides sorting algorithms for Route lists.
 * Implements both stable (Merge Sort) and unstable (Quick Sort) sorting.
 */
public class RouteSorter {

    /**
     * Stable sort using Merge Sort algorithm.
     * Time Complexity: O(n log n) in all cases
     * Space Complexity: O(n) for temporary arrays
     * Stability: Stable - preserves relative order of equal elements
     *
     * @param routes     List of routes to sort (will be modified in place)
     * @param comparator Comparator to determine sort order
     */
    public static void stableSort(List<Route> routes, Comparator<Route> comparator) {
        if (routes == null || routes.size() <= 1) {
            return;
        }
        mergeSort(routes, comparator, 0, routes.size() - 1);
    }

    /**
     * Unstable sort using Quick Sort algorithm.
     * Time Complexity: O(n log n) average, O(n²) worst case
     * Space Complexity: O(log n) for recursion stack
     * Stability: Unstable - may change relative order of equal elements
     *
     * @param routes     List of routes to sort (will be modified in place)
     * @param comparator Comparator to determine sort order
     */
    public static void unstableSort(List<Route> routes, Comparator<Route> comparator) {
        if (routes == null || routes.size() <= 1) {
            return;
        }
        quickSort(routes, comparator, 0, routes.size() - 1);
    }

    /**
     * Merge Sort implementation (recursive).
     * Divides the list into halves, recursively sorts them, and merges the results.
     *
     * @param routes     List to sort
     * @param comparator Comparator for ordering
     * @param left       Left boundary index
     * @param right      Right boundary index
     */
    private static void mergeSort(List<Route> routes, Comparator<Route> comparator, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            // Recursively sort left and right halves
            mergeSort(routes, comparator, left, mid);
            mergeSort(routes, comparator, mid + 1, right);

            // Merge the sorted halves
            merge(routes, comparator, left, mid, right);
        }
    }

    /**
     * Merges two sorted subarrays into a single sorted subarray.
     * This is the key operation in Merge Sort that ensures stability.
     *
     * @param routes     List containing the subarrays
     * @param comparator Comparator for ordering
     * @param left       Start index of first subarray
     * @param mid        End index of first subarray
     * @param right      End index of second subarray
     */
    private static void merge(List<Route> routes, Comparator<Route> comparator, int left, int mid, int right) {
        // Create temporary arrays
        List<Route> leftArray = new ArrayList<>(routes.subList(left, mid + 1));
        List<Route> rightArray = new ArrayList<>(routes.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;

        // Merge the temporary arrays back into routes[left..right]
        while (i < leftArray.size() && j < rightArray.size()) {
            // Use <= to maintain stability (prefer left element when equal)
            if (comparator.compare(leftArray.get(i), rightArray.get(j)) <= 0) {
                routes.set(k++, leftArray.get(i++));
            } else {
                routes.set(k++, rightArray.get(j++));
            }
        }

        // Copy remaining elements from leftArray
        while (i < leftArray.size()) {
            routes.set(k++, leftArray.get(i++));
        }

        // Copy remaining elements from rightArray
        while (j < rightArray.size()) {
            routes.set(k++, rightArray.get(j++));
        }
    }

    /**
     * Quick Sort implementation (recursive).
     * Selects a pivot, partitions the array around it, and recursively sorts partitions.
     *
     * @param routes     List to sort
     * @param comparator Comparator for ordering
     * @param low        Starting index
     * @param high       Ending index
     */
    private static void quickSort(List<Route> routes, Comparator<Route> comparator, int low, int high) {
        if (low < high) {
            // Partition the array and get the pivot index
            int pivotIndex = partition(routes, comparator, low, high);

            // Recursively sort elements before and after partition
            quickSort(routes, comparator, low, pivotIndex - 1);
            quickSort(routes, comparator, pivotIndex + 1, high);
        }
    }

    /**
     * Partitions the array around a pivot element.
     * Elements smaller than pivot go to the left, larger elements go to the right.
     *
     * @param routes     List to partition
     * @param comparator Comparator for ordering
     * @param low        Starting index
     * @param high       Ending index (pivot is chosen from here)
     * @return Final position of the pivot
     */
    private static int partition(List<Route> routes, Comparator<Route> comparator, int low, int high) {
        // Choose the rightmost element as pivot
        Route pivot = routes.get(high);
        int i = low - 1;

        // Move elements smaller than pivot to the left
        for (int j = low; j < high; j++) {
            if (comparator.compare(routes.get(j), pivot) < 0) {
                i++;
                swap(routes, i, j);
            }
        }

        // Place pivot in its correct position
        swap(routes, i + 1, high);
        return i + 1;
    }

    /**
     * Swaps two elements in the list.
     *
     * @param routes List containing the elements
     * @param i      Index of first element
     * @param j      Index of second element
     */
    private static void swap(List<Route> routes, int i, int j) {
        Route temp = routes.get(i);
        routes.set(i, routes.get(j));
        routes.set(j, temp);
    }
}
