package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

/**
 * WaveTrend D indicator by LazyBear.
 * <a href="https://www.tradingview.com/script/2KE8wTuF-Indicator-WaveTrend-Oscillator-WT/">TradingView</a>
 */
public class WaveTrendDIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> indicator;

	public WaveTrendDIndicator(WaveTrendKIndicator k) {
		super(k.getBarSeries());

		this.indicator = new SMAIndicator(k, 4);
	}

	@Override
	public Num getValue(int index) {
		return indicator.getValue(index);
	}

	@Override
	public int getUnstableBars() {
		return 4;
	}
}
