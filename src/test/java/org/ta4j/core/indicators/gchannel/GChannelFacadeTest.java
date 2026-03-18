package org.ta4j.core.indicators.gchannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TestdataReader;
import org.ta4j.core.indicators.AbstractIndicatorTest;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

/**
 * Unit tests for the {@code GChannelFacade} class. This class validates the accuracy and integrity of the G-Channel
 * Trend Detection indicator implementations, including the lower band, upper band, and average line calculations.
 *
 * The test cases use historical market data (`btcusdt-1h.csv`) to verify the indicators' accuracy under different
 * conditions and assert their behaviors for specific time points.
 *
 * Test methods include:
 *
 * - {@code average}: Validates the average line calculations and its bullish/bearish trend detection.
 *
 * - {@code upper}: Validates the upper band calculations and compares its bullish condition with the average
 * indicator.
 *
 * - {@code lower}: Validates the lower band calculations and compares its bullish condition with the average
 * indicator.
 *
 * Each test method evaluates the indicator at specific indices corresponding to historical timestamps.
 *
 * This class extends {@link AbstractIndicatorTest}, which provides a parameterized test setup to verify indicator
 * functionality across multiple Num implementations (e.g., {@code DecimalNum} and {@code DoubleNum}).
 */
public class GChannelFacadeTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    Duration duration = Duration.ofHours(1);
    private BarSeries data;
    private GChannelFacade indicator;

    public GChannelFacadeTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1h.csv", duration);
        indicator = new GChannelFacade(data, 100);
    }

    /**
     * Validates the functionality of the average method in the G-Channel indicator,
     * which calculates the average value of the upper and lower bands of the indicator
     * and determines the bullish or bearish trend based on historical data.
     *
     * The method performs unit tests across different points in time (specified via
     * index) and asserts the following:
     * - The calculated average value from the indicator.
     * - Whether the trend can be classified as bullish.
     *
     * Preconditions:
     * - The `indicator` object must be initialized and configured with valid data.
     * - The underlying `data` series must provide enough historical data to
     *   compute the average for the specified indices.
     *
     * Asserts:
     * - The calculated average value matches the expected value within the allowed
     *   delta (0.1 in this case).
     * - The bullish trend identification (`true` or `false`) aligns with the expectation.
     *
     * Test cases:
     * - Tests executed for three specific indices corresponding to different dates.
     * - Verifies the correctness of both the average value and the bullish flag.
     */
    @Test
    public void average() {
        int index;
        double value;
        boolean bullish;

        // Date: 2025-Dec-11 11:00
        index = data.getEndIndex() - (20 * 24) - 12;
        value = indicator.average().getValue(index).doubleValue();
        assertEquals(91401.3, value, 0.1);
        bullish = indicator.average().bullish(index);
        assertFalse(bullish);

        // Date: 2025-Dec-21 23:00
        index = data.getEndIndex() - (10 * 24);
        value = indicator.average().getValue(index).doubleValue();
        assertEquals(87572.2, value, 0.1);
        bullish = indicator.average().bullish(index);
        assertTrue(bullish);

        // Date: 2025-Dec-31 23:00
        index = data.getEndIndex();
        value = indicator.average().getValue(index).doubleValue();
        assertEquals(88196.5, value, 0.1);
        bullish = indicator.average().bullish(index);
        assertFalse(bullish);
    }

    /**
     * Validates the functionality of the upper band method in the G-Channel indicator,
     * which calculates the values of the upper band based on the underlying data series
     * and verifies the bullish trend determination for given historical indices.
     *
     * The method performs unit tests for multiple points in time, ensuring:
     * - The calculated upper band values from the indicator align with the expected results.
     * - The bullish trend determination for given indices is accurate.
     *
     * Preconditions:
     * - The `indicator` object must be correctly initialized with valid data.
     * - The underlying `data` series must contain sufficient historical data for the
     *   calculation of the upper band for the specific indices being tested.
     *
     * Asserts:
     * - The calculated value of the upper band must match the expected value within a specified
     *   delta (0.1 in this case).
     * - The bullish trend identification (true or false) aligns with the expected outcome.
     *
     * Test cases:
     * - The method tests three historical indices corresponding to specific dates and times.
     * - Both the upper band values and the bullish trend classifications are validated.
     */
    @Test
    public void upper() {
        int index;
        double value;
        boolean bullish;

        // Date: 2025-Dec-11 11:00
        index = data.getEndIndex() - (20 * 24) - 12;
        value = indicator.upper().getValue(index).doubleValue();
        assertEquals(92714.1, value, 0.1);
        bullish = indicator.average().bullish(index);
        assertFalse(bullish);

        // Date: 2025-Dec-21 23:00
        index = data.getEndIndex() - (10 * 24);
        value = indicator.upper().getValue(index).doubleValue();
        assertEquals(88608.9, value, 0.1);
        bullish = indicator.average().bullish(index);
        assertTrue(bullish);

        // Date: 2025-Dec-31 23:00
        index = data.getEndIndex();
        value = indicator.upper().getValue(index).doubleValue();
        assertEquals(88827.0, value, 0.1);
        bullish = indicator.average().bullish(index);
        assertFalse(bullish);
    }

    /**
     * Validates the functionality of the lower band method in the G-Channel indicator,
     * which calculates the values of the lower band based on the underlying data series
     * and verifies the bullish trend determination for given historical indices.
     *
     * The method performs unit tests for multiple points in time, ensuring:
     * - The calculated lower band values from the indicator align with the expected results.
     * - The bullish trend determination for given indices is accurate.
     *
     * Preconditions:
     * - The `indicator` object must be properly initialized with valid data.
     * - The underlying `data` series must contain sufficient historical data for the
     *   calculation of the lower band for the specific indices being tested.
     *
     * Asserts:
     * - The calculated value of the lower band must match the expected value within a specified
     *   delta (0.1 in this case).
     * - The bullish trend identification (`true` or `false`) aligns with the expected outcome.
     *
     * Test cases:
     * - The method tests three specific historical indices corresponding to different dates and times.
     * - Both the lower band values and the bullish trend classifications are validated.
     */
    @Test
    public void lower() {
        int index;
        double value;
        boolean bullish;

        // Date: 2025-Dec-11 11:00
        index = data.getEndIndex() - (20 * 24) - 12;
        value = indicator.lower().getValue(index).doubleValue();
        assertEquals(90088.5, value, 0.1);
        bullish = indicator.average().bullish(index);
        assertFalse(bullish);

        // Date: 2025-Dec-21 23:00
        index = data.getEndIndex() - (10 * 24);
        value = indicator.lower().getValue(index).doubleValue();
        assertEquals(86535.5, value, 0.1);
        bullish = indicator.average().bullish(index);
        assertTrue(bullish);

        // Date: 2025-Dec-31 23:00
        index = data.getEndIndex();
        value = indicator.lower().getValue(index).doubleValue();
        assertEquals(87566.0, value, 0.1);
        bullish = indicator.average().bullish(index);
        assertFalse(bullish);
    }
}
