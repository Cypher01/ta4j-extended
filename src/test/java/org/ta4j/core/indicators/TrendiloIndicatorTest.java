package org.ta4j.core.indicators;

import static org.ta4j.core.TestUtils.assertNumEquals;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TestdataReader;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

public class TrendiloIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    private BarSeries data;

    public TrendiloIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv");
    }

    @Test
    public void test() {
        final var indicator = new TrendiloIndicator(data, 50, 1, 0.85, 6);

        assertNumEquals(-0.5743, indicator.getValue(data.getBarCount() - 103));
        assertNumEquals(1.4231, indicator.getValue(data.getBarCount() - 30));
        assertNumEquals(0.4075, indicator.getValue(data.getBarCount() - 3));
        assertNumEquals(0.4695, indicator.getValue(data.getBarCount() - 2));
        assertNumEquals(0.4778, indicator.getValue(data.getEndIndex()));
    }
}
