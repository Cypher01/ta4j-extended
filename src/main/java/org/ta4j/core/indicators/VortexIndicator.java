package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.num.Num;

/**
 * Vortex Indicator.
 * <a href="https://www.tradingview.com/script/3JW7XZfz-Vortex-Indicator-VTX/">TradingView</a>
 */
/*
//@version=6
indicator(title = "Vortex Indicator", shorttitle="VI", format=format.price, precision=4, timeframe="",
timeframe_gaps=true)
period_ = input.int(14, title="Length", minval=2)
VMP = math.sum( math.abs( high - low[1]), period_ )
VMM = math.sum( math.abs( low - high[1]), period_ )
STR = math.sum( ta.atr(1), period_ )
VIP = VMP / STR
VIM = VMM / STR
plot(VIP, title="VI +", color=#2962FF)
plot(VIM, title="VI -", color=#E91E63)
 */
public class VortexIndicator extends AbstractIndicator<Num> {
    private final int barCount;

    private final Indicator<Num> highMinusPrevLow;
    private final Indicator<Num> lowMinusPrevHigh;
    private final ATRIndicator atrIndicator;

    public VortexIndicator(BarSeries series, int barCount) {
        this(new ClosePriceIndicator(series), barCount);
    }

    /**
     * Constructor.
     *
     * @param indicator the indicator
     * @param barCount  the bar count
     */
    public VortexIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator.getBarSeries());

        this.barCount = barCount;

        LowPriceIndicator lowPriceIndicator = new LowPriceIndicator(getBarSeries());
        HighPriceIndicator highPriceIndicator = new HighPriceIndicator(getBarSeries());
        highMinusPrevLow = CombineIndicator.minus(highPriceIndicator, new PreviousValueIndicator(lowPriceIndicator, 1));
        lowMinusPrevHigh = CombineIndicator.minus(lowPriceIndicator, new PreviousValueIndicator(highPriceIndicator, 1));
        atrIndicator = new ATRIndicator(getBarSeries(), 1);
    }

    /**
     * return zero here, use getViPlus or getViMinus instead.
     *
     * @param index the bar index
     *
     * @return
     */
    @Override
    public Num getValue(int index) {
        return getBarSeries().numFactory().zero();
    }

    private int getStartIndex(int index) {
        int startIndex = index - barCount + 1;
        if (startIndex < 0) {
            startIndex = 0;
        }
        return startIndex;
    }

    private Num getHighMinusPrevLowFromBarCount(int index, int startIndex) {
        Num value = getBarSeries().numFactory().zero();
        for (int i = startIndex; i <= index; i++) {
            value = value.plus(highMinusPrevLow.getValue(i).abs());
        }
        return value;
    }

    private Num getLowMinusPrevHighFromBarCount(int index, int startIndex) {
        Num value = getBarSeries().numFactory().zero();
        for (int i = startIndex; i <= index; i++) {
            value = value.plus(lowMinusPrevHigh.getValue(i).abs());
        }
        return value;
    }

    private Num getTrueRangeFromBarCount(int index, int startIndex) {
        Num atr = getBarSeries().numFactory().zero();
        for (int i = startIndex; i <= index; i++) {
            atr = atr.plus(atrIndicator.getValue(i));
        }
        return atr;
    }

    /**
     * VI+.
     *
     * @param index the bar index
     *
     * @return the VI+ value
     */
    public Num getViPlus(int index) {
        int startIndex = getStartIndex(index);
        Num highMinusLowPrev = getHighMinusPrevLowFromBarCount(index, startIndex);
        Num atr = getTrueRangeFromBarCount(index, startIndex);

        return highMinusLowPrev.dividedBy(atr).abs();
    }

    /**
     * VI-.
     *
     * @param index the bar index
     *
     * @return the VI- value
     */
    public Num getViMinus(int index) {
        int startIndex = getStartIndex(index);
        Num lowMinusHighPrev = getLowMinusPrevHighFromBarCount(index, startIndex);
        Num atr = getTrueRangeFromBarCount(index, startIndex);

        return lowMinusHighPrev.dividedBy(atr).abs();
    }

    @Override
    public int getCountOfUnstableBars() {
        return barCount;
    }
}
