package org.ta4j.core.indicators;

import static org.ta4j.core.TestUtils.assertNumEquals;

import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.mocks.MockBarSeriesBuilder;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

public class ZeroLagMACDIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {

    public ZeroLagMACDIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    private BarSeries data;

    @Before
    public void setUp() {
        data = new MockBarSeriesBuilder().withNumFactory(numFactory).withData(37.08, 36.7, 36.11, 35.85, 35.71, 36.04,
                36.41, 37.67, 38.01, 37.79, 36.83, 37.10, 38.01, 38.50, 38.99).build();
    }

    @Test
    public void macdUsingPeriod5And10() {
        var macdIndicator = new ZeroLagMACDIndicator(new ClosePriceIndicator(data), 5, 10, 7);
        assertNumEquals(0.0, macdIndicator.getValue(0));
        assertNumEquals(-0.08549127640036147, macdIndicator.getValue(1));
        assertNumEquals(-0.23831844616968567, macdIndicator.getValue(2));
        assertNumEquals(-0.31702815786211147, macdIndicator.getValue(3));
        assertNumEquals(-0.32208518897459726, macdIndicator.getValue(4));
        assertNumEquals(-0.18636731686530084, macdIndicator.getValue(5));
        assertNumEquals(-0.003278836935116658, macdIndicator.getValue(6));
        assertNumEquals(0.3706863098048956, macdIndicator.getValue(7));
        assertNumEquals(0.5514884535780809, macdIndicator.getValue(8));
        assertNumEquals(0.4923045319490953, macdIndicator.getValue(9));
        assertNumEquals(0.17196858594532216, macdIndicator.getValue(10));

        //        assertNumEquals(36.248333333333335, macdIndicator.getLongTermZLEma().getValue(5));
        //        assertNumEquals(36.11666666666667, macdIndicator.getShortTermZLEma().getValue(5));

        //        assertNumEquals(37.1689256198347, macdIndicator.getLongTermZLEma().getValue(10));
        //        assertNumEquals(37.25696844993142, macdIndicator.getShortTermZLEma().getValue(10));
    }
}
