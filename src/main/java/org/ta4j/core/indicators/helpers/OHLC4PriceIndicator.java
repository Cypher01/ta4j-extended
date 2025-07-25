package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * OHLC4 (open/high/low/close average) price indicator.
 */
public class OHLC4PriceIndicator extends CachedIndicator<Num> {
    public OHLC4PriceIndicator(BarSeries series) {
        super(series);
    }

    @Override protected Num calculate(int index) {
        Bar bar = getBarSeries().getBar(index);

        Num openPrice = bar.getOpenPrice();
        Num highPrice = bar.getHighPrice();
        Num lowPrice = bar.getLowPrice();
        Num closePrice = bar.getClosePrice();

        return openPrice.plus(highPrice).plus(lowPrice).plus(closePrice).dividedBy(
                getBarSeries().numFactory().numOf(4));
    }

    @Override public int getCountOfUnstableBars() {
        return 0;
    }
}
