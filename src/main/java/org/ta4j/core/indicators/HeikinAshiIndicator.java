package org.ta4j.core.indicators;

import java.util.ArrayList;
import java.util.List;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.Indicator;
import org.ta4j.core.bars.TimeBarBuilder;
import org.ta4j.core.num.Num;

/**
 * Heikin Ashi indicator.
 */
public class HeikinAshiIndicator extends RecursiveCachedIndicator<Bar> {
    private final Indicator<Bar> indicator;

    public HeikinAshiIndicator(BarSeries series) {
        this(new AbstractIndicator<>(series) {
            @Override
            public Bar getValue(int index) {
                return series.getBar(index);
            }

            @Override
            public int getCountOfUnstableBars() {
                return 0;
            }
        });
    }

    public HeikinAshiIndicator(Indicator<Bar> indicator) {
        super(indicator);

        this.indicator = indicator;
    }

    public BarSeries getHeikinAshiBarSeries() {
        return getHeikinAshiBarSeries(getBarSeries().getName() + "_HeikinAshi");
    }

    public BarSeries getHeikinAshiBarSeries(String name) {
        List<Bar> bars = new ArrayList<>();

        for (int i = getBarSeries().getBeginIndex(); i <= getBarSeries().getEndIndex(); i++) {
            bars.add(getValue(i));
        }

        return new BaseBarSeriesBuilder().withName(name).withBars(bars).build();
    }

    @Override
    protected Bar calculate(int index) {
        if (index == 0) {
            return indicator.getValue(index);
        }

        Bar prevHaBar = getValue(index - 1);
        Bar bar = indicator.getValue(index);

        Num openPrice = bar.getOpenPrice();
        Num highPrice = bar.getHighPrice();
        Num lowPrice = bar.getLowPrice();
        Num closePrice = bar.getClosePrice();

        Num haOpenPrice = prevHaBar.getOpenPrice().plus(prevHaBar.getClosePrice()).dividedBy(
                getBarSeries().numFactory().numOf(2));
        Num haClosePrice = openPrice.plus(highPrice).plus(lowPrice).plus(closePrice).dividedBy(
                getBarSeries().numFactory().numOf(4));
        Num haHighPrice = highPrice.max(haClosePrice).max(haOpenPrice);
        Num haLowPrice = lowPrice.min(haClosePrice).min(haOpenPrice);

        return new TimeBarBuilder().timePeriod(bar.getTimePeriod())
                .endTime(bar.getEndTime())
                .openPrice(haOpenPrice)
                .highPrice(haHighPrice)
                .lowPrice(haLowPrice)
                .closePrice(haClosePrice)
                .volume(bar.getVolume())
                .amount(bar.getAmount())
                .trades(bar.getTrades())
                .build();
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}
