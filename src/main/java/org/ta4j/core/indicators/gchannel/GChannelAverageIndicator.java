package org.ta4j.core.indicators.gchannel;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * G-Channel Trend Detection indicator by jaggedsoft.
 * <a href="https://www.tradingview.com/script/smADlDdP-G-Channel-Trend-Detection/">TradingView</a>
 */
public class GChannelAverageIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> averageIndicator;

	GChannelAverageIndicator(GChannelUpperBandIndicator upperBandIndicator, GChannelLowerBandIndicator lowerBandIndicator) {
		super(upperBandIndicator.getBarSeries());
		this.averageIndicator = TransformIndicator.divide(CombineIndicator.plus(upperBandIndicator, lowerBandIndicator), 2);
	}

	public boolean bullish(int index) {
		Num value = getValue(index);

		for (int i = index; i > 0; i--) {
			Num prevValue = getValue(i);

			if (prevValue.isGreaterThan(value)) {
				return false;
			} else if (prevValue.isLessThan(value)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Num getValue(int index) {
		return averageIndicator.getValue(index);
	}
}
