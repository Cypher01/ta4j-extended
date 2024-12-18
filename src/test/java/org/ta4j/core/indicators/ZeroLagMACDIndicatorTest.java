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

public class ZeroLagMACDIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {

    public ZeroLagMACDIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    private Duration duration = Duration.ofDays(1);
    private BarSeries data;

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv", duration);
    }

    @Test
    public void macdUsingPeriod5And10() {
        var indicator = new ZeroLagMACDIndicator(new ClosePriceIndicator(data), 12, 26, 9);
        int i = data.getEndIndex();

        // blue line
        assertNumEquals(-243.01264596292458, indicator.getValue(i));
        // red line
        assertNumEquals(-935.6700313276315, indicator.getSignalLine().getValue(i));
        // difference / histogram
        assertNumEquals(692.6573853647069, indicator.getHistogram().getValue(i));

        i--;
        // blue line
        assertNumEquals(-524.2822670615133, indicator.getValue(i));
        // red line
        assertNumEquals(-1302.386933149257, indicator.getSignalLine().getValue(i));
        // difference / histogram
        assertNumEquals(778.1046660877437, indicator.getHistogram().getValue(i));

        i--;
        // blue line
        assertNumEquals(-1024.576475175636, indicator.getValue(i));
        // red line
        assertNumEquals(-1668.5363887471062, indicator.getSignalLine().getValue(i));
        // difference / histogram
        assertNumEquals(643.9599135714702, indicator.getHistogram().getValue(i));
    }
}
