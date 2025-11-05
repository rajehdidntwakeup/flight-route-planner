# Flight Route Planner

A comprehensive Java 21 Maven console-based application for flight route planning, integrating graph theory, search algorithms, and sorting algorithms.

## Project Overview

This application models flight networks as a graph and provides optimal route finding based on various criteria such as price, duration, and number of stopovers. It demonstrates the practical application of computer science algorithms including Dijkstra's shortest path algorithm, depth-first search, merge sort, and quick sort.

## Features

### Core Functionality

- **Graph-Based Route Planning**: Models airports as nodes and flights as edges in a directed weighted graph
- **Multiple Optimization Criteria**:
  - Cheapest route (minimum total price)
  - Fastest route (minimum total duration)
  - Fewest stopovers (minimum number of connections)
  - Slowest route (maximum total duration, limited to 3 stopovers)
- **CSV Data Management**: Read and write airports, flights, and routes from/to CSV files
- **Sorting Capabilities**:
  - Stable sort (Merge Sort)
  - Unstable sort (Quick Sort)
  - Multiple comparators (Price, Duration, Stopovers, Combined)
- **Search Functionality**: Linear search for flights by origin, destination, airline, or flight number
- **Interactive Console Menu**: User-friendly text-based interface

## Project Structure

```
flight-route-planner/
├── pom.xml                                 # Maven configuration
├── README.md                               # This file
├── Justification.md                        # Algorithm and design justification (Markdown)
├── Justification.pdf                       # Algorithm and design justification (PDF)
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── flightplanner/
│       │           ├── model/              # Data models
│       │           │   ├── Airport.java
│       │           │   ├── Flight.java
│       │           │   └── Route.java
│       │           ├── graph/              # Graph data structure
│       │           │   └── FlightGraph.java
│       │           ├── algorithm/          # Search and sorting algorithms
│       │           │   ├── RouteFinder.java
│       │           │   ├── RouteComparators.java
│       │           │   └── RouteSorter.java
│       │           ├── search/             # Search functionality
│       │           │   └── FlightSearch.java
│       │           ├── util/               # Utility classes
│       │           │   └── DataLoader.java
│       │           └── menu/               # Main application
│       │               └── FlightPlannerApp.java
│       └── resources/                      # CSV data files
│           ├── airports.csv
│           ├── flights.csv
│           └── routes.csv
└── target/
    └── flight-route-planner-1.0.0.jar     # Compiled JAR file
```

## Requirements

- **Java**: Version 21 or higher
- **Maven**: Version 3.6 or higher

## Installation and Setup

### 1. Clone or Extract the Project

```bash
cd flight-route-planner
```

### 2. Compile the Project

```bash
mvn clean compile
```

### 3. Package as JAR

```bash
mvn package
```

This will create an executable JAR file in the `target/` directory.

## Running the Application

### Option 1: Run with Maven

```bash
mvn exec:java -Dexec.mainClass="com.flightplanner.menu.FlightPlannerApp"
```

### Option 2: Run the JAR File

```bash
java -jar target/flight-route-planner-1.0.0.jar
```

## Usage Guide

### Main Menu Options

1. **Route Calculation**: Find optimal routes between two airports
2. **Read Airports from CSV**: Load airport data from a CSV file
3. **Read Flights from CSV**: Load flight data from a CSV file
4. **Read Routes from CSV**: Load pre-defined routes from a CSV file
5. **Save Routes to CSV**: Save routes to a CSV file
6. **Sorting**: Sort routes using different algorithms and comparators
7. **Search**: Search for flights by various criteria
8. **Exit**: Exit the application

### Sample Workflow

1. Start the application
2. Select option **2** to load airports (use default `airports.csv`)
3. Select option **3** to load flights (use default `flights.csv`)
4. Select option **1** to calculate a route:
   - Enter origin IATA code (e.g., `VIE`)
   - Enter destination IATA code (e.g., `JFK`)
   - Select criterion (e.g., `1` for cheapest)
