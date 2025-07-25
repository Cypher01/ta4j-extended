package org.ta4j.core.indicators;

import java.util.ArrayList;
import java.util.List;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.Indicator;

/**
 * Smoothed Heikin Ashi indicator by jackvmk.
 * <a href="https://de.tradingview.com/script/ROokknI2-Smoothed-Heiken-Ashi-Candles-v1/">TradingView</a>
 */
public class SmoothedHeikinAshiIndicator extends AbstractIndicator<Bar> {
    private final Indicator<Bar> smoothedHeikinAshiIndicator;
    private final int barCount;

    public SmoothedHeikinAshiIndicator(BarSeries series, int barCount) {
        super(series);

        this.smoothedHeikinAshiIndicator = new SmoothedBarIndicator(new HeikinAshiIndicator(new SmoothedBarIndicator(series, barCount)), barCount);
        this.barCount = barCount;
    }

    public BarSeries getHeikinAshiBarSeries() {
        return getHeikinAshiBarSeries(getBarSeries().getName() + "_SmoothedHeikinAshi");
    }

    public BarSeries getHeikinAshiBarSeries(String name) {
        List<Bar> bars = new ArrayList<>();

        for (int i = getBarSeries().getBeginIndex(); i <= getBarSeries().getEndIndex(); i++) {
            bars.add(getValue(i));
        }

        return new BaseBarSeriesBuilder().withName(name).withBars(bars).build();
    }

    @Override
    public Bar getValue(int index) {
        return smoothedHeikinAshiIndicator.getValue(index);
    }

    @Override
    public int getCountOfUnstableBars() {
        return barCount;
    }
}
