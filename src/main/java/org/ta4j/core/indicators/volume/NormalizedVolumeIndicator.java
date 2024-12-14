package org.ta4j.core.indicators.volume;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.num.Num;

/**
 * Normalized volume indicator by ceyhun.
 * <a href="https://www.tradingview.com/script/pRBnipYB/">TradingView</a>
 */
public class NormalizedVolumeIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> volumeIndicator;
    private final Indicator<Num> smaIndicator;

    public NormalizedVolumeIndicator(BarSeries series, int barCount) {
        this(new VolumeIndicator(series), barCount);
    }

    public NormalizedVolumeIndicator(VolumeIndicator volumeIndicator, int barCount) {
        super(volumeIndicator.getBarSeries());

        this.volumeIndicator = volumeIndicator;
        this.smaIndicator = new SMAIndicator(volumeIndicator, barCount);
    }

    @Override public Num getValue(int index) {
        return volumeIndicator.getValue(index).dividedBy(smaIndicator.getValue(index)).multipliedBy(
                getBarSeries().numFactory().hundred());
    }

    @Override public int getCountOfUnstableBars() {
        return smaIndicator.getCountOfUnstableBars();
    }
}
