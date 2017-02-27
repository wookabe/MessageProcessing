package wookabe.msgprocess;

import static org.junit.Assert.assertEquals;

/**
 * Test helper methods bucket for static imports.
 */
public class Helper {
    /**
     * Helper method to clean up assertion of double values.
     *
     * @param expectedValue expected double
     * @param returnedValue returned double
     */
    public static void assertTotalValue(double expectedValue, double returnedValue) {
        assertEquals(expectedValue, returnedValue, .001);
    }
}
