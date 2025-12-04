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

public class TetherLineIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    public TetherLineIndicatorTest(NumFactory numFactory) {
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
        var indicator = new TetherLineIndicator(data, 50);

        assertNumEquals(86073.1, indicator.getValue(data.getEndIndex() - 2));
        assertNumEquals(87329, indicator.getValue(data.getEndIndex() - 1));
        assertNumEquals(87614.8, indicator.getValue(data.getEndIndex()));
    }
}
