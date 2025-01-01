package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Trendilo indicator by dudeowns.
 * <a href="https://www.tradingview.com/script/h5kMWewu-Trendilo-OPEN-SOURCE/">TradingView</a>
 */
public class TrendiloIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> avpch;

    public TrendiloIndicator(BarSeries series, int barCount, int smoothing, double offset, int sigma) {
        this(new ClosePriceIndicator(series), barCount, smoothing, offset, sigma);
    }

    public TrendiloIndicator(Indicator<Num> indicator, int barCount, int smoothing, double offset, int sigma) {
        super(indicator.getBarSeries());

        CombineIndicator ch = CombineIndicator.minus(indicator, new PreviousValueIndicator(indicator, smoothing));
        TransformIndicator pch = TransformIndicator.multiply(CombineIndicator.divide(ch, indicator), 100d);
        this.avpch = new ALMAIndicator(pch, barCount, offset, sigma);
    }

    @Override public Num getValue(int index) {
        return avpch.getValue(index);
    }

    @Override public int getCountOfUnstableBars() {
        return avpch.getCountOfUnstableBars();
    }
}
