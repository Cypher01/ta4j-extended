package org.ta4j.core.indicators.volume;

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

import java.io.IOException;
import java.time.Duration;

import static org.ta4j.core.TestUtils.assertNumEquals;

public class VolumeFlowIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    public VolumeFlowIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    private final Duration duration = Duration.ofDays(1);
    private BarSeries data;

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv", duration);
    }

    @Test
    public void calculationTest() {
        var indicator = new VolumeFlowIndicator(data, 130, 0.2, 2.5);

        Num value1 = indicator.getValue(data.getBarCount() - 3);
        Num value2 = indicator.getValue(data.getBarCount() - 2);
        Num value3 = indicator.getValue(data.getEndIndex());

        // different results for DoubleNum and DecimalNum
        if (numFactory instanceof DoubleNumFactory) {
            assertNumEquals(18.90587653085297, value1);
            assertNumEquals(18.337815505763764, value2);
            assertNumEquals(18.131546626210387, value3);
        }
        if (numFactory instanceof DecimalNumFactory) {
            assertNumEquals(18.56786747000685, value1);
            assertNumEquals(17.99783319821328, value2);
            assertNumEquals(17.78978074578313, value3);
        }
    }
}
