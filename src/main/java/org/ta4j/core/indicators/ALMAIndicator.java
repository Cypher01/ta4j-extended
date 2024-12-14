package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

/**
 * Arnaud Legoux moving average (ALMA) indicator, based on PineScript v5 Reference Manual.
 * <a href="https://www.tradingview.com/pine-script-reference/v5/#fun_ta.alma">TradingView</a>
 */
public class ALMAIndicator extends CachedIndicator<Num> {
    private final Indicator<Num> indicator;
    private final int barCount;
    private final double m;
    private final int s;

    public ALMAIndicator(BarSeries series, int barCount, double offset, int sigma) {
        this(new ClosePriceIndicator(series), barCount, offset, sigma);
    }

    public ALMAIndicator(Indicator<Num> indicator, int barCount, double offset, int sigma) {
        super(indicator);

        this.indicator = indicator;
        this.barCount = barCount;
        this.m = offset * (barCount - 1);
        this.s = barCount / sigma;
    }

    @Override protected Num calculate(int index) {
        Num norm = getBarSeries().numFactory().zero();
        Num sum = getBarSeries().numFactory().zero();

        for (int i = barCount - 1; i >= 0; i--) {
            int valueIndex = index - barCount + i + 1;
            if (valueIndex < 0) {
                break;
            }
            Num weight = getBarSeries().numFactory().numOf(
                    Math.exp(-1d * Math.pow(i - m, 2d) / (2d * Math.pow(s, 2d))));
            norm = norm.plus(weight);
            sum = sum.plus(indicator.getValue(valueIndex).multipliedBy(weight));
        }

        return sum.dividedBy(norm);
    }

    @Override public int getCountOfUnstableBars() {
        return barCount;
    }
}
