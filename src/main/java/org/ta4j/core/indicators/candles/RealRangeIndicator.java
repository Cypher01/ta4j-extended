package org.ta4j.core.indicators.candles;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Real (candle) full range indicator. This is based on {@link RealBodyIndicator}.
 */
public class RealRangeIndicator extends CachedIndicator<Num> {
    public RealRangeIndicator(BarSeries series) {
        super(series);
    }

    @Override
    protected Num calculate(int index) {
        Bar t = getBarSeries().getBar(index);
        return t.getHighPrice().minus(t.getLowPrice());
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}
