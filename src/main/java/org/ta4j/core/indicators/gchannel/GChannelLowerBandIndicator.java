package org.ta4j.core.indicators.gchannel;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * G-Channel Trend Detection indicator by jaggedsoft.
 * <a href="https://www.tradingview.com/script/smADlDdP-G-Channel-Trend-Detection/">TradingView</a>
 */
public class GChannelLowerBandIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> indicator;
	private final int barCount;
	private GChannelUpperBandIndicator upperBandIndicator;

	GChannelLowerBandIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator);
		this.indicator = indicator;
		this.barCount = barCount;
	}

	void setUpperBandIndicator(final GChannelUpperBandIndicator upperBandIndicator) {
		this.upperBandIndicator = upperBandIndicator;
	}

	@Override
	protected Num calculate(int index) {
		if (index == 0) {
			return getBarSeries().numFactory().zero();
		}

		Num prevValueLower = getValue(index - 1);
		Num prevValueUpper = upperBandIndicator.getValue(index - 1);

		return indicator.getValue(index).min(prevValueLower).plus(prevValueUpper.minus(prevValueLower).dividedBy(getBarSeries().numFactory().numOf(barCount)));
	}

	@Override
	public int getUnstableBars() {
		return barCount;
	}
}
