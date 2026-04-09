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

public class FantailVMAIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    private BarSeries data;

    public FantailVMAIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv");
    }

    @Test
    public void test() {
        var indicator = new FantailVMAIndicator(data, 2, 10, 6);

        assertNumEquals(100575.13812438415, indicator.getValue(data.getBarCount() - 3));
        assertNumEquals(101244.0238346954, indicator.getValue(data.getBarCount() - 2));
        assertNumEquals(102067.44049672519, indicator.getValue(data.getEndIndex()));
    }
}
