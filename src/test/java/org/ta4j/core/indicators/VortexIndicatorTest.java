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

public class VortexIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    private BarSeries data;

    public VortexIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv");
    }

    @Test
    public void test() {
        var indicator = new VortexIndicator(data, 14);

        assertNumEquals(1.0838, indicator.getViPlus(data.getEndIndex() - 2));
        assertNumEquals(0.8870, indicator.getViMinus(data.getEndIndex() - 2));
        assertNumEquals(1.1334, indicator.getViPlus(data.getEndIndex() - 1));
        assertNumEquals(0.8556, indicator.getViMinus(data.getEndIndex() - 1));
        assertNumEquals(1.1754, indicator.getViPlus(data.getEndIndex()));
        assertNumEquals(0.8195, indicator.getViMinus(data.getEndIndex()));
    }
}
