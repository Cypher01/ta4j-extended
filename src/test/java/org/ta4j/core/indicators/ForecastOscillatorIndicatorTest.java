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

public class ForecastOscillatorIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    public ForecastOscillatorIndicatorTest(NumFactory numFactory) {
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
        var indicator = new ForecastOscillatorIndicator(data, 14);

        assertNumEquals(-3.8549163466307017, indicator.getValue(data.getEndIndex() - 7));

        assertNumEquals(0.4493482752680229, indicator.getValue(data.getEndIndex() - 3));
        assertNumEquals(2.9232588613197645, indicator.getValue(data.getEndIndex() - 2));
        assertNumEquals(3.090217710526818, indicator.getValue(data.getEndIndex() - 1));
        assertNumEquals(1.8325349415818988, indicator.getValue(data.getEndIndex()));
    }
}
