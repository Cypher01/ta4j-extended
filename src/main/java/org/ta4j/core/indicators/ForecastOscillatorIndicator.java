package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.SimpleLinearRegressionIndicator;
import org.ta4j.core.num.Num;

/**
 * Forecast Oscillator Indicator.
 * <a href="https://www.tradingview.com/v/JkqUNW7J/">TradingView</a>
 */
/*
// This source code is subject to the terms of the Mozilla Public License 2.0 at https://mozilla.org/MPL/2.0/
// © KivancOzbilgic

//@version=4
study("Forecast Oscillator", overlay=false)
src = input(close)
len = input(defval=14, minval=1, title="Length")
lrc = linreg(src, len, 0)
lrc1 = linreg(src,len,1)
lrs = (lrc-lrc1)
TSF = linreg(src, len, 0)+lrs
fosc=100*(src-TSF[1])/src
col12 = fosc > fosc[1]
col32 = fosc < fosc[1]
color2 = col12 ? #21C400 : col32 ? #960012 : color.blue
plot(fosc, color = color2,linewidth=2, title = "TSF")
hline(0, linestyle=2, color=color.blue)
 */
public class ForecastOscillatorIndicator extends AbstractIndicator<Num> {
    private final int barCount;

    private final Indicator<Num> indicator;
    private final Indicator<Num> linregIndicator;
    private final Indicator<Num> linregIndicatorSlope;

    public ForecastOscillatorIndicator(BarSeries series, int barCount) {
        this(new ClosePriceIndicator(series), barCount);
    }

    /**
     * Constructor.
     *
     * @param indicator the indicator
     * @param barCount  the bar count
     */
    public ForecastOscillatorIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator.getBarSeries());

        this.barCount = barCount;
        this.indicator = indicator;

        linregIndicator = new SimpleLinearRegressionIndicator(indicator, barCount);
        linregIndicatorSlope = new SimpleLinearRegressionIndicator(indicator, barCount,
                SimpleLinearRegressionIndicator.SimpleLinearRegressionType.SLOPE);
    }

    /**
     * @param index the bar index
     *
     * @return the linreg value
     */
    private Num linreg(int index) {
        return linregIndicator.getValue(index);
    }

    /**
     * linreg1 = linreg - linregIndicatorSlope
     *
     * @param index the bar index
     *
     * @return the linreg1 value
     */
    private Num linreg1(int index) {
        final Num slope = linregIndicatorSlope.getValue(index);

        return linreg(index).minus(slope);
    }

    /**
     * TSF = linreg + (linreg - linreg1)
     *
     * @param index the bar index
     *
     * @return tsf value
     */
    private Num tsf(int index) {
        return linreg(index).plus(linreg(index).minus(linreg1(index)));
    }

    /**
     * fosc = 100 * (indicator - tsf1) / indicator
     *
     * @param index the bar index
     *
     * @return the forecast oscillator value
     */
    @Override
    public Num getValue(int index) {
        final var numFactory = getBarSeries().numFactory();

        final Num tsf1 = tsf(index - 1);
        final Num indicatorValue = indicator.getValue(index);

        return numFactory.numOf(100).multipliedBy(indicatorValue.minus(tsf1).dividedBy(indicatorValue));
    }

    @Override
    public int getCountOfUnstableBars() {
        return barCount;
    }
}
