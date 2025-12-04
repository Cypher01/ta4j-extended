package org.ta4j.core.indicators;

import static org.junit.Assert.assertTrue;
import static org.ta4j.core.TestUtils.assertNumEquals;

import java.io.IOException;
import java.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TestdataReader;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

public class SqueezeMomentumIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    public SqueezeMomentumIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    private Duration duration = Duration.ofDays(1);
    private BarSeries data;

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv", duration);
    }

    @Test
    public void test() {
        var indicator = new SqueezeMomentumIndicator(data, 20, 2d, 20, 1.5);

        // must be -871.8
        assertNumEquals(-959.2427619047812, indicator.getValue(data.getBarCount() - 8));
        assertTrue(indicator.squeezeOn(data.getBarCount() - 8));
        // must be -297.88
        assertNumEquals(-464.27066666667815, indicator.getValue(data.getBarCount() - 7));
        assertTrue(indicator.squeezeOn(data.getBarCount() - 7));
        // must be 193.4
        assertNumEquals(-29.559333333338145, indicator.getValue(data.getBarCount() - 6));
        assertTrue(indicator.squeezeOn(data.getBarCount() - 6));

        // must be 2490.2
        assertNumEquals(2284.4791666666642, indicator.getValue(data.getBarCount() - 3));
        assertTrue(indicator.squeezeOn(data.getBarCount() - 3));
        // must be 3122.1
        assertNumEquals(2909.6616190476198, indicator.getValue(data.getBarCount() - 2));
        assertTrue(indicator.squeezeOn(data.getBarCount() - 2));
        // must be 3941.2
        assertNumEquals(3742.5546428571415, indicator.getValue(data.getEndIndex()));
        assertTrue(indicator.squeezeOff(data.getEndIndex()));
    }
}
