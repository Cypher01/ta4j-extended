package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.num.Num;

/**
 * Volume adjusted weighted moving average (VAWMA) indicator by Electrified.
 * <a href="https://de.tradingview.com/script/MDNfJENm-Moving-Average-Weighted-Volume-Adjusted/">TradingView</a>
 */
public class VAWMAIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> indicator;
	private final Indicator<Num> volumeIndicator;
	private final int barCount;

	public VAWMAIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator);
		this.indicator = indicator;
		this.volumeIndicator = new VolumeIndicator(getBarSeries());
		this.barCount = barCount;
	}

	// TODO: This implementation could maybe be improved by using CombineIndicator or TransformIndicator or whatever other helper of ta4j.
	@Override
	protected Num calculate(int index) {
		if (index == 0) {
			return indicator.getValue(0);
		}

		Num sum = numOf(0);
		Num vol = numOf(0);
		int weight = 1;

		for (int i = Math.max(1, index - barCount + 1); i <= index; i++) {
			Num weightedVolume = volumeIndicator.getValue(i).multipliedBy(numOf(weight));
			vol = vol.plus(weightedVolume);
			sum = sum.plus(indicator.getValue(i).multipliedBy(weightedVolume));
			weight++;
		}

		return sum.dividedBy(vol);
	}
}
