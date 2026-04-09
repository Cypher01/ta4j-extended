package org.ta4j.core.indicators.volume;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TestdataReader;
import org.ta4j.core.indicators.AbstractIndicatorTest;
import org.ta4j.core.num.DecimalNumFactory;
import org.ta4j.core.num.DoubleNumFactory;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

public class VolumeFlowIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    private BarSeries data;

    public VolumeFlowIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1h.csv", Duration.ofHours(1));
    }

    @Test
    public void calculationTest() {
        var indicator = new VolumeFlowIndicator(data, 130, 0.2, 2.5);

        // TV: -0.9 Date: 2025-Dec-31 23:00
        Num value1 = indicator.getValue(data.getEndIndex());
        assertEquals(-0.9, value1.doubleValue(), 0.1);

        // TV: 0.8 Date: 2025-Dec-31 22:00
        Num value2 = indicator.getValue(data.getEndIndex() - 1);
        assertEquals(0.8, value2.doubleValue(), 0.1);

        // TV: 12.1 Date: 2025-Dec-31 11:00
        Num value3 = indicator.getValue(data.getEndIndex() - 12);

        // the value differs in the case of a DoubleNumFactory and DecimalNumFactory due to the different precision
        // of the underlying number types
        if (numFactory instanceof DoubleNumFactory) {
            assertEquals(12.1, value3.doubleValue(), 0.1);
        } else if (numFactory instanceof DecimalNumFactory) {
            assertEquals(10.7, value3.doubleValue(), 0.1);
        }
    }
}
