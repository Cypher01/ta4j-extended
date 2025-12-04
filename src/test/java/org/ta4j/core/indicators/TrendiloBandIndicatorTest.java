package org.ta4j.core.indicators;

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

public class TrendiloBandIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    public TrendiloBandIndicatorTest(NumFactory numFactory) {
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
        final var trendiloIndicator = new TrendiloIndicator(data, 50, 1, 0.85, 6);
        final var indicator = new TrendiloBandIndicator(trendiloIndicator, 1, 50);

        assertNumEquals(0.9958, indicator.getValue(data.getBarCount() - 260));
        assertNumEquals(0.4327, indicator.getValue(data.getBarCount() - 90));
        assertNumEquals(0.3695, indicator.getValue(data.getBarCount() - 60));
        assertNumEquals(0.6456, indicator.getValue(data.getBarCount() - 30));
        assertNumEquals(0.8902, indicator.getValue(data.getEndIndex()));
    }
}
