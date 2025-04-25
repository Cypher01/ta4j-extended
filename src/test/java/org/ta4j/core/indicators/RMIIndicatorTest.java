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

public class RMIIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    public RMIIndicatorTest(NumFactory numFactory) {
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
        var indicator = new RMIIndicator(data, 14, 3);

        // TV: 19.3
        assertNumEquals(19.264814213181978, indicator.getValue(data.getEndIndex() - 163));
        // TV: 77.4
        assertNumEquals(77.41018506225824, indicator.getValue(data.getEndIndex() - 2));
        // TV: 79.9
        assertNumEquals(79.94257682106146, indicator.getValue(data.getEndIndex() - 1));
        // TV: 82.1
        assertNumEquals(82.09754942143394, indicator.getValue(data.getEndIndex()));
    }
}
