# Flight Route Planner - Project Summary

## Deliverables Checklist

### ✅ Complete Runnable Java 21 Maven Project

- **Maven Configuration**: `pom.xml` configured for Java 21
- **Compiled Successfully**: All classes compile without errors
- **Executable JAR**: `target/flight-route-planner-1.0.0.jar` created and tested
- **Tested**: Application runs successfully and finds optimal routes

### ✅ Well-Structured, Commented Source Code

**Total: 10 Java Classes**

#### Model Classes (3)
1. `Airport.java` - Represents airports with IATA codes, coordinates, and location data
2. `Flight.java` - Represents flights with origin, destination, price, duration, and departure time
3. `Route.java` - Represents complete routes with automatic calculation of totals and stopovers

#### Graph & Data Management (2)
4. `FlightGraph.java` - Adjacency list graph implementation for airports and flights
5. `DataLoader.java` - CSV file reading/writing and graph construction

#### Algorithms (3)
6. `RouteFinder.java` - Dijkstra's algorithm and modified DFS for route finding
7. `RouteComparators.java` - Four comparators (Price, Duration, Stopovers, Combined)
8. `RouteSorter.java` - Merge Sort (stable) and Quick Sort (unstable) implementations

#### Search & UI (2)
9. `FlightSearch.java` - Linear search for flights by origin, destination, airline, flight number
10. `FlightPlannerApp.java` - Main application with interactive console menu

### ✅ Documentation PDF with Justifications

- **Justification.md**: Comprehensive Markdown documentation (5 pages equivalent)
- **Justification.pdf**: Professional PDF version
- **Content Includes**:
  - Algorithm choices and reasoning
  - Time and space complexity analysis
  - Overview of each class and its role
  - Data structure rationale (Adjacency List)
  - Handling stopovers, input errors, and assumptions

### ✅ CSV Sample Files

Located in `src/main/resources/`:

1. **airports.csv**: 15 major European and US airports
2. **flights.csv**: 60 flights connecting the airports
3. **routes.csv**: 10 pre-defined multi-stop routes

## Key Features Implemented

### 1. Route Calculation (Graph Search Algorithms)

#### Dijkstra's Algorithm
- **Cheapest Route**: Edge weight = price
- **Fastest Route**: Edge weight = duration + stopover time
- **Fewest Stopovers**: Edge weight = 1 (uniform)
- **Complexity**: O(E + V log V) with priority queue

#### Modified DFS
- **Slowest Route**: Maximum duration path with depth limit of 3 stopovers
- **Prevents Cycles**: Visited set to avoid infinite loops

### 2. Sorting Algorithms

#### Merge Sort (Stable)
- Time: O(n log n) guaranteed
- Space: O(n)
- Preserves relative order of equal elements

#### Quick Sort (Unstable)
- Time: O(n log n) average
- Space: O(log n)
- In-place sorting, fast in practice

### 3. Comparators

1. **PriceComparator**: Sort by total price
2. **DurationComparator**: Sort by total duration
3. **StopoversComparator**: Sort by number of stopovers
4. **CombinedComparator**: Chains all three (Price → Duration → Stopovers)

### 4. Search Functionality

Linear search implementations:
- Search by origin airport
- Search by destination airport
- Search by airline
- Search by flight number

### 5. Data Management

- Read airports from CSV
- Read flights from CSV
- Read routes from CSV
- Save routes to CSV
- Automatic graph construction
- Input validation and error handling

## Testing Results

### Test Case 1: Direct Flight
- **Route**: VIE → FRA
- **Status**: ✅ Passed
- **Result**: Single flight found correctly

### Test Case 2: Multi-Stop Flight
- **Route**: VIE → FRA → JFK (Cheapest)
- **Status**: ✅ Passed
- **Result**: 
  - Total Price: $770.50
  - Total Duration: 9h 35m (including 20-min stopover)
  - Stopovers: 1

### Test Case 3: Invalid IATA Codes
- **Input**: Invalid airport codes
- **Status**: ✅ Passed
- **Result**: Graceful error handling with clear message

### Test Case 4: CSV Loading
- **Files**: airports.csv, flights.csv, routes.csv
- **Status**: ✅ Passed
- **Result**: All data loaded successfully, graph built correctly

### Test Case 5: Sorting
- **Algorithm**: Merge Sort with Price Comparator
- **Status**: ✅ Passed
- **Result**: Routes sorted correctly by price

## Code Quality

### Documentation
- ✅ Javadoc comments on all public classes and methods
- ✅ Inline comments explaining complex logic
- ✅ Clear method and variable naming

### Error Handling
- ✅ Try-catch blocks for user input
- ✅ Validation of CSV data
- ✅ Graceful handling of missing files
- ✅ Clear error messages

### Code Organization
- ✅ Modular package structure
- ✅ Separation of concerns
- ✅ Single Responsibility Principle
- ✅ Clean, readable code

## Performance Characteristics

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Route Finding (Dijkstra) | O(E + V log V) | O(V + E) |
| Route Finding (DFS) | O(b^d) | O(d) |
| Stable Sort | O(n log n) | O(n) |
| Unstable Sort | O(n log n) avg | O(log n) |
| Linear Search | O(n) | O(1) |
| Graph Construction | O(V + E) | O(V + E) |

## Files Included

### Source Code
- 10 Java classes (1,500+ lines of code)
- Fully commented and documented

### Configuration
- `pom.xml` - Maven build configuration

### Data Files
- `airports.csv` - 15 airports
- `flights.csv` - 60 flights
- `routes.csv` - 10 routes

### Documentation
- `README.md` - Comprehensive user guide
- `Justification.md` - Algorithm and design justification
- `Justification.pdf` - PDF version of justification
- `PROJECT_SUMMARY.md` - This file

### Build Artifacts
- `target/flight-route-planner-1.0.0.jar` - Executable JAR

## How to Run

### Compile and Package
```bash
mvn clean package
```

### Run the Application
```bash
java -jar target/flight-route-planner-1.0.0.jar
```

### Quick Test
1. Select option 2: Load airports (use default `airports.csv`)
2. Select option 3: Load flights (use default `flights.csv`)
3. Select option 1: Calculate route
   - Origin: VIE
   - Destination: JFK
   - Criterion: 1 (Cheapest)
4. View the optimal route

## Conclusion

This project successfully implements a comprehensive flight route planning system using graph theory and advanced algorithms. All requirements have been met:

✅ Complete runnable Java 21 Maven project  
✅ Well-structured, commented source code  
✅ Documentation PDF with algorithm justifications  
✅ CSV sample files in src/main/resources/  
✅ Tested and verified functionality  

The application demonstrates practical applications of:
- Graph data structures (Adjacency List)
- Search algorithms (Dijkstra, DFS)
- Sorting algorithms (Merge Sort, Quick Sort)
- Algorithm complexity analysis
- Software engineering best practices
