package org.ta4j.core.indicators.rangefilter;

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

public class RangeFilterFallingIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    private BarSeries data;

    public RangeFilterFallingIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv");
    }

    @Test
    public void calculationTest() {
        var rangeFilterIndicator = new RangeFilterIndicator(data, 20, 3.0);
        var indicator = new RangeFilterFallingIndicator(rangeFilterIndicator);

        int index;

        // TV: 3292.9; Date: 2024-Jul-31
        index = data.getEndIndex() - 139;
        Num value1 = indicator.getValue(index);
        assertNumEquals(0, value1);

        // TV: 3292.9; Date: 2024-Aug-2
        index = data.getEndIndex() - 137;
        Num value2 = indicator.getValue(index);
        assertNumEquals(1, value2);
    }
}
