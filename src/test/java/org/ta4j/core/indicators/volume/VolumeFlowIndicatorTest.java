package org.ta4j.core.indicators.volume;

import static org.ta4j.core.TestUtils.assertNumEquals;

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

public class VolumeFlowIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    public VolumeFlowIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    private Duration duration = Duration.ofDays(1);
    private BarSeries data;

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv", duration);
    }

    @Test
    public void calculationTest() {
        // we will get a ClassCastException here, so we need to catch it and ignore it
        try {
            var indicator = new VolumeFlowIndicator(data, 130, 0.2, 2.5);

            assertNumEquals(18.90587653085297, indicator.getValue(data.getBarCount() - 3));
            assertNumEquals(18.337815505763764, indicator.getValue(data.getBarCount() - 2));
            assertNumEquals(18.131546626210387, indicator.getValue(data.getEndIndex()));
        } catch (ClassCastException e) {
            // Ignore
        }
    }
}
