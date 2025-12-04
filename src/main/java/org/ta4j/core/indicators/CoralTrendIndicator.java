package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.numeric.BinaryOperationIndicator;
import org.ta4j.core.num.Num;

/**
 * Coral Trend indicator by LazyBear.
 * <a href="https://www.tradingview.com/script/qyUwc2Al-Coral-Trend-Indicator-LazyBear/">TradingView</a>
 */
public class CoralTrendIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> coralTrendIndicator;
    private final int smoothingPeriod;

    public CoralTrendIndicator(BarSeries series, int smoothingPeriod, double constantD) {
        this(new ClosePriceIndicator(series), smoothingPeriod, constantD);
    }

    public CoralTrendIndicator(Indicator<Num> indicator, int smoothingPeriod, double constantD) {
        super(indicator.getBarSeries());

        double di = (smoothingPeriod - 1) / 2d + 1d;
        double c1 = 2d / (di + 1d);
        double c2 = 1d - c1;
        double c3 = 3d * (constantD * constantD + constantD * constantD * constantD);
        double c4 = -3d * (2d * constantD * constantD + constantD + constantD * constantD * constantD);
        double c5 = 3d * constantD + 1d + constantD * constantD * constantD + 3d * constantD * constantD;
        IIndicator i1 = new IIndicator(indicator, c1, c2);
        IIndicator i2 = new IIndicator(i1, c1, c2);
        IIndicator i3 = new IIndicator(i2, c1, c2);
        IIndicator i4 = new IIndicator(i3, c1, c2);
        IIndicator i5 = new IIndicator(i4, c1, c2);
        IIndicator i6 = new IIndicator(i5, c1, c2);

        BinaryOperationIndicator addend1 = BinaryOperationIndicator.product(i6, -constantD * constantD * constantD);
        BinaryOperationIndicator addend2 = BinaryOperationIndicator.product(i5, c3);
        BinaryOperationIndicator addend3 = BinaryOperationIndicator.product(i4, c4);
        BinaryOperationIndicator addend4 = BinaryOperationIndicator.product(i3, c5);

        this.coralTrendIndicator = BinaryOperationIndicator.sum(BinaryOperationIndicator.sum(BinaryOperationIndicator.sum(addend1, addend2), addend3), addend4);
        this.smoothingPeriod = smoothingPeriod;
    }

    @Override
    public Num getValue(int index) {
        return coralTrendIndicator.getValue(index);
    }

    @Override
    public int getCountOfUnstableBars() {
        return smoothingPeriod;
    }

    private static class IIndicator extends CachedIndicator<Num> {
        private final BinaryOperationIndicator addend1;
        private final Num c2;

        private IIndicator(Indicator<Num> indicator, double c1, double c2) {
            super(indicator);

            this.addend1 = BinaryOperationIndicator.product(indicator, c1);
            this.c2 = indicator.getBarSeries().numFactory().numOf(c2);
        }

        @Override
        protected Num calculate(int index) {
            Num addend1Value = addend1.getValue(index);

            if (index == 0) {
                return addend1Value;
            }

            return addend1Value.plus(c2.multipliedBy(getValue(index - 1)));
        }

        @Override
        public int getCountOfUnstableBars() {
            return 0;
        }
    }
}