5. View the optimal route with detailed flight information

### Example: Finding the Cheapest Route from Vienna to New York

```
Enter origin IATA code: VIE
Enter destination IATA code: JFK
Select criterion: 1

Result:
=== Route 0 ===
Total Duration: 9h 35m | Total Price: $770.50 | Stopovers: 1

Flight 1: Flight LH1234 [ID: 1] - VIE → FRA | Lufthansa | Duration: 75 min | Price: $120.50 | Departs: 08:00
  → Stopover: 20 minutes
Flight 2: Flight LH400 [ID: 6] - FRA → JFK | Lufthansa | Duration: 480 min | Price: $650.00 | Departs: 10:30
```

## CSV File Formats

### airports.csv

```csv
id,iata,city,country,latitude,longitude
1,VIE,Vienna,Austria,48.1103,16.5697
```

### flights.csv

```csv
id,origin,destination,airline,flightNumber,duration,price,departureTime
1,VIE,FRA,Lufthansa,LH1234,75,120.50,08:00
```

### routes.csv

```csv
id,flightIds
1,1-6
```

Flight IDs are separated by hyphens (e.g., `1-6` means flight 1 followed by flight 6).

## Algorithms Implemented

### Route Finding

- **Dijkstra's Algorithm**: Used for finding cheapest, fastest, and fewest stopovers routes
  - Time Complexity: O(E + V log V) with priority queue
  - Space Complexity: O(V + E)

- **Modified Depth-First Search (DFS)**: Used for finding the slowest route
  - Depth-limited to 3 stopovers to prevent excessive computation
  - Time Complexity: O(b^d) where b is branching factor and d is depth limit

### Sorting

- **Merge Sort (Stable)**: Guarantees O(n log n) time complexity in all cases
- **Quick Sort (Unstable)**: Average O(n log n), efficient in-place sorting

### Search

- **Linear Search**: O(n) time complexity for searching flights by various criteria

## Key Design Decisions

### Graph Representation

The application uses an **adjacency list** representation (`Map<Airport, List<Flight>>`) for the flight graph. This choice was made because:

- Flight networks are typically sparse (few flights per airport compared to total airports)
- Adjacency lists are space-efficient for sparse graphs
- Fast neighbor traversal for route-finding algorithms

### Stopover Handling

A minimum **20-minute stopover time** is automatically added between connecting flights when calculating total duration. This reflects realistic transfer times.

### Assumptions

- **Time zones are ignored**: All times are treated as local
- **Static data**: Flight and airport data is loaded once and treated as static during runtime
- **No cycles in slowest route**: The DFS algorithm prevents revisiting airports to avoid infinite loops

## Testing

The application has been tested with the following scenarios:

- ✅ Direct flights (e.g., VIE → FRA)
- ✅ Multi-stop flights (e.g., VIE → FRA → JFK)
- ✅ No possible route found
- ✅ Invalid IATA codes
- ✅ Sorting with different comparators
- ✅ Searching for existing and non-existing flights

## Documentation

For a detailed explanation of algorithm choices, complexity analysis, and design rationale, please refer to:

- **Justification.md**: Markdown version
- **Justification.pdf**: PDF version

## Author

**Manus AI**

## License

This project is provided as-is for educational purposes.

## Troubleshooting

### Issue: Java version mismatch

**Solution**: Ensure you have Java 21 or higher installed:

```bash
java -version
```

If needed, install Java 21:

```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

### Issue: Maven not found

**Solution**: Install Maven:

```bash
sudo apt update
sudo apt install maven
```

### Issue: CSV files not found

**Solution**: Ensure CSV files are located in `src/main/resources/` directory. The application looks for files in this location by default.

## Future Enhancements

Potential improvements for future versions:

- Time zone support for accurate departure and arrival times
- Real-time flight data integration via APIs
- GUI interface for better user experience
- Multi-criteria optimization (Pareto optimal routes)
- Flight booking integration
- Historical route analysis and recommendations
