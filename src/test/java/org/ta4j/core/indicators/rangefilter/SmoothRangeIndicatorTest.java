package org.ta4j.core.indicators.rangefilter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TestdataReader;
import org.ta4j.core.indicators.AbstractIndicatorTest;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

public class SmoothRangeIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    private BarSeries data;

    public SmoothRangeIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv");
    }

    @Test
    public void calculationTest() {
        var indicator = new SmoothRangeIndicator(data, 20, 3.0);

        int index;

        // TV: 3292.9; Date: 2024-Jul-31
        index = data.getEndIndex() - 139;
        Num value1 = indicator.getValue(index);
        assertEquals(3292.9, value1.doubleValue(), 0.1);

        // TV: 5110.2; Date: 2024-Dec-17
        index = data.getEndIndex();
        Num value2 = indicator.getValue(index);
        assertEquals(5110.2, value2.doubleValue(), 0.1);

    }
}
