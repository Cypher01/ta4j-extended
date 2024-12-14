package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ChangeIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * True Strength Index (TSI) indicator, based on PineScript v5 Reference Manual.
 * <a href="https://www.tradingview.com/pine-script-reference/v3/#fun_tsi">TradingView</a>
 */
public class TSIIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> tsiIndicator;
    private final int unstableBars;

    public TSIIndicator(BarSeries series, int longLength, int shortLength) {
        this(new ClosePriceIndicator(series), longLength, shortLength);
    }

    public TSIIndicator(Indicator<Num> indicator, int longLength, int shortLength) {
        super(indicator.getBarSeries());

        ChangeIndicator priceChange = new ChangeIndicator(indicator);
        EMAIndicator priceChangeDoubleSmoothed = new EMAIndicator(new EMAIndicator(priceChange, longLength),
                shortLength);
        EMAIndicator priceChangeAbsDoubleSmoothed = new EMAIndicator(
                new EMAIndicator(TransformIndicator.abs(priceChange), longLength), shortLength);

        this.tsiIndicator = TransformIndicator.multiply(
                CombineIndicator.divide(priceChangeDoubleSmoothed, priceChangeAbsDoubleSmoothed), 100);
        this.unstableBars = Math.max(longLength, shortLength);
    }

    @Override public Num getValue(int index) {
        return tsiIndicator.getValue(index);
    }

    @Override public int getCountOfUnstableBars() {
        return unstableBars;
    }
}
