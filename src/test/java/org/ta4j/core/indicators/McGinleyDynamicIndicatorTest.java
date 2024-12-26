package org.ta4j.core.indicators;

import static org.ta4j.core.TestUtils.assertNumEquals;

import java.io.IOException;
import java.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TestdataReader;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

public class McGinleyDynamicIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    public McGinleyDynamicIndicatorTest(NumFactory numFactory) {
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
        var indicator = new McGinleyDynamicIndicator(new ClosePriceIndicator(data), 14);

        assertNumEquals(88023.3195151717, indicator.getValue(data.getBarCount() - 3));
        assertNumEquals(88634.93106574699, indicator.getValue(data.getBarCount() - 2));
        assertNumEquals(89242.3235704304, indicator.getValue(data.getEndIndex()));
    }
}
