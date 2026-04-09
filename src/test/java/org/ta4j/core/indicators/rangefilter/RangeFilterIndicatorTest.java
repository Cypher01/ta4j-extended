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

public class RangeFilterIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    private BarSeries data;

    public RangeFilterIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv");
    }

    @Test
    public void calculationTest() {
        var indicator = new RangeFilterIndicator(data, 20, 3.0);

        int index;

        // TV: 64,909.1; Date: 2024-Jul-31
        index = data.getEndIndex() - 139;
        Num value1 = indicator.getValue(index);
        assertEquals(64909.1, value1.doubleValue(), 0.1);

        // TV: 100,974.3; Date: 2024-Dec-17
        index = data.getEndIndex();
        Num value2 = indicator.getValue(index);
        assertEquals(100974.3, value2.doubleValue(), 0.1);

    }
}
