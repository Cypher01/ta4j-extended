package org.ta4j.core.indicators.wae;

import static org.ta4j.core.TestUtils.assertNumEquals;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TestdataReader;
import org.ta4j.core.indicators.ATRIndicatorPlus;
import org.ta4j.core.indicators.AbstractIndicatorTest;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

public class WAEDeadZoneIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    private BarSeries data;

    public WAEDeadZoneIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv");
    }

    @Test
    public void test() {
        final var atrRmaIndicator = new ATRIndicatorPlus(data, 100, ATRIndicatorPlus.Input.SMA);
        final var indicator = new WAEDeadZoneIndicator(data, 100, 3.7, atrRmaIndicator);

        assertNumEquals(10872.2058, indicator.getValue(data.getBarCount() - 3));
        assertNumEquals(10999.9002, indicator.getValue(data.getBarCount() - 2));
        assertNumEquals(11052.776899999999, indicator.getValue(data.getEndIndex()));
    }
}
