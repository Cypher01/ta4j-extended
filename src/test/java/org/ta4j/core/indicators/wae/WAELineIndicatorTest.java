package org.ta4j.core.indicators.wae;

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

public class WAELineIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    public WAELineIndicatorTest(NumFactory numFactory) {
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
        final var indicator = new WAELineIndicator(data, 20, 2);

        assertNumEquals(11179.398559332243, indicator.getValue(data.getBarCount() - 3));
        assertNumEquals(11602.338338300615, indicator.getValue(data.getBarCount() - 2));
        assertNumEquals(12776.862679327809, indicator.getValue(data.getEndIndex()));
    }
}
