package org.ta4j.core.indicators.wae;

import static org.ta4j.core.TestUtils.assertNumEquals;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TestdataReader;
import org.ta4j.core.indicators.AbstractIndicatorTest;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

public class WAETrendIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    private BarSeries data;

    public WAETrendIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv");
    }

    @Test
    public void test() {
        final var indicator = new WAETrendIndicator(data, 150, 20, 40);

        assertNumEquals(9170.116403274005, indicator.getValue(data.getBarCount() - 3));
        assertNumEquals(15048.793459335866, indicator.getValue(data.getBarCount() - 2));
        assertNumEquals(8970.633790003922, indicator.getValue(data.getEndIndex()));
    }
}
