package org.ta4j.core.indicators.gchannel;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * G-Channel Trend Detection indicator by jaggedsoft.
 * <a href="https://www.tradingview.com/script/smADlDdP-G-Channel-Trend-Detection/">TradingView</a>
 */
public class GChannelUpperBandIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> indicator;
	private final Num barCount;
	private GChannelLowerBandIndicator lowerBandIndicator;

	GChannelUpperBandIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator);
		this.indicator = indicator;
		this.barCount = numOf(barCount);
	}

	void setLowerBandIndicator(final GChannelLowerBandIndicator lowerBandIndicator) {
		this.lowerBandIndicator = lowerBandIndicator;
	}

	boolean breakout(int index) {
		if (index == 0) {
			return false;
		}

		Num prevClosePrice = getBarSeries().getBar(index - 1).getClosePrice();
		Num closePrice = getBarSeries().getBar(index).getClosePrice();
		Num prevValue = getValue(index - 1);
		Num value = getValue(index);

		return prevValue.isLessThan(prevClosePrice) && value.isGreaterThan(closePrice);
	}

	@Override
	protected Num calculate(int index) {
		if (index == 0) {
			return numOf(0);
		}

		Num prevValueUpper = getValue(index - 1);
		Num prevValueLower = lowerBandIndicator.getValue(index - 1);

		return indicator.getValue(index).max(prevValueUpper).minus(prevValueUpper.minus(prevValueLower).dividedBy(barCount));
	}
}
