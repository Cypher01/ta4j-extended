package org.ta4j.core.indicators.helpers;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.num.Num;

/**
 * Change indicator aka. momentum indicator. Compared to the rate of change (ROC) indicator, this indicator calculates
 * the absolute change value, whereas the ROC indicator calculates the relative change (percentage).
 */
public class ChangeIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> changeIndicator;
    private final Indicator<Num> indicator;
    private final int barCount;

    public ChangeIndicator(BarSeries series) {
        this(series, 1);
    }

    public ChangeIndicator(Indicator<Num> indicator) {
        this(indicator, 1);
    }

    public ChangeIndicator(BarSeries series, int barCount) {
        this(new ClosePriceIndicator(series), barCount);
    }

    public ChangeIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator.getBarSeries());

        this.changeIndicator = CombineIndicator.minus(indicator, new PreviousValueIndicator(indicator, barCount));
        this.indicator = indicator;
        this.barCount = barCount;
    }

    @Override public Num getValue(int index) {
        if (index < barCount) {
            return getBarSeries().numFactory().zero();
        }

        return changeIndicator.getValue(index);
    }

    @Override public int getCountOfUnstableBars() {
        return Math.max(barCount, indicator.getCountOfUnstableBars());
    }
}
