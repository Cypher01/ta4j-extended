package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.indicators.numeric.BinaryOperationIndicator;
import org.ta4j.core.num.Num;

/**
 * Volume weighted moving average (VWMA) indicator, based on PineScript v5 Reference Manual.
 * <a href="https://www.tradingview.com/pine-script-reference/v5/#fun_ta.vwma">TradingView</a>
 */
public class VWMAIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> vwmaIndicator;
    private final int barCount;

    public VWMAIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator.getBarSeries());
        VolumeIndicator volumeIndicator = new VolumeIndicator(indicator.getBarSeries());
        Indicator<Num> inputTimesVolume = BinaryOperationIndicator.product(indicator, volumeIndicator);
        this.vwmaIndicator = BinaryOperationIndicator.quotient(new SMAIndicator(inputTimesVolume, barCount),
                new SMAIndicator(volumeIndicator, barCount));
        this.barCount = barCount;
    }

    @Override
    public Num getValue(int index) {
        return vwmaIndicator.getValue(index);
    }

    @Override
    public int getCountOfUnstableBars() {
        return barCount;
    }
}
