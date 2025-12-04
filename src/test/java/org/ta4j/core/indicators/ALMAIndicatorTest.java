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

public class ALMAIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    public ALMAIndicatorTest(NumFactory numFactory) {
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
        final var indicator = new ALMAIndicator(data, 9, 0.85, 6);

        assertNumEquals(101921.651272, indicator.getValue(data.getBarCount() - 3));
        assertNumEquals(103609.348755, indicator.getValue(data.getBarCount() - 2));
        assertNumEquals(105192.983739, indicator.getValue(data.getEndIndex()));
    }
}
