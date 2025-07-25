package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ChangeIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.NzIndicator;
import org.ta4j.core.num.Num;

/**
 * Holt Exponential Moving Average (HEMA) indicator by everget.
 * <a href="https://www.tradingview.com/script/Y9K4SHMj-Holt-Exponential-Moving-Average/">TradingView</a>
 */
public class HEMAIndicator extends CachedIndicator<Num> {
    private final Indicator<Num> indicator;
    private final Num alpha;
    private final NzIndicator bIndicator;
    private final int unstableBars;

    public HEMAIndicator(BarSeries series, int alphaLength, int gammaLength) {
        this(new ClosePriceIndicator(series), alphaLength, gammaLength);
    }

    public HEMAIndicator(Indicator<Num> indicator, int alphaLength, int gammaLength) {
        super(indicator);

        double alpha = 2d / (alphaLength + 1);
        double gamma = 2d / (gammaLength + 1);
        this.indicator = indicator;
        this.alpha = getBarSeries().numFactory().numOf(alpha);
        this.bIndicator = new NzIndicator(new BIndicator(this, gamma), indicator);
        this.unstableBars = Math.max(alphaLength, gammaLength);
    }

    @Override protected Num calculate(int index) {
        if (index == 0) {
            return getBarSeries().numFactory().zero();
        }

        return getBarSeries().numFactory().one().minus(alpha).multipliedBy(
                getValue(index - 1).plus(bIndicator.getValue(index - 1))).plus(
                alpha.multipliedBy(indicator.getValue(index)));
    }

    @Override public int getCountOfUnstableBars() {
        return unstableBars;
    }

    private static class BIndicator extends CachedIndicator<Num> {
        private final ChangeIndicator hemaChangeIndicator;
        private final Num gamma;

        public BIndicator(HEMAIndicator hemaIndicator, double gamma) {
            super(hemaIndicator);

            this.hemaChangeIndicator = new ChangeIndicator(hemaIndicator);
            this.gamma = getBarSeries().numFactory().numOf(gamma);
        }

        @Override protected Num calculate(int index) {
            if (index == 0) {
                return getBarSeries().numFactory().zero();
            }

            return getBarSeries().numFactory().one().minus(gamma).multipliedBy(getValue(index - 1)).plus(
                    gamma.multipliedBy(hemaChangeIndicator.getValue(index)));
        }

        @Override public int getCountOfUnstableBars() {
            return hemaChangeIndicator.getCountOfUnstableBars();
        }
    }
}
