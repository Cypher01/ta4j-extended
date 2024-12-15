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

    @Test(expected = IllegalArgumentException.class)
    public void throwsErrorOnIllegalArguments() {
        new ZeroLagMACDIndicator(new ClosePriceIndicator(data), 10, 5);
    }

    @Test
    public void macdUsingPeriod5And10() {
        var macdIndicator = new ZeroLagMACDIndicator(new ClosePriceIndicator(data), 5, 10);
        assertNumEquals(0.0, macdIndicator.getValue(0));
        assertNumEquals(0.0, macdIndicator.getValue(1));
        assertNumEquals(0.0, macdIndicator.getValue(2));
        assertNumEquals(0.0, macdIndicator.getValue(3));
        assertNumEquals(-0.23000000000000398, macdIndicator.getValue(4));
        assertNumEquals(-0.1316666666666677, macdIndicator.getValue(5));
        assertNumEquals(0.17634920634920093, macdIndicator.getValue(6));
        assertNumEquals(0.9522685185185154, macdIndicator.getValue(7));
        assertNumEquals(1.5156790123456787, macdIndicator.getValue(8));
        assertNumEquals(0.9095435839880324, macdIndicator.getValue(9));
        assertNumEquals(0.0880428300967182, macdIndicator.getValue(10));

        assertNumEquals(36.248333333333335, macdIndicator.getLongTermZLEma().getValue(5));
        assertNumEquals(36.11666666666667, macdIndicator.getShortTermZLEma().getValue(5));

        assertNumEquals(37.1689256198347, macdIndicator.getLongTermZLEma().getValue(10));
        assertNumEquals(37.25696844993142, macdIndicator.getShortTermZLEma().getValue(10));
    }
}
