package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.NzIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.indicators.helpers.TypicalPriceIndicator;
import org.ta4j.core.num.Num;

/**
 * WaveTrend K indicator by LazyBear.
 * <a href="https://www.tradingview.com/script/2KE8wTuF-Indicator-WaveTrend-Oscillator-WT/">TradingView</a>
 */
public class WaveTrendKIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> indicator;
    private final int unstableBars;

    public WaveTrendKIndicator(BarSeries series) {
        this(series, 10, 21);
    }

    public WaveTrendKIndicator(BarSeries series, int barCountChannel, int barCountAverage) {
        super(series);

        TypicalPriceIndicator ap = new TypicalPriceIndicator(series);
        EMAIndicator esa = new EMAIndicator(ap, barCountChannel);
        CombineIndicator apMinusEsa = CombineIndicator.minus(ap, esa);
        EMAIndicator d = new EMAIndicator(TransformIndicator.abs(apMinusEsa), barCountChannel);
        TransformIndicator divisor = TransformIndicator.multiply(d, 0.015);
        NzIndicator ci = new NzIndicator(CombineIndicator.divide(apMinusEsa, divisor));
        this.indicator = new EMAIndicator(ci, barCountAverage);
        this.unstableBars = Math.max(barCountChannel, barCountAverage);
    }

    @Override public Num getValue(int index) {
        return indicator.getValue(index);
    }

    @Override public int getCountOfUnstableBars() {
        return unstableBars;
    }
}
