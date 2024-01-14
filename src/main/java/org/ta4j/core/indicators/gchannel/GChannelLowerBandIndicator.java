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
	private final Num barCount;
	private GChannelUpperBandIndicator upperBandIndicator;

	GChannelLowerBandIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator);
		this.indicator = indicator;
		this.barCount = numOf(barCount);
	}

	void setUpperBandIndicator(final GChannelUpperBandIndicator upperBandIndicator) {
		this.upperBandIndicator = upperBandIndicator;
	}

	boolean breakout(int index) {
		if (index == 0) {
			return false;
		}

		Num prevClosePrice = getBarSeries().getBar(index - 1).getClosePrice();
		Num closePrice = getBarSeries().getBar(index).getClosePrice();
		Num prevValue = getValue(index - 1);
		Num value = getValue(index);

		return prevValue.isGreaterThan(prevClosePrice) && value.isLessThan(closePrice);
	}

	@Override
	protected Num calculate(int index) {
		if (index == 0) {
			return numOf(0);
		}

		Num prevValueLower = getValue(index - 1);
		Num prevValueUpper = upperBandIndicator.getValue(index - 1);

		return indicator.getValue(index).min(prevValueLower).plus(prevValueUpper.minus(prevValueLower).dividedBy(barCount));
	}
}
