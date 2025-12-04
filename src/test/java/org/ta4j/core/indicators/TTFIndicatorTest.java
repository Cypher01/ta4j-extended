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

public class TTFIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    public TTFIndicatorTest(NumFactory numFactory) {
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
        var indicator = new TTFIndicator(data, 15);

        // TV: -88.2
        assertNumEquals(-88.22880518465749, indicator.getValue(data.getEndIndex() - 90));
        // TV: 57.7
        assertNumEquals(57.77777777777778, indicator.getValue(data.getEndIndex() - 2));
        // TV: 70.4
        assertNumEquals(70.41235326780858, indicator.getValue(data.getEndIndex() - 1));
        // TV: 69.9
        assertNumEquals(69.87527346080634, indicator.getValue(data.getEndIndex()));
    }
}
