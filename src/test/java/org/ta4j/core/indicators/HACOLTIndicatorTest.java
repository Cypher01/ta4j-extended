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
    public HACOLTIndicatorTest(NumFactory numFactory) {
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
        var indicator = new HACOLTIndicator(data, 55, 60, 1.1);

        int index = 0;
        Instant startDate;

        // TV: 1.0; Date: 2024-Aug-02
//        index = data.getEndIndex() - 137;
//        startDate = data.getBar(index).getEndTime().minus(duration);
//        assertNumEquals(1.0, indicator.getValue(index));
//
//        // TV: -1.0; Date: 2024-Aug-04
//        index = data.getEndIndex() - 135;
//        startDate = data.getBar(index).getEndTime().minus(duration);
//        assertNumEquals(-1.0, indicator.getValue(index));

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
        //index = data.getEndIndex() - 129;
        //startDate = data.getBar(index).getEndTime().minus(duration);
        //assertNumEquals(-1.0, indicator.getValue(index));

        //        // TV: 77.4
        //        assertNumEquals(77.41018506225824, indicator.getValue(data.getEndIndex() - 2));
        //        // TV: 79.9
        //        assertNumEquals(79.94257682106146, indicator.getValue(data.getEndIndex() - 1));
        // TV: 82.1
//        assertNumEquals(1.0, indicator.getValue(data.getEndIndex()));
    }
}
