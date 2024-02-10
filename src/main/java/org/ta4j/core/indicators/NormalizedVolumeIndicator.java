package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.num.Num;

/**
 * Normalized volume indicator by ceyhun.
 * <a href="https://www.tradingview.com/script/pRBnipYB/">TradingView</a>
 */
public class NormalizedVolumeIndicator extends AbstractIndicator<Num> {
	private final VolumeIndicator volumeIndicator;
	private final SMAIndicator smaIndicator;

	public NormalizedVolumeIndicator(BarSeries series, int barCount) {
		this(new VolumeIndicator(series), barCount);
	}

	public NormalizedVolumeIndicator(VolumeIndicator volumeIndicator, int barCount) {
		super(volumeIndicator.getBarSeries());

		this.volumeIndicator = volumeIndicator;
		this.smaIndicator = new SMAIndicator(volumeIndicator, barCount);
	}

	@Override
	public Num getValue(int index) {
		return volumeIndicator.getValue(index).dividedBy(smaIndicator.getValue(index)).multipliedBy(numOf(100));
	}
}
