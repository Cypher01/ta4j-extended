package org.ta4j.core.indicators;

import static org.ta4j.core.TestUtils.assertNumEquals;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.TestdataReader;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

public class HACOLTIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {
    private final Duration duration = Duration.ofDays(1);
    private BarSeries data;

    public HACOLTIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() throws IOException {
        data = new TestdataReader(numFactory).readCsv("btcusdt-1d.csv");
    }

    @Test
    public void test() {
        var indicator = new HACOLTIndicator(data, 55, 60, 1.1);

        int index;
        Instant startDate;

        // TV: 1.0; Date: 2024-Jul-31
        index = data.getEndIndex() - 139;
        startDate = data.getBar(index).getEndTime().minus(duration);
        assertNumEquals(1.0, indicator.getValue(index));

        // TV: 1.0; Date: 2024-Aug-01
        index = data.getEndIndex() - 138;
        startDate = data.getBar(index).getEndTime().minus(duration);
        assertNumEquals(1.0, indicator.getValue(index));

        // TV: 1.0; Date: 2024-Aug-02
        index = data.getEndIndex() - 137;
        startDate = data.getBar(index).getEndTime().minus(duration);
        assertNumEquals(1.0, indicator.getValue(index));

        // TV: -1.0; Date: 2024-Aug-03
        index = data.getEndIndex() - 136;
        startDate = data.getBar(index).getEndTime().minus(duration);
        assertNumEquals(-1.0, indicator.getValue(index));

        // TV: -1.0; Date: 2024-Aug-10
        index = data.getEndIndex() - 129;
        startDate = data.getBar(index).getEndTime().minus(duration);
        assertNumEquals(-1.0, indicator.getValue(index));

        // TV: -1.0; Date: 2024-Aug-23
        index = data.getEndIndex() - 116;
        startDate = data.getBar(index).getEndTime().minus(duration);
        assertNumEquals(-1.0, indicator.getValue(index));

        // TV: 1.0; Date: 2024-Aug-24
        index = data.getEndIndex() - 115;
        startDate = data.getBar(index).getEndTime().minus(duration);
        assertNumEquals(1.0, indicator.getValue(index));
    }
}
