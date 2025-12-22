import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import at.hochschule.burgenland.bswe.algo.menu.FlightPlannerUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FlightPlannerIntegrationTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;
    private final PrintStream systemErr = System.err;

    private ByteArrayOutputStream testOut;
    private ByteArrayOutputStream testErr;

    @BeforeEach
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        testErr = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
        System.setErr(new PrintStream(testErr));
    }

    @AfterEach
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
        System.setErr(systemErr);
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }

    @Test
    public void testFullWorkflow() throws Exception {
        // Prepare test data files in src/main/resources if they don't exist or use existing ones.
        // The DataLoader looks in classpath then src/main/resources/
        
        // Actions:
        // 2. Read Airports (airports.csv)
        // 3. Read Flights (flights.csv)
        // 1. Route Calculation (VIE to JFK)
        // 5. Save Routes (test_routes.csv)
        // 4. Read Routes (test_routes.csv)
        // 6. Sorting (by Price)
        // 7. Search (VIE)
        // 8. Exit

        String input = "2\nairports.csv\n" +
                       "3\nflights.csv\n" +
                       "1\nVIE\nJFK\n1\ny\n" + // VIE to JFK, optimize for price, Save: y
                       "5\ntest_routes.csv\n" +
                       "4\ntest_routes.csv\n" +
                       "6\nall\n1\n1\n" + // Sort by Price: IDs=all, Algo=1, Comp=1
                       "7\n1\nVIE\n" + // Search by Origin: Type=1, Term=VIE
                       "8\n";

        provideInput(input);

        FlightPlannerUI ui = new FlightPlannerUI();
        ui.start();

        String output = getOutput();
        
        // Verify output contains expected milestones
        assertTrue(output.contains("FLIGHT ROUTE PLANNER"), "Should show welcome message");
        assertTrue(output.contains("Loaded 15 airports"), "Should have loaded airports");
        assertTrue(output.contains("Loaded 60 flights"), "Should have loaded flights");
        assertTrue(output.contains("Route"), "Should have found a route");
        assertTrue(output.contains("Saved 1 routes to test_routes.csv"), "Should have saved routes");
        assertTrue(output.contains("Loaded 1 routes from test_routes.csv"), "Should have loaded routes");
        assertTrue(output.contains("Sorted Routes"), "Should have sorted routes");
        assertTrue(output.contains("Search Results"), "Should have performed search");
        assertTrue(output.contains("Goodbye!"), "Should have exited cleanly");
        
        // Clean up
        Path testRoutesPath = Paths.get("src", "main", "resources", "test_routes.csv");
        Files.deleteIfExists(testRoutesPath);
    }

    @Test
    public void testInvalidChoicesAndErrorHandling() {
        String input = "9\n" + // Invalid choice
                       "2\nnon_existent.csv\n" + // Invalid file
                       "8\n";
        
        provideInput(input);
        
        FlightPlannerUI ui = new FlightPlannerUI();
        ui.start();
        
        String output = getOutput();
        assertTrue(output.contains("Invalid choice"), "Should handle invalid menu choice");
        // DataLoader might print error to System.err or throw exception handled by UI
        // FlightPlannerUI handles exceptions with System.err.println("Error: " + e.getMessage());
        // Since we redirected System.err, we can check testErr too if needed, 
        // but UI prints "Error: ..." to System.err.
    }
}
